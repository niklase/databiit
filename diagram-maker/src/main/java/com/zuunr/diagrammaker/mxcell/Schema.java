package com.zuunr.diagrammaker.mxcell;

import com.zuunr.diagrammaker.mxcell.Defs;
import com.zuunr.diagrammaker.mxcell.ObjectSchema;
import com.zuunr.diagrammaker.mxcell.Ref;
import com.zuunr.json.JsonArray;
import com.zuunr.json.JsonArrayBuilder;
import com.zuunr.json.JsonObject;
import com.zuunr.json.JsonValue;

public class Schema {

    public static JsonArray mxCellsOf(JsonValue jsonSchema) {
        return mxCellsOf(jsonSchema, jsonSchema, JsonArray.EMPTY);
    }

    public static JsonArray mxCellsOf(JsonValue jsonSchema, JsonValue rootDoc, JsonArray pathToSchema) {
        if (!jsonSchema.isJsonObject()) {
            return JsonArray.EMPTY;
        } else {
            return mxCellOf(jsonSchema.getJsonObject(), rootDoc, pathToSchema);
        }
    }

    private static JsonArray mxCellOf(JsonObject jsonSchema, JsonValue rootDoc, JsonArray pathToSchema) {

        JsonArrayBuilder mxCellBuilder = JsonArray.EMPTY.builder();

        // MxCellsWrapper<Integer> mxCellsWrapper = Properties.mxCellsOf(jsonSchema, rootDoc, pathToSchema);
        // mxCellBuilder.addAll(mxCellsWrapper.mxCells);

        JsonArray objectSchemaMxCells = ObjectSchema.mxCellsOf(jsonSchema, rootDoc, pathToSchema);
        mxCellBuilder.addAll(objectSchemaMxCells);

        JsonArray arrowMxCells = Ref.mxCellsOf(jsonSchema, rootDoc, pathToSchema);
        mxCellBuilder.addAll(arrowMxCells);

        JsonArray defMxCells = Defs.mxCellsOf(jsonSchema, rootDoc, pathToSchema);
        mxCellBuilder.addAll(defMxCells);

        return mxCellBuilder.build();
    }


    private static JsonArray createSwimlaneItems(JsonObject schema) {
        return JsonArray.EMPTY;
    }
}
