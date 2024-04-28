package com.hacom.replicatoragent.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.*;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import com.mongodb.client.model.changestream.OperationType;
import com.mongodb.client.result.DeleteResult;

import java.util.Arrays;

import org.bson.BsonDocument;
import org.bson.BsonObjectId;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.rocksdb.RocksIterator;
import java.util.HashMap;
import java.util.Map;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

@RestController
@RequestMapping("/public")
public class PublicController {
	
	// Configuración de RocksDB
    RocksDB rocksDB = null;
    
    MongoDatabase database = null;
    MongoDatabase database_r = null;
    
    public PublicController() {
    	//MongoDB
    	this.database = conexion_mongodb("pwsuser","pwsuser","pwsalertsystem","192.168.5.128",27017);
        this.database_r = conexion_mongodb("pwsuserv2","pwsuserv2","pwsalertsystemv2","192.168.5.128",27017);
    	
    	//RocksDB
    	RocksDB.loadLibrary();
        Options options = new Options().setCreateIfMissing(true);
        
        try {
            this.rocksDB = RocksDB.open(options, "rocksdb_data");
            System.out.println("Conexión exitosa");
        } catch (RocksDBException e) {
        	System.out.println("Error");
            e.printStackTrace();
        }
    }

    @GetMapping(value = "/status")
    public Mono<ResponseEntity<String>> getStatus() {
        return Mono.just(ResponseEntity.ok("OK"));
    }
    
    public MongoDatabase conexion_mongodb(String username, String password, String databaseName, String ip,int puerto) {
    	//Configuración MongoDB - BD Principal
    	MongoCredential credential = MongoCredential.createCredential(username, databaseName, password.toCharArray());
    	MongoClientSettings settings = MongoClientSettings.builder()
    	        .credential(credential)
    	        .applyToClusterSettings(builder ->
    	                builder.hosts(Arrays.asList(new ServerAddress(ip, puerto))))
    	        .build();
    	
    	MongoClient mongoClient = MongoClients.create(settings);
    	MongoDatabase database = mongoClient.getDatabase(databaseName);
    	
    	return database;
    }
    
    @GetMapping(value = "/follow_collection_PWSAccount")
    public void collection_PWSAccount() throws JsonMappingException, JsonProcessingException {
    	MongoCollection<Document> collection = this.database.getCollection("PWSAccount");
    	MongoCollection<Document> collection_r = this.database_r.getCollection("PWSAccount");
    	
    	MongoCursor<ChangeStreamDocument<Document>> cursor = collection.watch().iterator();
    	
    	while (cursor.hasNext()) {
    		call_event(cursor,collection,collection_r);
    	}
    }
    
    @GetMapping(value = "/follow_collection_PWSAccountCBoundary01")
    public void collection_PWSAccountCBoundary01x() throws JsonMappingException, JsonProcessingException {
    	MongoCollection<Document> collection = this.database.getCollection("PWSAccountCBoundary01");
    	MongoCollection<Document> collection_r = this.database_r.getCollection("PWSAccountCBoundary01");
    	
    	MongoCursor<ChangeStreamDocument<Document>> cursor = collection.watch().iterator();
    	
    	while (cursor.hasNext()) {
    		call_event(cursor,collection,collection_r);
    	}
    }
    
    @GetMapping(value = "/follow_collection_PWSAccountState")
    public void collection_PWSAccountState() throws JsonMappingException, JsonProcessingException {
    	MongoCollection<Document> collection = this.database.getCollection("PWSAccountState");
    	MongoCollection<Document> collection_r = this.database_r.getCollection("PWSAccountState");
    	
    	MongoCursor<ChangeStreamDocument<Document>> cursor = collection.watch().iterator();
    	
    	while (cursor.hasNext()) {
    		call_event(cursor,collection,collection_r);
    	}
    }
    
