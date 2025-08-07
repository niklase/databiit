package com.zuunr.diagrammaker.mxcell;

import com.zuunr.diagrammaker.mxcell.Templates;
import com.zuunr.json.JsonArray;
import com.zuunr.json.JsonObject;
import com.zuunr.json.JsonValue;
import com.zuunr.json.pointer.JsonPointer;

public class Ref {


    public static JsonArray mxCellsOf(JsonObject schema, JsonValue rootDoc, JsonArray pathToSchema) {

        JsonValue ref = schema.get("$ref");

        if (ref != null) {
            Templates.ARROW
                    .put("source", pathToSchema.as(JsonPointer.class).getJsonPointerString())
                    .put("target", ref.getString());

        }
        return JsonArray.EMPTY;
    }
}
