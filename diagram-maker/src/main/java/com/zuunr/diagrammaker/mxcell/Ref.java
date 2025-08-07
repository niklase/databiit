package com.zuunr.diagrammaker.mxcell;

import com.zuunr.json.JsonArray;
import com.zuunr.json.JsonObject;
import com.zuunr.json.JsonValue;
import com.zuunr.json.pointer.JsonPointer;

public class Ref {

    public static JsonArray mxCellsOf(JsonObject schema, JsonValue rootDoc, JsonArray pathToSchema) {

        JsonValue ref = schema.get("$ref");

        if (ref != null) {
            Templates.ARROW
                    .put("source", MxCell.createId(pathToSchema))
                    .put("target", MxCell.createId(ref.as(JsonPointer.class).asArray()));
        }
        return JsonArray.EMPTY;
    }
}