    @GetMapping(value = "/follow_collection_PWSAlert")
    public void collection_PWSAlert() throws JsonMappingException, JsonProcessingException {
    	MongoCollection<Document> collection = this.database.getCollection("PWSAlert");
    	MongoCollection<Document> collection_r = this.database_r.getCollection("PWSAlert");
    	
    	MongoCursor<ChangeStreamDocument<Document>> cursor = collection.watch().iterator();
    	
    	while (cursor.hasNext()) {
    		call_event(cursor,collection,collection_r);
    	}
    }
    
    @GetMapping(value = "/follow_collection_PWSAlertBroadcastList")
    public void collection_PWSAlertBroadcastList() throws JsonMappingException, JsonProcessingException {
    	MongoCollection<Document> collection = this.database.getCollection("PWSAlertBroadcastList");
    	MongoCollection<Document> collection_r = this.database_r.getCollection("PWSAlertBroadcastList");
    	
    	MongoCursor<ChangeStreamDocument<Document>> cursor = collection.watch().iterator();
    	
    	while (cursor.hasNext()) {
    		call_event(cursor,collection,collection_r);
    	}
    }
    
    @GetMapping(value = "/follow_collection_PWSAlertCategory")
    public void collection_PWSAlertCategory() throws JsonMappingException, JsonProcessingException {
    	MongoCollection<Document> collection = this.database.getCollection("PWSAlertCategory");
    	MongoCollection<Document> collection_r = this.database_r.getCollection("PWSAlertCategory");
    	
    	MongoCursor<ChangeStreamDocument<Document>> cursor = collection.watch().iterator();
    	
    	while (cursor.hasNext()) {
    		call_event(cursor,collection,collection_r);
    	}
    }
    
    @GetMapping(value = "/follow_collection_PWSAlertDescription")
    public void collection_PWSAlertDescription() throws JsonMappingException, JsonProcessingException {
    	MongoCollection<Document> collection = this.database.getCollection("PWSAlertDescription");
    	MongoCollection<Document> collection_r = this.database_r.getCollection("PWSAlertDescription");
    	
    	MongoCursor<ChangeStreamDocument<Document>> cursor = collection.watch().iterator();
    	
    	while (cursor.hasNext()) {
    		call_event(cursor,collection,collection_r);
    	}
    }
    
    @GetMapping(value = "/follow_collection_PWSAlertInstruction")
    public void collection_PWSAlertInstruction() throws JsonMappingException, JsonProcessingException {
    	MongoCollection<Document> collection = this.database.getCollection("PWSAlertInstruction");
    	MongoCollection<Document> collection_r = this.database_r.getCollection("PWSAlertInstruction");
    	
    	MongoCursor<ChangeStreamDocument<Document>> cursor = collection.watch().iterator();
    	
    	while (cursor.hasNext()) {
    		call_event(cursor,collection,collection_r);
    	}
    }
    
    @GetMapping(value = "/follow_collection_PWSAlertReport")
    public void collection_PWSAlertReport() throws JsonMappingException, JsonProcessingException {
    	MongoCollection<Document> collection = this.database.getCollection("PWSAlertReport");
    	MongoCollection<Document> collection_r = this.database_r.getCollection("PWSAlertReport");
    	
    	MongoCursor<ChangeStreamDocument<Document>> cursor = collection.watch().iterator();
    	
    	while (cursor.hasNext()) {
    		call_event(cursor,collection,collection_r);
    	}
    }
    
    @GetMapping(value = "/follow_collection_PWSAlertStatus")
    public void collection_PWSAlertStatus() throws JsonMappingException, JsonProcessingException {
    	MongoCollection<Document> collection = this.database.getCollection("PWSAlertStatus");
    	MongoCollection<Document> collection_r = this.database_r.getCollection("PWSAlertStatus");
    	
    	MongoCursor<ChangeStreamDocument<Document>> cursor = collection.watch().iterator();
    	
    	while (cursor.hasNext()) {
    		call_event(cursor,collection,collection_r);
    	}
    }
    
