package com.hacom.replicatoragent.config;

import java.util.Arrays;

import javax.annotation.PostConstruct;

import org.bson.BsonObjectId;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import com.mongodb.client.model.changestream.OperationType;

@Configuration
//@ComponentScan(basePackages = "com.hacom")
public class AppConfig {
	
	@PostConstruct
    public void initialize() {
		/*String username = "pwsuser";
    	String password = "pwsuser";
    	String databaseName = "pwsalertsystem";
    	
    	//Configuración MongoDB
    	MongoCredential credential = MongoCredential.createCredential(username, databaseName, password.toCharArray());
    	MongoClientSettings settings = MongoClientSettings.builder()
    	        .credential(credential)
    	        .applyToClusterSettings(builder ->
    	                builder.hosts(Arrays.asList(new ServerAddress("192.168.5.128", 27017))))
    	        .build();
    	
    	MongoClient mongoClient = MongoClients.create(settings);
    	
    	MongoDatabase database = mongoClient.getDatabase(databaseName);
    	MongoCollection<Document> collection = database.getCollection("PWSState");
    	
    	MongoCursor<ChangeStreamDocument<Document>> cursor = collection.watch().iterator();
    	
    	while (cursor.hasNext()) {
    		ObjectMapper objectMapper = new ObjectMapper();
    	    ObjectNode json = objectMapper.createObjectNode();
    	    
    	    ChangeStreamDocument<Document> changeStreamDocument = cursor.next();
    	    System.out.println("Operación: " + changeStreamDocument.getOperationType());
    	    System.out.println("Colección: " + changeStreamDocument.getNamespace().getCollectionName());
    	    
    	    json.put("operation", changeStreamDocument.getOperationType().getValue().toString());
    	    json.put("collection", changeStreamDocument.getNamespace().getCollectionName().toString());
    	    
    	    if (changeStreamDocument.getOperationType() == OperationType.INSERT || changeStreamDocument.getOperationType() == OperationType.REPLACE) {
    	        System.out.println("Documento insertado o reemplazado: " + changeStreamDocument.getFullDocument());
    	        //json.put("id", changeStreamDocument.getFullDocument().getObjectId("_id").toString());
    	        json.put("value", changeStreamDocument.getFullDocument().toJson());
    	    } else if (changeStreamDocument.getOperationType() == OperationType.UPDATE) {
    	        System.out.println("ID del documento actualizado: " + changeStreamDocument.getDocumentKey());
    	        System.out.println("Descripción de la actualización: " + changeStreamDocument.getUpdateDescription());
    	        
    	        //json.put("id", changeStreamDocument.getDocumentKey().getObjectId("_id").getValue().toString());
    	        json.put("value", changeStreamDocument.getUpdateDescription().getUpdatedFields().toJson());
    	    } else if (changeStreamDocument.getOperationType() == OperationType.DELETE) {
    	        System.out.println("ID del documento eliminado: " + changeStreamDocument.getDocumentKey());
    	        
    	        json.put("value", "");
    	    }
    	    
    	    try {
	        	json.put("id", changeStreamDocument.getDocumentKey().getObjectId("_id").getValue().toString());
	        }catch(Exception e) {
	        	json.put("id", changeStreamDocument.getDocumentKey().getString("_id").getValue());
	        }
    	    
    	    // Configuración de RocksDB
            RocksDB.loadLibrary();
            Options options = new Options().setCreateIfMissing(true);
            RocksDB rocksDB = null;
            
            try {
                rocksDB = RocksDB.open(options, "rocksdb_data");
            } catch (RocksDBException e) {
                e.printStackTrace();
            }
    	    
    	    // Obtener la ID del documento actualizado
    	    BsonValue documentId = new BsonObjectId(new ObjectId());
    	    
	        // Obtener el valor como BsonObjectId
	        BsonObjectId objectId = documentId.asObjectId();
	        
	        // Obtener la representación hexadecimal del ObjectId
	        String key = objectId.getValue().toHexString();
	        
    	    System.out.println("Valor del ObjectId como cadena hexadecimal: " + key);
    	    
            try {
            	rocksDB.put(key.getBytes(), json.toString().getBytes());
            } catch (RocksDBException e) {
                e.printStackTrace();
            }
    	}*/
    }

}
