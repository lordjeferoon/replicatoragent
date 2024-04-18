package com.hacom.replicatoragent.actor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;

import org.bson.Document;
import org.springframework.data.mongodb.core.ChangeStreamEvent;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.publisher.Flux;

public class MongoDBChangeDetectionActor extends AbstractActor {
    private final ReactiveMongoTemplate mongoTemplate;
    private final ActorRef changeHandler;

    public MongoDBChangeDetectionActor(ReactiveMongoTemplate mongoTemplate, ActorRef changeHandler) {
        this.mongoTemplate = mongoTemplate;
        this.changeHandler = changeHandler;
    }

    public static Props props(ReactiveMongoTemplate mongoTemplate, ActorRef changeHandler) {
        return Props.create(MongoDBChangeDetectionActor.class, mongoTemplate, changeHandler);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(StartChangeDetection.class, msg -> startChangeDetection())
                .build();
    }

    private void startChangeDetection() {
    	System.out.println("ENTRO");
        Flux<ChangeStreamEvent<Document>> changeStream = (Flux<ChangeStreamEvent<Document>>) mongoTemplate.changeStream(Document.class);
        changeStream.subscribe(changeEvent -> {
            String collectionName = changeEvent.getRaw().getNamespace().getCollectionName();
            Document changedDocument = changeEvent.getBody();
            System.out.println("COLECCION: ---------> " + collectionName);
            changeHandler.tell(new ChangeEvent(collectionName, changedDocument), self());
        });
    }

    public static class StartChangeDetection {}

    public static class ChangeEvent {
        private final String collectionName;
        private final Document document;

        public ChangeEvent(String collectionName, Document document) {
            this.collectionName = collectionName;
            this.document = document;
        }

        public String getCollectionName() {
            return collectionName;
        }

        public Document getDocument() {
            return document;
        }
    }
}