    @GetMapping(value = "/follow_collection_PWSAlertStatusGroupedLocation")
    public void collection_PWSAlertStatusGroupedLocation() throws JsonMappingException, JsonProcessingException {
    	MongoCollection<Document> collection = this.database.getCollection("PWSAlertStatusGroupedLocation");
    	MongoCollection<Document> collection_r = this.database_r.getCollection("PWSAlertStatusGroupedLocation");
    	
    	MongoCursor<ChangeStreamDocument<Document>> cursor = collection.watch().iterator();
    	
    	while (cursor.hasNext()) {
    		call_event(cursor,collection,collection_r);
    	}
    }
    
    @GetMapping(value = "/follow_collection_PWSAlertStatusOLD")
    public void collection_PWSAlertStatusOLD() throws JsonMappingException, JsonProcessingException {
    	MongoCollection<Document> collection = this.database.getCollection("PWSAlertStatusOLD");
    	MongoCollection<Document> collection_r = this.database_r.getCollection("PWSAlertStatusOLD");
    	
    	MongoCursor<ChangeStreamDocument<Document>> cursor = collection.watch().iterator();
    	
    	while (cursor.hasNext()) {
    		call_event(cursor,collection,collection_r);
    	}
    }
    
    @GetMapping(value = "/follow_collection_PWSAudit")
    public void collection_PWSAudit() throws JsonMappingException, JsonProcessingException {
    	MongoCollection<Document> collection = this.database.getCollection("PWSAudit");
    	MongoCollection<Document> collection_r = this.database_r.getCollection("PWSAudit");
    	
    	MongoCursor<ChangeStreamDocument<Document>> cursor = collection.watch().iterator();
    	
    	while (cursor.hasNext()) {
    		call_event(cursor,collection,collection_r);
    	}
    }
    
    @GetMapping(value = "/follow_collection_PWSBroadcastCenter")
    public void collection_PWSBroadcastCenter() throws JsonMappingException, JsonProcessingException {
    	MongoCollection<Document> collection = this.database.getCollection("PWSBroadcastCenter");
    	MongoCollection<Document> collection_r = this.database_r.getCollection("PWSBroadcastCenter");
    	
    	MongoCursor<ChangeStreamDocument<Document>> cursor = collection.watch().iterator();
    	
    	while (cursor.hasNext()) {
    		call_event(cursor,collection,collection_r);
    	}
    }
    
    @GetMapping(value = "/follow_collection_PWSBroadcastElement")
    public void collection_PWSBroadcastElement() throws JsonMappingException, JsonProcessingException {
    	MongoCollection<Document> collection = this.database.getCollection("PWSBroadcastElement");
    	MongoCollection<Document> collection_r = this.database_r.getCollection("PWSBroadcastElement");
    	
    	MongoCursor<ChangeStreamDocument<Document>> cursor = collection.watch().iterator();
    	
    	while (cursor.hasNext()) {
    		call_event(cursor,collection,collection_r);
    	}
    }
    
    @GetMapping(value = "/follow_collection_PWSCategoryEvent")
    public void collection_PWSCategoryEvent() throws JsonMappingException, JsonProcessingException {
    	MongoCollection<Document> collection = this.database.getCollection("PWSCategoryEvent");
    	MongoCollection<Document> collection_r = this.database_r.getCollection("PWSCategoryEvent");
    	
    	MongoCursor<ChangeStreamDocument<Document>> cursor = collection.watch().iterator();
    	
    	while (cursor.hasNext()) {
    		call_event(cursor,collection,collection_r);
    	}
    }
    
    @GetMapping(value = "/follow_collection_PWSCBoundary01")
    public void collection_PWSCBoundary01() throws JsonMappingException, JsonProcessingException {
    	MongoCollection<Document> collection = this.database.getCollection("PWSCBoundary01");
    	MongoCollection<Document> collection_r = this.database_r.getCollection("PWSCBoundary01");
    	
    	MongoCursor<ChangeStreamDocument<Document>> cursor = collection.watch().iterator();
    	
    	while (cursor.hasNext()) {
    		call_event(cursor,collection,collection_r);
    	}
    }
    
