package com.zuunr.diagrammaker.mxcell;

import com.zuunr.json.JsonArray;
import com.zuunr.json.JsonValue;
import com.zuunr.json.pointer.JsonPointer;

public class MxCell {

    public static JsonValue createId(JsonArray pathInDoc) {
        return JsonValue.of(Templates.PREFIX_OF_MANAGED_CELLS+pathInDoc.as(JsonPointer.class).getJsonPointerString().getString());
    }
}
