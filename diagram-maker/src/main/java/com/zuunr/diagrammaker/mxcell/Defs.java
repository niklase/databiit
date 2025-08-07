package com.zuunr.diagrammaker.mxcell;

import com.zuunr.json.JsonArray;
import com.zuunr.json.JsonArrayBuilder;
import com.zuunr.json.JsonObject;
import com.zuunr.json.JsonValue;

public class Defs {

    public static JsonArray mxCellsOf(JsonObject jsonSchema, JsonValue rootDoc, JsonArray pathToSchema) {

        JsonObject defs = jsonSchema.get("$defs", JsonValue.NULL).getJsonObject();
        if (defs == null) {
            return JsonArray.EMPTY;
        }

        JsonArray defNames = defs.keys();
        JsonArray defSchemas = defs.values();
        JsonArrayBuilder mxCellsBuilder = JsonArray.EMPTY.builder();
        for (int i = 0; i < defNames.size(); i++) {
            String defName = defNames.get(i).getString();
            JsonValue defSchema = defSchemas.get(i);
            mxCellsBuilder.addAll(Schema.mxCellsOf(defSchema, rootDoc, pathToSchema.add("$defs").add(defName)));
        }
        return mxCellsBuilder.build();
    }
}