    @GetMapping(value = "/follow_collection_PWSCustomConfig")
    public void collection_PWSCustomConfig() throws JsonMappingException, JsonProcessingException {
    	MongoCollection<Document> collection = this.database.getCollection("PWSCustomConfig");
    	MongoCollection<Document> collection_r = this.database_r.getCollection("PWSCustomConfig");
    	
    	MongoCursor<ChangeStreamDocument<Document>> cursor = collection.watch().iterator();
    	
    	while (cursor.hasNext()) {
    		call_event(cursor,collection,collection_r);
    	}
    }
    
    @GetMapping(value = "/follow_collection_PWSEvent")
    public void collection_PWSEvent() throws JsonMappingException, JsonProcessingException {
    	MongoCollection<Document> collection = this.database.getCollection("PWSEvent");
    	MongoCollection<Document> collection_r = this.database_r.getCollection("PWSEvent");
    	
    	MongoCursor<ChangeStreamDocument<Document>> cursor = collection.watch().iterator();
    	
    	while (cursor.hasNext()) {
    		call_event(cursor,collection,collection_r);
    	}
    }
    
    @GetMapping(value = "/follow_collection_PWSEventAlert")
    public void collection_PWSEventAlert() throws JsonMappingException, JsonProcessingException {
    	MongoCollection<Document> collection = this.database.getCollection("PWSEventAlert");
    	MongoCollection<Document> collection_r = this.database_r.getCollection("PWSEventAlert");
    	
    	MongoCursor<ChangeStreamDocument<Document>> cursor = collection.watch().iterator();
    	
    	while (cursor.hasNext()) {
    		call_event(cursor,collection,collection_r);
    	}
    }
    
    @GetMapping(value = "/follow_collection_PWSEventSeverity")
    public void collection_PWSEventSeverity() throws JsonMappingException, JsonProcessingException {
    	MongoCollection<Document> collection = this.database.getCollection("PWSEventSeverity");
    	MongoCollection<Document> collection_r = this.database_r.getCollection("PWSEventSeverity");
    	
    	MongoCursor<ChangeStreamDocument<Document>> cursor = collection.watch().iterator();
    	
    	while (cursor.hasNext()) {
    		call_event(cursor,collection,collection_r);
    	}
    }
    
    @GetMapping(value = "/follow_collection_PWSNotificationGroup")
    public void collection_PWSNotificationGroup() throws JsonMappingException, JsonProcessingException {
    	MongoCollection<Document> collection = this.database.getCollection("PWSNotificationGroup");
    	MongoCollection<Document> collection_r = this.database_r.getCollection("PWSNotificationGroup");
    	
    	MongoCursor<ChangeStreamDocument<Document>> cursor = collection.watch().iterator();
    	
    	while (cursor.hasNext()) {
    		call_event(cursor,collection,collection_r);
    	}
    }
    
    @GetMapping(value = "/follow_collection_PWSNotificationList")
    public void collection_PWSNotificationList() throws JsonMappingException, JsonProcessingException {
    	MongoCollection<Document> collection = this.database.getCollection("PWSNotificationList");
    	MongoCollection<Document> collection_r = this.database_r.getCollection("PWSNotificationList");
    	
    	MongoCursor<ChangeStreamDocument<Document>> cursor = collection.watch().iterator();
    	
    	while (cursor.hasNext()) {
    		call_event(cursor,collection,collection_r);
    	}
    }
    
    @GetMapping(value = "/follow_collection_PWSOperator")
    public void collection_PWSOperator() throws JsonMappingException, JsonProcessingException {
    	MongoCollection<Document> collection = this.database.getCollection("PWSOperator");
    	MongoCollection<Document> collection_r = this.database_r.getCollection("PWSOperator");
    	
    	MongoCursor<ChangeStreamDocument<Document>> cursor = collection.watch().iterator();
    	
    	while (cursor.hasNext()) {
    		call_event(cursor,collection,collection_r);
    	}
    }
    
