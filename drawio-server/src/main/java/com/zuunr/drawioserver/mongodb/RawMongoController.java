package com.zuunr.drawioserver.mongodb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.bson.Document;

@RestController
@RequestMapping("/api/raw")
public class RawMongoController {

    @Autowired
    private RawMongoService rawMongoService;

    @PostMapping("/insert/{collectionName}")
    public String insertRawDocument(@PathVariable String collectionName, @RequestBody String jsonString) {
        rawMongoService.insertRawDocument(collectionName, jsonString);
        return "Document inserted!";
    }

    @GetMapping("/find/{collectionName}")
    public List<Document> getAllDocuments(@PathVariable String collectionName) {
        return rawMongoService.getAllDocuments(collectionName);
    }

    @PostMapping("/find/{collectionName}")
    public List<Document> findDocumentsWithFilter(
            @PathVariable String collectionName,
            @RequestBody String filterJson) {
        return rawMongoService.findDocumentsWithFilter(collectionName, filterJson);
    }

    @DeleteMapping("/delete/{collectionName}")
    public String deleteDocumentsWithFilter(
            @PathVariable String collectionName,
            @RequestBody String filterJson) {
        rawMongoService.deleteDocumentsWithFilter(collectionName, filterJson);
        return "Documents deleted!";
    }
}