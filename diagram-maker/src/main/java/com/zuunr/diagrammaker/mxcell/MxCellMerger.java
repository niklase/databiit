package com.zuunr.diagrammaker.mxcell;

import com.zuunr.json.*;

import java.util.Comparator;

public class MxCellMerger {


    private static JsonObjectMerger merger = new JsonObjectMerger();

    public static JsonArray merge(JsonArray mxCells, JsonArray autoMxCells) {
        JsonObjectBuilder autoMxCellsBuilder = JsonObject.EMPTY.builder();

        for (int i = 0; i < autoMxCells.size(); i++) {
            JsonObject autoMxCell = autoMxCells.get(i).getJsonObject();
            autoMxCellsBuilder.put(autoMxCell.get("id").getString(), autoMxCell);
        }
        return merge(mxCells, autoMxCellsBuilder.build());
    }

    private static JsonArray merge(JsonArray customMxCells, JsonObject autoMxCellsById) {

        JsonObject autoCellsLeft = autoMxCellsById;
        JsonArrayBuilder newMxCells = JsonArray.EMPTY.builder();

        for (int i = 0; i < customMxCells.size(); i++) {
            JsonObject mxCell = customMxCells.get(i).getJsonObject();
            String mxCellId = mxCell.get("id").getString();
            JsonObject autoCell = autoMxCellsById.get(mxCell.get("id").getString(), JsonValue.NULL).getJsonObject();

            if (autoCell == null) {
                if (!mxCellId.startsWith(Templates.PREFIX_OF_MANAGED_CELLS)) {
                    newMxCells.add(mxCell);
                } else {
                    // do not add this one as it is auto-generated but does no longer exist
                }
            } else {
                autoCellsLeft = autoCellsLeft.remove(mxCellId);
                if (mxCellId.startsWith(Templates.PREFIX_OF_MANAGED_CELLS)) {
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
        JsonObject mxGeometryForged = mxCellAuto.get("mxGeometry", JsonValue.NULL).getJsonObject();

        if (mxGeometryForged != null) {
            JsonObject customGeometry = mxCellCustom.get("mxGeometry", JsonValue.NULL).getJsonObject();

            JsonValue xCustom = customGeometry.get("x");
            JsonValue yCustom = customGeometry.get("y");

            mxGeometryForged = xCustom == null ? mxGeometryForged : mxGeometryForged.put("x", xCustom);
            mxGeometryForged = yCustom == null ? mxGeometryForged : mxGeometryForged.put("y", yCustom);
            JsonValue customGeometryArray = customGeometry.get("Array");

            if (customGeometryArray != null) {
                mxGeometryForged = mxGeometryForged.put("Array", customGeometryArray);
            }

            forged = forged.put("mxGeometry", mxGeometryForged);

        }
        return forged;
    }

    private static JsonObject mergeMxCells(JsonObject mxCell1, JsonObject mxCell2) {
        //JsonValue mergedStyle = StyleMerger.merge(mxCell1.get("style"), mxCell2.get("style"));
        return merger.merge(mxCell1, mxCell2)
                //.put("style", mergedStyle)
                ;
    }
}
