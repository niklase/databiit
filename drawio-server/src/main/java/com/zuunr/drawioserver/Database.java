package com.zuunr.drawioserver;

import com.zuunr.json.JsonObject;
import com.zuunr.json.JsonValue;
import com.zuunr.json.JsonValueFactory;

public class Database {

    JsonObject store = JsonObject.EMPTY
            .put("id-1", JsonObject.EMPTY
                    .put("id", "id-1")
                    .put("schema", JsonValueFactory.create("""
                           {
                                "$defs": {
                                    "Person": {
                                        "title": "Person",
                                        "type": "object",
                                        "properties": {
                                            "namn": {
                                                "type": "string"
                                            },
                                            "adress": {
                                                    "$ref": "#/$defs/Address"
                                                },
                                            "addresser": {
                                                "type": "array",
                                                "items": {
                                                    "$ref": "#/$defs/Address"
                                                }
                                            }
                                        }
                                    },
                                    "Address": {
                                        "title": "Address",
                                        "type": "object",
                                        "properties": {
                                            "street": {
                                                "type": "string"
                                            }
                                        }
                                    }
                                }
                            }
                            """)));

    public JsonObject putItem(JsonObject item) {
        String id = item.get("id").getString();
        item = item.put("id", id);
        store = store.put(id, item);
        return item;
    }

    public JsonObject readItem(String id) {
        return store.get(id, JsonValue.NULL).getJsonObject();
    }
}
