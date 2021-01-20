package com.nexthoughts.poc.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.Filters;
import com.nexthoughts.poc.payload.SearchRequest;
import com.nexthoughts.poc.payload.QueryResponse;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class QueryService {

    private static final Logger logger = LoggerFactory.getLogger(QueryService.class);
    @Autowired
    MongoTemplate mongoTemplate;

    public ResponseEntity<?> createConnectionAndSearch(SearchRequest searchRequest) {
        logger.info("***in query service***");
        List<QueryResponse> queryResponses = new ArrayList<>();
        if (searchRequest.getWord() == null || searchRequest.getWord().equals("")) {
            return new ResponseEntity<>("There is no word present to be searched!", HttpStatus.NOT_ACCEPTABLE);
        }
        try {
            MongoClientURI uri = new MongoClientURI(searchRequest.getUri());
            MongoClient mongoClient = new MongoClient(uri);
            for (String databaseName : mongoClient.listDatabaseNames()) {
                logger.info("***Database: ***" + databaseName);
                MongoDatabase database = mongoClient.getDatabase(databaseName);
                MongoIterable<String> collections = database.listCollectionNames();
                for (String collectionName : collections) {
                    logger.info("***Collection: ***" + collectionName);
                    MongoCollection<Document> mongoCollection = database.getCollection(collectionName);
                    Set<String> fieldNames = new HashSet<>();
                    Iterable<Document> fields = mongoCollection.find();
                    fields.forEach(field -> {
                        String json = field.toJson();
                        JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();
                        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                            fieldNames.add(entry.getKey());
                        }
                    });
                    for (String fieldName : fieldNames) {
                        Iterable<Document> documents = mongoCollection.find(Filters.regex(fieldName, searchRequest.getWord()));
                        documents.forEach(document -> {
                            Object idObject = document.get("_id");
                            String id = idObject.toString();
                            QueryResponse queryResponse = new QueryResponse(databaseName, collectionName, fieldName, id);
                            queryResponses.add(queryResponse);
                        });
                    }
                }
            }
            return new ResponseEntity<>(queryResponses, HttpStatus.OK);
        } catch (
                Exception e) {
            logger.info("***Error connecting!***" + e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }
}
