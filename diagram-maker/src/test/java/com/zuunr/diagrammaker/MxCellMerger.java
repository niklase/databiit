package com.zuunr.diagrammaker;

import com.zuunr.json.*;

import java.util.Comparator;

public class MxCellMerger {

    private static JsonObjectMerger merger = new JsonObjectMerger();

    public static JsonArray merge(JsonArray mxCells, JsonArray autoMxCells) {
        JsonObjectBuilder builder = JsonObject.EMPTY.builder();

        for (int i = 0; i < autoMxCells.size(); i++) {
            JsonObject autoMxCell = autoMxCells.get(i).getJsonObject();
            builder.put(autoMxCell.get("id").getString(), autoMxCell);
        }
        return merge(mxCells, builder.build());
    }

    public static JsonArray merge(JsonArray mxCells, JsonObject autoMxCellsById) {

        JsonObject autoCellsLeft = autoMxCellsById;
        JsonArrayBuilder newMxCells = JsonArray.EMPTY.builder();

        for (int i = 0; i < mxCells.size(); i++) {
            JsonObject mxCell = mxCells.get(i).getJsonObject();
            String mxCellId = mxCell.get("id").getString();
            JsonObject autoCell = autoMxCellsById.get(mxCell.get("id").getString(), JsonValue.NULL).getJsonObject();

            if (autoCell == null) {
                if (!mxCellId.startsWith("auto:")) {
                    newMxCells.add(mxCell);
                } else {
                    // do not add this one as it is auto-generated but does no longer exist
                }
            } else {
                autoCellsLeft = autoCellsLeft.remove(mxCellId);
                if (mxCellId.startsWith("auto:")) {
                    newMxCells.add(forgeMxCells(mxCell, autoCell));
                } else {
                    newMxCells.add(mxCell);
                }
            }
        }
        return newMxCells.addAll(autoCellsLeft.values().sort(new Comparator<JsonValue>() {
            @Override
            public int compare(JsonValue o1, JsonValue o2) {
                return o1.getJsonObject().get("id").getString().compareTo(o2.getJsonObject().get("id").getString());
            }
        })).build();
    }

    private static JsonObject forgeMxCells(JsonObject mxCellCustom, JsonObject mxCellAuto) {
        //JsonValue mergedStyle = StyleMerger.merge(mxCell1.get("style"), mxCell2.get("style"));
        JsonObject forged = merger.merge(mxCellCustom, mxCellAuto)
                //.put("style", mergedStyle)
                ;

        // x/y position and height/weight should be customizable
        JsonObjectBuilder mxGeometry = JsonObject.EMPTY.builder();
        JsonObject customGeometry = mxCellCustom.get("mxGeometry", JsonValue.NULL).getJsonObject();
        JsonValue width = customGeometry.get("width", mxCellAuto.get("width"));
        JsonValue height = customGeometry.get("height", mxCellAuto.get("height"));
        JsonValue x = customGeometry.get("x", mxCellAuto.get("x"));
        JsonValue y = customGeometry.get("y", mxCellAuto.get("y"));
        customGeometry.put("x", x).put("y",y).put("width", width).put("height", height);
        return forged.put("mxGeometry", customGeometry);
    }

    private static JsonObject mergeMxCells(JsonObject mxCell1, JsonObject mxCell2) {
        //JsonValue mergedStyle = StyleMerger.merge(mxCell1.get("style"), mxCell2.get("style"));
        return merger.merge(mxCell1, mxCell2)
                //.put("style", mergedStyle)
                ;
    }
}
