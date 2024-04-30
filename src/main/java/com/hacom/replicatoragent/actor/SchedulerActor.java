package com.hacom.replicatoragent.actor;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.Map;

import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.rocksdb.RocksIterator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;

import akka.actor.AbstractActor;
import akka.actor.Props;

public class SchedulerActor extends AbstractActor {
	
	RocksDB rocksDB = null;
	MongoDatabase database_r = null;
	
	@Override
    public Receive createReceive() {
      return receiveBuilder()
    		  .matchAny(message -> {
	    		regularizar_bd();
	          })
	          .build();
    }
	
	public SchedulerActor(RocksDB rocksDB, MongoDatabase database_r) {
		this.rocksDB = rocksDB;
		this.database_r = database_r;
	}
	
	public SchedulerActor() {
		
	}
	
	public static Props props(RocksDB rocksDB, MongoDatabase database_r) {
        return Props.create(SchedulerActor.class, rocksDB,database_r);
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
	
	public void regularizar_bd() throws JsonMappingException, JsonProcessingException, RocksDBException {
		// Obtener un iterador sobre todas las entradas en RocksDB
		RocksIterator iterator = this.rocksDB.newIterator();
		
		// Convertir el String JSON a un ObjectNode
        //ObjectMapper objectMapper = new ObjectMapper();
		
		// Iterar sobre todas las entradas
		System.out.println("==================");
		System.out.println("Inicio de regularización");
		System.out.println("==================");
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
		}
    }
}
