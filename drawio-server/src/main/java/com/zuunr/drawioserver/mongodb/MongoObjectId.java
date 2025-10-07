package com.zuunr.drawioserver.mongodb;

import com.zuunr.json.JsonValue;
import org.bson.types.ObjectId;

public class MongoObjectId {

    private JsonValue jsonValue;
    private ObjectId objectId;

    private MongoObjectId(JsonValue jsonValue) {
        this.jsonValue = jsonValue;
    }

    public boolean isValid() {
        try {
            getObjectId();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public ObjectId getObjectId() {
        if (objectId == null) {
            objectId = new ObjectId(jsonValue.getString());
        }
        return objectId;
    }
}