    @GetMapping(value = "/follow_collection_PWSRefreshToken")
    public void collection_PWSRefreshToken() throws JsonMappingException, JsonProcessingException {
    	MongoCollection<Document> collection = this.database.getCollection("PWSRefreshToken");
    	MongoCollection<Document> collection_r = this.database_r.getCollection("PWSRefreshToken");
    	
    	MongoCursor<ChangeStreamDocument<Document>> cursor = collection.watch().iterator();
    	
    	while (cursor.hasNext()) {
    		call_event(cursor,collection,collection_r);
    	}
    }
    
    @GetMapping(value = "/follow_collection_PWSRol")
    public void collection_PWSRol() throws JsonMappingException, JsonProcessingException {
    	MongoCollection<Document> collection = this.database.getCollection("PWSRol");
    	MongoCollection<Document> collection_r = this.database_r.getCollection("PWSRol");
    	
    	MongoCursor<ChangeStreamDocument<Document>> cursor = collection.watch().iterator();
    	
    	while (cursor.hasNext()) {
    		call_event(cursor,collection,collection_r);
    	}
    }
    
    @GetMapping(value = "/follow_collection_PWSSenderIn")
    public void collection_PWSSenderIn() throws JsonMappingException, JsonProcessingException {
    	MongoCollection<Document> collection = this.database.getCollection("PWSSenderIn");
    	MongoCollection<Document> collection_r = this.database_r.getCollection("PWSSenderIn");
    	
    	MongoCursor<ChangeStreamDocument<Document>> cursor = collection.watch().iterator();
    	
    	while (cursor.hasNext()) {
    		call_event(cursor,collection,collection_r);
    	}
    }
    
    @GetMapping(value = "/follow_collection_PWSSenderInEndpoint")
    public void collection_PWSSenderInEndpoint() throws JsonMappingException, JsonProcessingException {
    	MongoCollection<Document> collection = this.database.getCollection("PWSSenderInEndpoint");
    	MongoCollection<Document> collection_r = this.database_r.getCollection("PWSSenderInEndpoint");
    	
    	MongoCursor<ChangeStreamDocument<Document>> cursor = collection.watch().iterator();
    	
    	while (cursor.hasNext()) {
    		call_event(cursor,collection,collection_r);
    	}
    }
    
    @GetMapping(value = "/follow_collection_PWSSenderUser")
    public void collection_PWSSenderUser() throws JsonMappingException, JsonProcessingException {
    	MongoCollection<Document> collection = this.database.getCollection("PWSSenderUser");
    	MongoCollection<Document> collection_r = this.database_r.getCollection("PWSSenderUser");
    	
    	MongoCursor<ChangeStreamDocument<Document>> cursor = collection.watch().iterator();
    	
    	while (cursor.hasNext()) {
    		call_event(cursor,collection,collection_r);
    	}
    }
    
    @GetMapping(value = "/follow_collection_PWSState")
    public void collection_PWSState() throws JsonMappingException, JsonProcessingException {
    	MongoCollection<Document> collection = this.database.getCollection("PWSState");
    	MongoCollection<Document> collection_r = this.database_r.getCollection("PWSState");
    	
    	MongoCursor<ChangeStreamDocument<Document>> cursor = collection.watch().iterator();
    	
    	while (cursor.hasNext()) {
    		call_event(cursor,collection,collection_r);
    	}
    }
    
    @GetMapping(value = "/follow_collection_PWSTemplateAlertCategoryEvents")
    public void collection_PWSTemplateAlertCategoryEvents() throws JsonMappingException, JsonProcessingException {
    	MongoCollection<Document> collection = this.database.getCollection("PWSTemplateAlertCategoryEvents");
    	MongoCollection<Document> collection_r = this.database_r.getCollection("PWSTemplateAlertCategoryEvents");
    	
    	MongoCursor<ChangeStreamDocument<Document>> cursor = collection.watch().iterator();
    	
    	while (cursor.hasNext()) {
    		call_event(cursor,collection,collection_r);
    	}
    }
    
