package com.zuunr.formbuilder;

import com.zuunr.json.JsonObject;
import com.zuunr.json.JsonValue;

import java.util.UUID;

public class Database {

    private JsonObject store = JsonObject.EMPTY;

    public JsonObject createItem(JsonObject item) {
        String id = UUID.randomUUID().toString();
        item = item.put("id", id);
        store = store.put(id, item);
        return item;
    }

    public JsonObject readItem(String id) {
        return store.get(id, JsonValue.NULL).getJsonObject();
    }

    public void writeItem(JsonObject item) {
        store = store.put(item.get("id").getString(), item);
    }

    public void deleteItem(String id) {
        store = store.remove(id);
    }
}
