package com.zuunr.drawioserver.mongodb;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RawMongoService {

    @Autowired
    private MongoTemplate mongoTemplate;

    // Insert a raw BSON document
    public void insertRawDocument(String collectionName, String jsonString) {
        Document document = Document.parse(jsonString); // Parse raw JSON into BSON Document
        mongoTemplate.getCollection(collectionName).insertOne(document); // Insert into MongoDB
    }

    // Fetch all documents from a collection
    public List<Document> getAllDocuments(String collectionName) {
        MongoCollection<Document> collection = mongoTemplate.getCollection(collectionName);
        return collection.find().into(new java.util.ArrayList<>());
    }

    // Find documents with a filter (BSON)
    public List<Document> findDocumentsWithFilter(String collectionName, String filterJson) {
        Document filter = Document.parse(filterJson); // Parse filter JSON into BSON Document
        MongoCollection<Document> collection = mongoTemplate.getCollection(collectionName);
        return collection.find(filter).into(new java.util.ArrayList<>());
    }

    // Delete documents using a filter
    public void deleteDocumentsWithFilter(String collectionName, String filterJson) {
        Document filter = Document.parse(filterJson); // Parse filter into BSON Document
        mongoTemplate.getCollection(collectionName).deleteMany(filter); // Delete based on filter
    }
}