package com.hacom.replicatoragent.actor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.bson.BsonDocument;
import org.bson.BsonObjectId;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import com.mongodb.client.model.changestream.OperationType;
import com.mongodb.client.result.DeleteResult;

import akka.actor.AbstractActor;
import akka.actor.Props;

public class MyActor extends AbstractActor {
	
    RocksDB rocksDB = null;
    MongoDatabase database = null;
    MongoDatabase database_r = null;
    
    public MyActor(RocksDB rocksDB, MongoDatabase database, MongoDatabase database_r) {
        this.rocksDB = rocksDB;
        this.database = database;
        this.database_r = database_r;
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
	
    public static Props props(RocksDB rocksDB, MongoDatabase database, MongoDatabase database_r) {
        return Props.create(MyActor.class, rocksDB, database, database_r);
    }

}
