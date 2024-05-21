package com.hacom.replicatoragent.actor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.*;

import com.hacom.replicatoragent.model.PWSAudit;
import com.hacom.replicatoragent.repository.PWSAuditRepo;
import org.bson.BsonDocument;
import org.bson.BsonObjectId;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.rocksdb.RocksIterator;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import com.mongodb.client.model.changestream.OperationType;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.DuplicateKeyException;

import akka.actor.AbstractActor;
import akka.actor.Props;
import java.util.*;

public class MyActor extends AbstractActor {
	private PWSAuditRepo audits;

    String dataCenter = null;
	String module = null;
	String utcZoneId = null;
    RocksDB rocksDB = null;
    MongoDatabase database = null;
    MongoDatabase database_r = null;
    
    public MyActor(PWSAuditRepo audits, RocksDB rocksDB, MongoDatabase database, MongoDatabase database_r, String dataCenter, String module, String utcZoneId) {
		this.audits = audits;
		this.rocksDB = rocksDB;
        this.database = database;
        this.database_r = database_r;
        this.dataCenter = dataCenter;
		this.module = module;
		this.utcZoneId = utcZoneId;
		getContext().getSystem().log().info("Actor {} initialized with collection: {}", getSelf().path().name(), database);
    }
    
    public MyActor() {
        // Constructor sin argumentos
    }
	
