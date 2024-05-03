package com.hacom.replicatoragent.config;

import java.util.Arrays;

import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

@Configuration
public class DatabaseManager {

	private final MongoDatabase database;
    private final MongoDatabase database_r;
    private final RocksDB rocksDB;

    //Datos conexión a la BD
    @Value("${spring.data.mongodb.database}")
    private String databaseName;
    @Value("${spring.data.mongodb.username}")
    private String databaseUsername;
    @Value("${spring.data.mongodb.password}")
    private String databasePassword;
    @Value("${spring.data.mongodb.host}")
    private String databaseHost;
    @Value("${spring.data.mongodb.port}")
    private int databasePort;

    //Datos Conexion a la réplica
    @Value("${database-replic.data.mongodb.database}")
    private String databaseNameReplic;
    @Value("${database-replic.data.mongodb.username}")
    private String databaseUsernameReplic;
    @Value("${database-replic.data.mongodb.password}")
    private String databasePasswordReplic;
    @Value("${database-replic.data.mongodb.host}")
    private String databaseHostReplic;
    @Value("${database-replic.data.mongodb.port}")
    private int databasePortReplic;

    public DatabaseManager() {
        System.out.printf(databaseUsername + " " + databasePassword + " " + databaseName + " " + databaseHost + " " + databasePort);
        // Configuración de MongoDB
        this.database = conexion_mongodb(databaseUsername, databasePassword, databaseName, databaseHost, databasePort);
        this.database_r = conexion_mongodb(databaseUsernameReplic, databasePasswordReplic, databaseNameReplic, databaseHostReplic, databasePortReplic);

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

    public MongoDatabase conexion_mongodb(String username, String password, String databaseName, String ip, int puerto) {
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
