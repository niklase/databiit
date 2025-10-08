package com.zuunr.drawioserver.mongodb;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.zuunr.json.JsonObject;
import com.zuunr.json.util.JsonObjectWrapper;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Service
public class RawMongoDB {

    private final ResourceDeserializer resourceDeserializer = new ResourceDeserializer().init();

    @Autowired
    private MongoClient mongoClient;

    @Autowired
    MongoTemplate mongoTemplate;

    private CreateCommandTranslator create = new CreateCommandTranslator();
    private CreateIndexesCommandTranslator createIndexes = new CreateIndexesCommandTranslator();
    private CreateUserCommandTranslator createUser = new CreateUserCommandTranslator();
    private DropDatabaseCommandTranslator dropDatabase = new DropDatabaseCommandTranslator();
    private AggregateCommandTranslator aggregate = new AggregateCommandTranslator();
    private FindCommandTranslator find = new FindCommandTranslator();
    private FindAndModifyCommandTranslator findAndModify = new FindAndModifyCommandTranslator();
    private InsertCommandTranslator insert = new InsertCommandTranslator();
    private UpdateCommandTranslator update = new UpdateCommandTranslator();
    private DeleteCommandTranslator delete = new DeleteCommandTranslator();
    private DropCommandTranslator drop = new DropCommandTranslator();




    public JsonObject runCommand(JsonObject command) {
        MongoDatabase mongoDatabase = mongoTemplate.getMongoDatabaseFactory().getMongoDatabase("dryagram");
        Document bsonCommand = translate(command);
        return runCommand(bsonCommand, mongoDatabase);
    }

    private JsonObject runCommand(Document bsonCommand, MongoDatabase mongoDatabase) {

        Document outDoc = mongoDatabase.runCommand(bsonCommand);
        return resourceDeserializer.deserialize(outDoc, JsonObjectWrapper.class).asJsonObject();
    }


    private Document translate(JsonObject command) {

        String commandName = command.keys().get(0).getString();
        JsonObject commandValue = command.values().get(0).getJsonObject();
        //System.out.println("db command: "+commandName+"\n" + commandValue.asPrettyJson());
        switch (commandName) {
            case "aggregate":
                return aggregate.translate(commandValue);
            case "create":
                return create.translate(commandValue);
            case "createIndexes":
                return createIndexes.translate(commandValue);
            case "createUser":
                return createUser.translate(commandValue);
            case "dropDatabase":
                return dropDatabase.translate(commandValue);
            case "find":
                return find.translate(commandValue);
            case "findAndModify":
                return findAndModify.translate(commandValue);
            case "insert":
                return insert.translate(commandValue);
            case "update":
                return update.translate(commandValue);
            case "delete":
                return delete.translate(commandValue);
            case "drop":
                return drop.translate(commandValue);
            default:
                throw new RuntimeException("Unsupported command");
        }
    }
}