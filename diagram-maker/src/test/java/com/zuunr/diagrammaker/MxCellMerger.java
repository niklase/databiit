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
                    // do not add this one as it is auto-generated but dos no longer exist
                }
            } else {
                autoCellsLeft = autoCellsLeft.remove(mxCellId);
                newMxCells.add(merger.merge(mxCell, autoCell));
            }
        }
        return newMxCells.addAll(autoCellsLeft.values().sort(new Comparator<JsonValue>() {
            @Override
            public int compare(JsonValue o1, JsonValue o2) {
                return o1.getJsonObject().get("id").getString().compareTo(o2.getJsonObject().get("id").getString());
            }
        })).build();
    }

    private static JsonObject mergeMxCells(JsonObject mxCell1, JsonObject mxCell2) {
        JsonValue mergedStyle = StyleMerger.merge(mxCell1.get("style"), mxCell2.get("style"));
        return merger.merge(mxCell1, mxCell2).put("style", mergedStyle);
    }

}
