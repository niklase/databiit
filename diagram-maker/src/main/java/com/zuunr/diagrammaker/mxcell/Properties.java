package com.zuunr.diagrammaker.mxcell;

import com.zuunr.json.*;
import com.zuunr.json.pointer.JsonPointer;
import com.zuunr.json.schema.Keywords;

public class Properties {

    public static final int SWIMLANE_ITEM_HEIGHT = 30;

    public static MxCellsWrapper mxCellsOf(JsonObject schema, JsonValue rootDoc, JsonArray pathToSchema) {
        JsonObject properties = schema.get("properties", JsonValue.NULL).getJsonObject();

        if (properties == null) {
            return MxCellsWrapper.EMPTY;
        }

        JsonArray propertyNames = properties.keys();
        JsonArray propertySchemas = properties.values();

        JsonArrayBuilder propertiesBuilder = JsonArray.EMPTY.builder();
        int swimlaneItems = 0;
        for (int i = 0; i < propertyNames.size(); i++) {

            JsonValue propertyName = propertyNames.get(i);
            JsonArray pathToPropertySchema = pathToSchema.add("properties").add(propertyName);
            JsonValue propertySchema = propertySchemas.get(i);

            // The following will only handle inline schemas of type array. Improve by following $ref
            boolean isInlineArray = Keywords.ARRAY.equals(propertySchema.get(Keywords.TYPE, JsonValue.NULL).getString());

            JsonPointer targetJsonPointer = getTargetJsonPointer(propertySchema, pathToPropertySchema, rootDoc);

            if (targetJsonPointer != null) { // There is an object somewhere in the hierarchy of $refs
                // This property is represented as an arrow to another swimlane

                JsonValue targetSchema = rootDoc.get(targetJsonPointer);
                boolean isObjectAtLevelOfAnyDepth = Keywords.OBJECT.equals(targetSchema.get(Keywords.TYPE, "").getString());

                String arrowId = MxCell.createId(pathToSchema).getString() + "#" + propertyName.getString();

                JsonValue targetId = MxCell.createId(targetJsonPointer.asArray());

                propertiesBuilder.add(Templates.ARROW
                        .put("id", arrowId)
                        .put("source", MxCell.createId(pathToSchema))
                        .put("target", targetId)
                        .put("value", propertyName)
                );


                JsonObject multiplicityMxCell = Templates.MULTIPLICITY
                        .put("id", arrowId + "#multiplicity")
                        .put("parent", arrowId);

                if (isInlineArray) {
                    propertiesBuilder.addAll(
                            Schema.mxCellsOf(rootDoc.get(pathToPropertySchema).getJsonObject().put("title", "[array]").jsonValue(), rootDoc, JsonArray.EMPTY));

                    String minItems = "" + propertySchema.get(Keywords.MIN_ITEMS, 0);
                    JsonNumber maxItems = propertySchema.get(Keywords.MAX_ITEMS, -1).getJsonNumber();
                    String multiplicity = minItems + ".." + (JsonValue.MINUS_ONE.equals(maxItems.jsonValue()) ? "*" : maxItems);

                    propertiesBuilder.add(multiplicityMxCell.put("value", multiplicity));
                } else if (isObjectAtLevelOfAnyDepth) {
                    propertiesBuilder.addAll(Schema.mxCellsOf(propertySchemas.get(i).getJsonObject().put("title", "[object]").jsonValue(), rootDoc, pathToPropertySchema));
                    String multiplicity = schema.get(Keywords.REQUIRED, JsonArray.EMPTY).getJsonArray().contains(propertyName) ? "1" : "0..1";
                    propertiesBuilder.add(multiplicityMxCell.put("value", multiplicity));
                }
            } else {
                String leafType = getLeafType(propertySchema, pathToPropertySchema, new StringBuilder());
                propertiesBuilder.add(Templates.SWIMLANE_ITEM_TEMPLATE
                        .put("parent", MxCell.createId(pathToSchema))
                        .put("id", MxCell.createId(pathToPropertySchema))
                        .put("value", propertyName.getString() + ": " + (leafType == null ? "?" : leafType))
                        .put(JsonArray.of("mxGeometry", "y"), "" + (swimlaneItems++ * SWIMLANE_ITEM_HEIGHT + SWIMLANE_ITEM_HEIGHT))
                );
            }

        }
        return new MxCellsWrapper<>(propertiesBuilder.build(), swimlaneItems);
    }

    private static JsonPointer getTargetJsonPointer(JsonValue schema, JsonArray pathToSchema, JsonValue rootDoc) {

        boolean isRef = schema.get("$ref") != null;
        boolean isObject = "object".equals(schema.get("type", JsonValue.NULL).getString());
        boolean isArray = "array".equals(schema.get("type", JsonValue.NULL).getString());

        if (isRef) {
            JsonPointer refJsonPointer = schema.get("$ref").as(JsonPointer.class);
            return getTargetJsonPointer(rootDoc.get(refJsonPointer), refJsonPointer.asArray(), rootDoc);
        } else if (isObject) {
            return pathToSchema.as(JsonPointer.class);
        } else if (isArray) {
            if (schema.get(Keywords.ITEMS) != null) {
                return getTargetJsonPointer(schema.get(Keywords.ITEMS), pathToSchema.add(Keywords.ITEMS), rootDoc);
            }
        } else {  // Is a leaf
            return null;
        }
        return null;

    }

    private static String getLeafType(JsonValue schema, JsonArray pathToSchema, StringBuilder result) {

        boolean isArray = "array".equals(schema.get("type", JsonValue.NULL).getString());

        if (isArray) {
            JsonValue itemsSchema = schema.get(Keywords.ITEMS);
            if (itemsSchema != null){
                return itemsSchema.get(Keywords.TYPE).getString()+"[]";
            } else {
                return "?[]";
            }
        } else {  // Is a leaf
            return schema.get("type", "?").getString();
        }
    }
}