	@Override
    public Receive createReceive() {
        return receiveBuilder()
                .matchAny(message -> {
                	System.out.println(message);
                	MongoCollection<Document> collection = this.database.getCollection(message.toString());
                	MongoCollection<Document> collection_r = this.database_r.getCollection(message.toString());
                	
                	MongoCursor<ChangeStreamDocument<Document>> cursor = collection.watch().iterator();
                	
                	while (cursor.hasNext()) {
                		call_event(cursor,collection,collection_r);
                	}
                })
                .build();
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
    
    private void call_event(MongoCursor<ChangeStreamDocument<Document>> cursor, MongoCollection<Document> collection, MongoCollection<Document> collection_r) throws RocksDBException {
	    Map<String, Object> mapaDatos = new HashMap<>();
	    
	    ChangeStreamDocument<Document> changeStreamDocument = cursor.next();
	    String name_collection = changeStreamDocument.getNamespace().getCollectionName();
	    System.out.println("Operación: " + changeStreamDocument.getOperationType());
	    System.out.println("Colección: " + name_collection.toString());
	    
	    mapaDatos.put("operation", changeStreamDocument.getOperationType().getValue().toString());
	    mapaDatos.put("collection", name_collection.toString());
	    
	    if (changeStreamDocument.getOperationType() == OperationType.INSERT || changeStreamDocument.getOperationType() == OperationType.REPLACE) {
	        mapaDatos.put("id", changeStreamDocument.getDocumentKey().getObjectId("_id").getValue().toString());
	        mapaDatos.put("value", changeStreamDocument.getFullDocument());
	        
	        /*if(name_collection.equals("PWSAlert") && this.dataCenter.equals("ADIP")) {
	        	mapaDatos.put("process", "regularization");
	        }*/
	        
        	System.out.println("Documento insertado o reemplazado: " + changeStreamDocument.getFullDocument());
	        try {
	        	//Guardar evento
    	        collection_r.insertOne(changeStreamDocument.getFullDocument());
	        }catch (DuplicateKeyException e) {
	        	System.out.println("Error: Clave duplicada al intentar insertar el documento con ID: "+ changeStreamDocument.getDocumentKey().getObjectId("_id").getValue().toString());
	        }catch(Exception e) {
	        	save_register(mapaDatos);
	        }
	    } else if (changeStreamDocument.getOperationType() == OperationType.UPDATE) {
	    	/*FindIterable<Document> documents = collection.find();
    		for (Document document : documents) {
    			collection_r.insertOne(document);
                System.out.println("Documento insertado en la base de datos destino: " + document.toJson());
            }*/
    		
	    	System.out.println("ID del documento actualizado: " + changeStreamDocument.getDocumentKey());
	        System.out.println("Descripción de la actualización: " + changeStreamDocument.getUpdateDescription());
	        
	        mapaDatos.put("id", changeStreamDocument.getDocumentKey().getObjectId("_id").getValue().toString());

	        mapaDatos.put("value", changeStreamDocument.getUpdateDescription().getUpdatedFields());
	        
	    	if(name_collection.equals("PWSCustomConfig")) {
	    		proceso_regularizacion_pwsalert(this.database.getCollection("PWSAlert"),this.database_r.getCollection("PWSAlert"));
	    	}
	        
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
                collection_r.updateOne(filter, updateDocument);
	        }catch(Exception e) {
	        	save_register(mapaDatos);
	        }
	    } else if (changeStreamDocument.getOperationType() == OperationType.DELETE) {
	        System.out.println("ID del documento eliminado: " + changeStreamDocument.getDocumentKey());
	        //json.put("value", "");
	        mapaDatos.put("id", changeStreamDocument.getDocumentKey().getObjectId("_id").getValue().toString());
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
    
    public void proceso_regularizacion_pwsalert(MongoCollection<Document> collection, MongoCollection<Document> collection_r) throws RocksDBException {
    	if(this.dataCenter.equals("ADIP")) {
    		
    		/*FindIterable<Document> documents = collection.find();
    		for (Document document : documents) {
    			collection_r.insertOne(document);
                System.out.println("Documento insertado en la base de datos destino: " + document.toJson());
            }
    		
    		Set<String> identificadoresVistos = new HashSet<>();

    		List<String> clavesAEliminar = new ArrayList<>();

    		RocksIterator iterator = this.rocksDB.newIterator();
    		    
    		for (iterator.seekToFirst(); iterator.isValid(); iterator.next()) {
    		    byte[] keyBytes = iterator.key();
    		    byte[] valueBytes = iterator.value();
    		    
    		    Map<String, Object> changeStreamDocument = deserializar(valueBytes);
    		    
    		    String key = new String(keyBytes);
    		    String msgType = "";
    		    String identifier = "";
    		    String process = "";

                try {
                    process = changeStreamDocument.get("process").toString();
                } catch (Exception e) {

                }
                if (process.equals("regularization")) {
                	try {
        		        msgType = ((Document) changeStreamDocument.get("value")).get("msgType").toString();
        		        identifier = ((Document) changeStreamDocument.get("value")).get("identifier").toString();
        		    } catch (Exception e) {
        		        
        		    }
        		    
        		    String claveUnica = msgType + "_" + identifier;

        		    if (identificadoresVistos.contains(claveUnica)) {
        		        clavesAEliminar.add(key);
        		    } else { 
        		        identificadoresVistos.add(claveUnica);
        		    }
                }
    		    
    		}

    		for (String clave : clavesAEliminar) {
    			this.rocksDB.delete(clave.getBytes());
    		}
    		
    		iterator = this.rocksDB.newIterator();
    		
    		for (iterator.seekToFirst(); iterator.isValid(); iterator.next()) {
    			byte[] keyBytes = iterator.key();
    		    byte[] valueBytes = iterator.value();
    		    
    		    Map<String, Object> changeStreamDocument = deserializar(valueBytes);
    		    
    		    String msgType = "";
    		    String identifier = "";
    		    String process = "";
    		    
    		    try {
                    process = changeStreamDocument.get("process").toString();
                } catch (Exception e) {

                }
    		    
    		    if (process.equals("regularization")) {
    		    	try {
        		        msgType = ((Document) changeStreamDocument.get("value")).get("msgType").toString();
        		        identifier = ((Document) changeStreamDocument.get("value")).get("identifier").toString();
        		    } catch (Exception e) {
        		        
        		    }
        		    
        		    Document filtro = new Document("msgType", msgType).append("identifier", identifier);
        		    Document resultado = collection.find(filtro).first();

        		    if (resultado != null) {
        		    	collection_r.deleteOne(filtro);
            		    collection_r.insertOne(resultado);
            		    this.rocksDB.delete(keyBytes);
        		    }
        		    
    		    }
    		}*/

			PWSAudit audit = PWSAudit.builder()
					.username("System")
					.module(this.module)
					.action("update")
					.description("Regularización de Alertas en " + this.dataCenter)
					.eventTime(LocalDateTime.now(ZoneId.of(this.utcZoneId)))
					.build();
			audits.save(audit).subscribe();
    	}
    }
	
    public static Props props(PWSAuditRepo audits, RocksDB rocksDB, MongoDatabase database, MongoDatabase database_r,String dataCenter, String module, String utcZoneId) {
        return Props.create(MyActor.class, audits, rocksDB, database, database_r,dataCenter, module, utcZoneId);
    }

}
