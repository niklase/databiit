package com.zuunr.diagrammaker.mxcell;

import com.zuunr.json.*;
import com.zuunr.json.pointer.JsonPointer;

public class ObjectSchema {

    private static JsonValue OBJECT = JsonValue.of("object");
    private static JsonObject SWIMLANE = JsonValueFactory.create("""
            {
              "mxGeometry": {
                "width": "140",
                "height": "60",
                "x": "90",
                "y": "430",
                "as": "geometry"
              },
              "parent": "1",
              "id": "other",
              "style": "swimlane;fontStyle=0;childLayout=stackLayout;horizontal=1;startSize=30;horizontalStack=0;resizeParent=1;resizeParentMax=0;resizeLast=0;collapsible=1;marginBottom=0;whiteSpace=wrap;html=1;",
              "value": "Person",
              "vertex": "1"
            }
            """).getJsonObject();


    public static JsonArray mxCellsOf(JsonObject schema, JsonValue rootDoc, JsonArray pathToSchema) {

        JsonValue type = schema.get("type");

        if (type == null || OBJECT.equals(type) || type.isJsonArray() && type.getJsonArray().contains(OBJECT)) {
            JsonArrayBuilder mxCells = JsonArray.EMPTY.builder();

            MxCellsWrapper<Integer> mxCellsWrapper = Properties.mxCellsOf(schema, rootDoc, pathToSchema);
            mxCells.addAll(mxCellsWrapper.mxCells);

            if (!pathToSchema.isEmpty() && mxCellsWrapper.meta > 0) {
                mxCells.add(SWIMLANE
                        .put(JsonArray.of("mxGeometry", "height"), "" + (Properties.SWIMLANE_ITEM_HEIGHT + Properties.SWIMLANE_ITEM_HEIGHT * mxCellsWrapper.meta))
                        //.put(JsonArray.of("mxGeometry", "x"), "" + (pathToSchema.as(JsonPointer.class).getJsonPointerString().getString().length() * 10))
                        .put("id", MxCell.createId(pathToSchema))
                        .put("value", schema.get("title", pathToSchema.isEmpty() ? JsonValue.of("ROOT") : pathToSchema.last())).jsonValue());
            }
            return mxCells.build();
        }
        return JsonArray.EMPTY;
    }


}