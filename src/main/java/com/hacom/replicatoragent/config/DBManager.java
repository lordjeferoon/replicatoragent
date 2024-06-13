package com.hacom.replicatoragent.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hacom.replicatoragent.model.PWSAudit;
import com.hacom.replicatoragent.repository.PWSAuditRepo;
import com.hacom.replicatoragent.util.ReplicatorObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import lombok.extern.log4j.Log4j2;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.rocksdb.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Log4j2
@Component
public class DBManager {

    private final MongoTemplate mongoReplicTemplate;
    private final String rocksDbPath;
    private final PWSAuditRepo audits;

    private RocksDB rocksDB;

    @Value("${info.application.short-name}")
    String module;

    @Value("${info.application.datacenter}")
    String datacenter;

    @Value("${customconfig.utcZoneId}")
    String utcZoneId;

    public DBManager(MongoTemplate mongoReplicaTemplate, @Value("${rocksdb.path}") String rocksDbPath, PWSAuditRepo audits) {
        this.mongoReplicTemplate = mongoReplicaTemplate;
        this.rocksDbPath = rocksDbPath;
        this.audits = audits;
    }

    @PostConstruct
    private synchronized void initRocksDB() throws RocksDBException {
        //Options options = new Options().setCreateIfMissing(true);
        //rocksDB = RocksDB.open(options, rocksDbPath);

        RocksDB.loadLibrary();
        Options options = new Options().setCreateIfMissing(true);

        RocksDB db = null;
        try {
            db = RocksDB.open(options, rocksDbPath);
            System.out.println("Conexión exitosa a RocksDB");
            this.rocksDB = db;
            // Agregar programación para reintento de operaciones pendientes
            scheduleRetry();
        } catch (RocksDBException e) {
            System.out.println("Error al abrir RocksDB");
            e.printStackTrace();
        }
    }

    @PreDestroy
    private synchronized void closeRocksDB() {
        if (rocksDB != null) {
            rocksDB.close();
            log.info("RocksDB cerrada correctamente");
        }
    }

    public void insertDocument(ReplicatorObject replicatorObject) {
        performOperation(replicatorObject, Operation.INSERT);
    }

    public void updateDocument(ReplicatorObject replicatorObject) {
        performOperation(replicatorObject, Operation.UPDATE);
    }

    public void deleteDocument(ReplicatorObject replicatorObject) {
        performOperation(replicatorObject, Operation.DELETE);
    }

    private void performOperation(ReplicatorObject replicatorObject, Operation operation) {
        Object document = replicatorObject.getDocument();
        String collection = replicatorObject.getCollection();
        String id = extractId(document);
        if (id != null) {
            try {
                switch (operation) {
                    case INSERT:
                        performInsert(document, collection, id);
                        break;
                    case UPDATE:
                        performUpdate(document, collection, id);
                        break;
                    case DELETE:
                        performDelete(collection, id);
                        break;
                }
            } catch (Exception e) {
                saveToRocksDB(replicatorObject);
            }
        } else {
            throw new IllegalArgumentException("Document must have an 'id' field");
        }
    }

    private void performInsert(Object document, String collection, String id) {
        Update update = createUpdate(document);
        mongoReplicTemplate.upsert(Query.query(Criteria.where("_id").is(id)), update, collection);
    }

    private void performUpdate(Object document, String collection, String id) {
        Query query = Query.query(Criteria.where("_id").is(id));
        if (mongoReplicTemplate.exists(query, collection)) {
            Update update = createUpdate(document);
            mongoReplicTemplate.upsert(query, update, collection);
        } else {
            throw new IllegalArgumentException("Document with id " + id + " does not exist");
        }
    }

    private void performDelete(String collection, String id) {
        Query query = Query.query(Criteria.where("_id").is(id));
        if (mongoReplicTemplate.exists(query, collection)) {
            mongoReplicTemplate.remove(query, collection);
        } else {
            throw new IllegalArgumentException("Document with id " + id + " does not exist");
        }
    }

    private Update createUpdate(Object document) {
        Map<String, Object> documentMap = (Map<String, Object>) document;
        documentMap.remove("id");
        Update update = new Update();
        documentMap.forEach(update::set);
        return update;
    }

    private String extractId(Object document) {
        if (document instanceof Map) {
            Map<String, Object> documentMap = (Map<String, Object>) document;
            return (String) documentMap.get("id");
        }
        return null;
    }

    private void saveToRocksDB(ReplicatorObject replicatorObject) {
        try {
            byte[] key = replicatorObject.getOperation().getBytes(StandardCharsets.UTF_8);
            byte[] value = serialize(replicatorObject);
            rocksDB.put(key, value);
            log.info("ReplicatorObject para la Colección: {} con Operación: {}. agregado a RocksDB", replicatorObject.getCollection(), replicatorObject.getOperation());
        } catch (Exception e) {
            log.info("ReplicatorObject para la Colección: {} con Operación: {}. no pudo ser agregado a RocksDB", replicatorObject.getCollection(), replicatorObject.getOperation());
            e.printStackTrace();
        }
    }

    public byte[] serialize(ReplicatorObject replicatorObject) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsBytes(replicatorObject);
        } catch (JsonProcessingException e) {
            System.err.println("Error al serializar el objeto ReplicatorObject a JSON:");
            e.printStackTrace();
            return null;
        }
    }


    private void scheduleRetry() {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(this::retryOperations, 0, 5, TimeUnit.MINUTES);
    }

    private void retryOperations() {
        try {
            log.info("===================== INICIÓ TAREA DE ACTUALIZACIÓN ROCKS DB =====================");
            RocksIterator iterator = rocksDB.newIterator();
            for (iterator.seekToFirst(); iterator.isValid(); iterator.next()) {
                byte[] key = iterator.key();
                byte[] value = iterator.value();
                String operation = new String(key, StandardCharsets.UTF_8);
                ReplicatorObject replicatorObject = deserialize(value);
                log.info("Reintento de envío de ReplicatorObject para la Colección: {} con Operación: {}.", replicatorObject.getCollection(), replicatorObject.getOperation());
                performOperation(replicatorObject, operation.equals("insert") ?  Operation.INSERT : operation.equals("update") ? Operation.UPDATE : Operation.DELETE );
                rocksDB.delete(key);
            }
            log.info("==================== FINALIZÓ TAREA DE ACTUALIZACIÓN ROCKS DB ====================");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ReplicatorObject deserialize(byte[] data) {
        log.info(new String(data, StandardCharsets.UTF_8));
        return ReplicatorObject.fromString(new String(data, StandardCharsets.UTF_8));
    }

    private enum Operation {
        INSERT, UPDATE, DELETE
    }
}