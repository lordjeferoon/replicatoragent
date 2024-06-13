package com.hacom.replicatoragent.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

@Configuration
public class MongoReplicConfig {

    private final MongoReplicProperties mongoProperties;

    public MongoReplicConfig(MongoReplicProperties mongoProperties) {
        this.mongoProperties = mongoProperties;
    }

    @Bean
    public MongoClient mongoReplicClient() {
        String uri = String.format("mongodb://%s:%s@%s:%d/%s",
                mongoProperties.getUsername(),
                mongoProperties.getPassword(),
                mongoProperties.getHost(),
                mongoProperties.getPort(),
                mongoProperties.getAuthenticationDatabase());
        return MongoClients.create(uri);
    }

    @Bean
    public MongoTemplate mongoReplicTemplate(MongoClient mongoReplicClient) {
        return new MongoTemplate(new SimpleMongoClientDatabaseFactory(mongoReplicClient, mongoProperties.getDatabase()));
    }
}
