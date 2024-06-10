package com.hacom.replicatoragent.service;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Service
public class ReplicatorService {

    public ReplicatorService(MongoTemplate mongoTemplate) {

    }

    public void insertDocument(String collection, Object document) {

    }

    public void updateDocument(String collection, Object document) {

    }

    public void deleteDocument(String collection, Object document) {

    }

    public void processDocument(String collection, String operation, Object document) {
        switch (operation) {
            case "insert":
                insertDocument(collection, document);
                break;
            case "update":
                updateDocument(collection, document);
                break;
            case "delete":
                deleteDocument(collection, document);
                break;
            default:
                System.out.println("Operación no válida");
                break;
        }
    }
}