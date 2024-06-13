package com.hacom.replicatoragent.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "database-replic.data.mongodb")
public class MongoReplicProperties {
    private String authenticationDatabase;
    private String database;
    private String host;
    private int port;
    private String username;
    private String password;
}