    @GetMapping(value = "/follow_collection_PWSTemplateCAP")
    public void collection_PWSTemplateCAP() throws JsonMappingException, JsonProcessingException {
    	MongoCollection<Document> collection = this.database.getCollection("PWSTemplateCAP");
    	MongoCollection<Document> collection_r = this.database_r.getCollection("PWSTemplateCAP");
    	
    	MongoCursor<ChangeStreamDocument<Document>> cursor = collection.watch().iterator();
    	
    	while (cursor.hasNext()) {
    		call_event(cursor,collection,collection_r);
    	}
    }
    
    @GetMapping(value = "/follow_collection_PWSTransmissionConfig")
    public void collection_PWSTransmissionConfig() throws JsonMappingException, JsonProcessingException {
    	MongoCollection<Document> collection = this.database.getCollection("PWSTransmissionConfig");
    	MongoCollection<Document> collection_r = this.database_r.getCollection("PWSTransmissionConfig");
    	
    	MongoCursor<ChangeStreamDocument<Document>> cursor = collection.watch().iterator();
    	
    	while (cursor.hasNext()) {
    		call_event(cursor,collection,collection_r);
    	}
    }
    
    @GetMapping(value = "/follow_collection_PWSUser")
    public void collection_PWSUser() throws JsonMappingException, JsonProcessingException {
    	MongoCollection<Document> collection = this.database.getCollection("PWSUser");
    	MongoCollection<Document> collection_r = this.database_r.getCollection("PWSUser");
    	
    	MongoCursor<ChangeStreamDocument<Document>> cursor = collection.watch().iterator();
    	
    	while (cursor.hasNext()) {
    		call_event(cursor,collection,collection_r);
    	}
    }
    
    @GetMapping(value = "/follow_collection_PWSUserAccountCBoundary01")
    public void collection_PWSUserAccountCBoundary01() throws JsonMappingException, JsonProcessingException {
    	MongoCollection<Document> collection = this.database.getCollection("PWSUserAccountCBoundary01");
    	MongoCollection<Document> collection_r = this.database_r.getCollection("PWSUserAccountCBoundary01");
    	
    	MongoCursor<ChangeStreamDocument<Document>> cursor = collection.watch().iterator();
    	
    	while (cursor.hasNext()) {
    		call_event(cursor,collection,collection_r);
    	}
    }
    
    @GetMapping(value = "/follow_collection_PWSUserAccountState")
    public void collection_PWSUserAccountState() throws JsonMappingException, JsonProcessingException {
    	MongoCollection<Document> collection = this.database.getCollection("PWSUserAccountState");
    	MongoCollection<Document> collection_r = this.database_r.getCollection("PWSUserAccountState");
    	
    	MongoCursor<ChangeStreamDocument<Document>> cursor = collection.watch().iterator();
    	
    	while (cursor.hasNext()) {
    		call_event(cursor,collection,collection_r);
    	}
    }
    
    @GetMapping(value = "/follow_collection_PWSZipCodes")
    public void collection_PWSZipCodes() throws JsonMappingException, JsonProcessingException {
    	MongoCollection<Document> collection = this.database.getCollection("PWSZipCodes");
    	MongoCollection<Document> collection_r = this.database_r.getCollection("PWSZipCodes");
    	
    	MongoCursor<ChangeStreamDocument<Document>> cursor = collection.watch().iterator();
    	
    	while (cursor.hasNext()) {
    		call_event(cursor,collection,collection_r);
    	}
    }
    
    private void save_register( Map<String, Object> mapaDatos) {
    	BsonValue documentId = new BsonObjectId(new ObjectId());
        BsonObjectId objectId = documentId.asObjectId();
        String key = objectId.getValue().toHexString();
        
	    //System.out.println("Valor del ObjectId como cadena hexadecimal: " + key);
	    
        try {
        	//this.rocksDB.put(key.getBytes(), json.toString().getBytes());
        	this.rocksDB.put(key.getBytes(), serializar(mapaDatos));
        } catch (RocksDBException excep) {
        	excep.printStackTrace();
        }
    }
    
