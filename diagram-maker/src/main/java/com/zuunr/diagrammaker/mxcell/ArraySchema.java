package com.zuunr.diagrammaker.mxcell;

import com.zuunr.json.JsonArray;
import com.zuunr.json.JsonObject;
import com.zuunr.json.JsonValue;
import com.zuunr.json.schema.JsonSchema;
import com.zuunr.json.schema.Keywords;

public class ArraySchema {

    public static JsonArray mxCellsOf(JsonObject schema, JsonValue rootDoc, JsonArray pathToSchema) {

        JsonValue type = schema.get(Keywords.TYPE);

        JsonArray result = JsonArray.EMPTY;
        if (type == null || Keywords.ARRAY.equals(type) || type.isJsonArray() && type.getJsonArray().contains(Keywords.ARRAY)) {

            JsonValue nestedSchema = schema.get(Keywords.ITEMS);

            if (nestedSchema.isJsonObject()) {
                result = Schema.mxCellsOf(nestedSchema, rootDoc, pathToSchema.add(Keywords.ITEMS));
            }

        }
        return result;
    }
}
