package com.hacom.replicatoragent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import com.mongodb.client.*;
import com.mongodb.client.model.changestream.ChangeStreamDocument;

import org.bson.Document;

@SpringBootApplication
@ComponentScan(basePackages = "com.hacom")
public class ReplicatorAgentApplication {

	public static void main(String[] args) {
		/*MongoClient mongoClient = MongoClients.create("mongodb://192.168.5.128:27017");
        MongoDatabase database = mongoClient.getDatabase("pwsalertsystem");
        MongoCollection<Document> collection = database.getCollection("PWSState");

        MongoCursor<ChangeStreamDocument<Document>> cursor = collection.watch().iterator();

        while (cursor.hasNext()) {
            ChangeStreamDocument<Document> changeStreamDocument = cursor.next();
            System.out.println("Operación: " + changeStreamDocument.getOperationType());
            System.out.println("Colección: " + changeStreamDocument.getNamespace().getCollectionName());
            System.out.println("Documento: " + changeStreamDocument.getFullDocument());
        }*/
        SpringApplication.run(ReplicatorAgentApplication.class, args);
	}

	@Bean
	public WebClient webClient() {
		final int size = 100 * 1024 * 1024;
		final ExchangeStrategies strategies = ExchangeStrategies.builder()
				.codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(size))
				.build();
		return WebClient.builder()
				.exchangeStrategies(strategies)
				.build();
	}
}
