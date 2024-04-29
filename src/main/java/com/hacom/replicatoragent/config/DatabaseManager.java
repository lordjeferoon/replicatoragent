package com.hacom.replicatoragent.config;

import java.util.Arrays;

import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.springframework.stereotype.Component;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

@Component
public class DatabaseManager {
	
	private final MongoDatabase database;
    private final MongoDatabase database_r;
    private final RocksDB rocksDB;

    public DatabaseManager() {
        // Configuración de MongoDB
        this.database = conexion_mongodb("pwsuser", "pwsuser", "pwsalertsystem", "192.168.5.128", 27017);
        this.database_r = conexion_mongodb("pwsuserv2", "pwsuserv2", "pwsalertsystemv2", "192.168.5.128", 27017);

        // Configuración de RocksDB
        RocksDB.loadLibrary();
        Options options = new Options().setCreateIfMissing(true);

        RocksDB db = null;
        try {
            db = RocksDB.open(options, "rocksdb_data");
            System.out.println("Conexión exitosa a RocksDB");
        } catch (RocksDBException e) {
            System.out.println("Error al abrir RocksDB");
            e.printStackTrace();
        }
        this.rocksDB = db;
    }

    public MongoDatabase getDatabase() {
        return database;
    }

    public MongoDatabase getDatabase_r() {
        return database_r;
    }

    public RocksDB getRocksDB() {
        return rocksDB;
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

}
