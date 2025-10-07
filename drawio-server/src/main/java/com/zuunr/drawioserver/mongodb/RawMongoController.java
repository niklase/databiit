package com.zuunr.drawioserver.mongodb;

import com.zuunr.json.JsonArray;
import com.zuunr.json.JsonObject;
import com.zuunr.json.JsonValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.bson.Document;

@RestController
@RequestMapping("/mongodb")
public class RawMongoController {

    @Autowired RawMongoDB rawMongoDB;

    @GetMapping(value = "/test", produces = "application/json")
    public JsonValue insertRawDocument() {
        return rawMongoDB.runCommand(JsonObject.EMPTY
                .put("insert", JsonObject.EMPTY
                        .put("insert", "persons")
                        .put("documents", JsonArray.of(JsonObject.EMPTY
                                .put("name", "Peter Andersson")
                                .put("secretId", "sercretid..."))))).jsonValue();
    }
}