    private static byte[] serializar(Map<String, Object> objeto) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(objeto);
            out.flush();
            return bos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private static Map<String, Object> deserializar(byte[] bytes) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInputStream in = new ObjectInputStream(bis);
            Object objeto = in.readObject();
            return (Map<String, Object>) objeto;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private void call_event(MongoCursor<ChangeStreamDocument<Document>> cursor, MongoCollection<Document> collection, MongoCollection<Document> collection_r) {
    	//ObjectMapper objectMapper = new ObjectMapper();
	    //ObjectNode json = objectMapper.createObjectNode();
	    Map<String, Object> mapaDatos = new HashMap<>();
	    
	    ChangeStreamDocument<Document> changeStreamDocument = cursor.next();
	    System.out.println("Operación: " + changeStreamDocument.getOperationType());
	    System.out.println("Colección: " + changeStreamDocument.getNamespace().getCollectionName());
	    
	    mapaDatos.put("operation", changeStreamDocument.getOperationType().getValue().toString());
	    mapaDatos.put("collection", changeStreamDocument.getNamespace().getCollectionName().toString());
	    //json.put("operation", changeStreamDocument.getOperationType().getValue().toString());
	    //json.put("collection", changeStreamDocument.getNamespace().getCollectionName().toString());
	    
	    /*try {
        	json.put("id", changeStreamDocument.getDocumentKey().getObjectId("_id").getValue().toString());
        }catch(Exception e) {
        	json.put("id", changeStreamDocument.getDocumentKey().getString("_id").getValue());
        }*/
	    
	    if (changeStreamDocument.getOperationType() == OperationType.INSERT || changeStreamDocument.getOperationType() == OperationType.REPLACE) {
	        System.out.println("Documento insertado o reemplazado: " + changeStreamDocument.getFullDocument());
	        
	        //json.put("value", changeStreamDocument.getFullDocument().toJson());
	        mapaDatos.put("id", changeStreamDocument.getDocumentKey().getString("_id").getValue().toString());
	        mapaDatos.put("value", changeStreamDocument.getFullDocument());
	        
	        try {
	        	//Guardar evento
    	        collection_r.insertOne(changeStreamDocument.getFullDocument());
	        }catch(Exception e) {
	        	save_register(mapaDatos);
	        }
	    } else if (changeStreamDocument.getOperationType() == OperationType.UPDATE) {
	        System.out.println("ID del documento actualizado: " + changeStreamDocument.getDocumentKey());
	        System.out.println("Descripción de la actualización: " + changeStreamDocument.getUpdateDescription());
	        
	        mapaDatos.put("id", changeStreamDocument.getDocumentKey().getObjectId("_id").getValue().toString());

	        //json.put("value", changeStreamDocument.getUpdateDescription().getUpdatedFields().toJson());
	        mapaDatos.put("value", changeStreamDocument.getUpdateDescription().getUpdatedFields());
	        
	        try {
	        	//Guardar evento
    	        BsonDocument updateDocumentBson = changeStreamDocument.getUpdateDescription().getUpdatedFields();
    	        Document filter = new Document("_id", changeStreamDocument.getDocumentKey().get("_id"));
    	        
    	        Document document = new Document();
    	        for (String key : updateDocumentBson.keySet()) {
    	            BsonValue value = updateDocumentBson.get(key);
    	            document.append(key, value);
    	        }
    	        
    	        Document updateDocument = new Document("$set", document);
    	        collection_r.updateOne(filter,updateDocument);
	        }catch(Exception e) {
	        	save_register(mapaDatos);
	        }
	    } else if (changeStreamDocument.getOperationType() == OperationType.DELETE) {
	        System.out.println("ID del documento eliminado: " + changeStreamDocument.getDocumentKey());
	        //json.put("value", "");
	        mapaDatos.put("id", changeStreamDocument.getDocumentKey().getString("_id").getValue().toString());
	        mapaDatos.put("value", null);
	        
	        try {
	        	Document filter = new Document("_id", changeStreamDocument.getDocumentKey().get("_id"));
    	        DeleteResult deleteResult = collection_r.deleteOne(filter);
    	        
    	        if (deleteResult.getDeletedCount() > 0) {
    	            System.out.println("Se ha eliminado un documento correctamente.");
    	        } else {
    	            System.out.println("No se encontró ningún documento para eliminar.");
    	        }
	        }catch(Exception e) {
	        	save_register(mapaDatos);
	        }
	    }
    }
    
