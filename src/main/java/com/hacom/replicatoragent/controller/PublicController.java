package com.hacom.replicatoragent.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.*;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import com.mongodb.client.model.changestream.OperationType;
import com.mongodb.client.result.DeleteResult;

import java.util.Arrays;

import org.bson.BsonDocument;
import org.bson.BsonObjectId;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.rocksdb.RocksIterator;
import java.util.HashMap;
import java.util.Map;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

@RestController
@RequestMapping("/public")
public class PublicController {

    @GetMapping(value = "/status")
    public Mono<ResponseEntity<String>> getStatus() {
        return Mono.just(ResponseEntity.ok("OK"));
    }

}