    @GetMapping(value = "/iterar")
    public void iterar() throws JsonMappingException, JsonProcessingException, RocksDBException {
		// Obtener un iterador sobre todas las entradas en RocksDB
		RocksIterator iterator = this.rocksDB.newIterator();
		
		// Convertir el String JSON a un ObjectNode
        //ObjectMapper objectMapper = new ObjectMapper();
		
		// Iterar sobre todas las entradas
		for (iterator.seekToFirst(); iterator.isValid(); iterator.next()) {
		    // Obtener la clave y el valor de la entrada actual
		    byte[] keyBytes = iterator.key();
		    byte[] valueBytes = iterator.value();
		    
		    Map<String, Object> changeStreamDocument = deserializar(valueBytes);
		    
		    // Convertir las claves y los valores de bytes a cadenas (o al formato que hayas utilizado al almacenarlos)
		    String key = new String(keyBytes);
		    //String value = new String(valueBytes);
		    
		    String operation = changeStreamDocument.get("operation").toString();
		    String name_collection = changeStreamDocument.get("collection").toString();
		    BsonValue _id = new BsonString(changeStreamDocument.get("id").toString());
		    
		    MongoCollection<Document> collection_r = this.database_r.getCollection(name_collection);
		    
		    if(operation.equals("insert")) {
		    	Document doc = (Document) changeStreamDocument.get("value");
		    	try {
		    		collection_r.insertOne(doc);
		    		this.rocksDB.delete(keyBytes);
		    	}catch(Exception e) {
		    		System.out.println("Hubo un error al hacer el INSERT del registro RocksDB: "+key);
		    	}
		    }else {
		    	if(operation.equals("update")) {
		    		try {
			    		BsonDocument updateDocumentBson = (BsonDocument) changeStreamDocument.get("value");
		    	        Document filter = new Document("_id", new ObjectId(changeStreamDocument.get("id").toString()));
		    	        Document document = new Document();
		    	        
		    	        for (String key_d : updateDocumentBson.keySet()) {
		    	            BsonValue value_d = updateDocumentBson.get(key_d);
		    	            document.append(key_d, value_d);
		    	        }
		    	        
		    	        Document updateDocument = new Document("$set", document);
		    	        collection_r.updateOne(filter,updateDocument);
		    	        this.rocksDB.delete(keyBytes);
			    	}catch(Exception e) {
			    		System.out.println("Hubo un error al hacer el UPDATE del registro RocksDB: "+key);
			    	}
		    	}else {
		    		try {
			    		Document filter = new Document("_id", _id);
		    	        DeleteResult deleteResult = collection_r.deleteOne(filter);
		    	        
		    	        if (deleteResult.getDeletedCount() > 0) {
		    	        	this.rocksDB.delete(keyBytes);
		    	        }else {
		    	        	System.out.println("No se encontraron registros con el Key: " + key);
		    	        }
			    	}catch(Exception e) {
			    		System.out.println("Hubo un error al hacer el DELETE del registro RocksDB: "+key);
			    	}
		    	}
		    }
		    
		    // Imprimir la clave y el valor
		    //System.out.println("Clave: " + key + ", Valor: " + value);
		}
    }

}