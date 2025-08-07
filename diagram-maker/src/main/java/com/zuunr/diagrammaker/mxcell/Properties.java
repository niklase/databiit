package com.zuunr.diagrammaker.mxcell;

import com.zuunr.json.JsonArray;
import com.zuunr.json.JsonArrayBuilder;
import com.zuunr.json.JsonObject;
import com.zuunr.json.JsonValue;
import com.zuunr.json.pointer.JsonPointer;

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

            boolean isRef = propertySchema.get("$ref") != null;
            boolean isObject = "object".equals(propertySchema.get("type", JsonValue.NULL).getString());
            boolean isArray = "array".equals(propertySchema.get("type", JsonValue.NULL).getString());

            if (isRef || isObject || isArray) {
                // This property is represented as an arrow to another swimlane

                String arrowId = MxCell.createId(pathToSchema).getString() + "#" + propertyName.getString();

                JsonPointer targetJsonPointer = isRef
                        ? propertySchema.get("$ref").as(JsonPointer.class)
                        : pathToPropertySchema.as(JsonPointer.class);

                JsonValue targetId = MxCell.createId(targetJsonPointer.asArray());

                propertiesBuilder.add(Templates.ARROW
                        .put("id", arrowId)
                        .put("source", MxCell.createId(pathToSchema))
                        .put("target", targetId)
                        .put("value", propertyName)
                );

                if (isObject || isArray) {
                    propertiesBuilder.addAll(Schema.mxCellsOf(propertySchemas.get(i).getJsonObject().put("title", isObject ? "[object]" : "[array]").jsonValue(), rootDoc, pathToPropertySchema));
                }
            } else if (propertySchema.get("type") == null || propertySchema.get("type").isString()
                    && !"object".equals(propertySchema.get("type").getString())
                    && !"array".equals(propertySchema.get("type").getString())) {
                // This property schema should be inlined


                propertiesBuilder.add(Templates.SWIMLANE_ITEM_TEMPLATE
                        .put("parent", MxCell.createId(pathToSchema))
                        .put("id", MxCell.createId(pathToPropertySchema))
                        .put("value", propertyName.getString() + ": " + propertySchema.get("type", "").getString())
                        .put(JsonArray.of("mxGeometry", "y"), "" + (swimlaneItems++ * SWIMLANE_ITEM_HEIGHT + SWIMLANE_ITEM_HEIGHT))
                );
            } else {
                throw new RuntimeException("Unsupported property type: " + propertySchema.get("type", JsonValue.NULL).getString());
            }

        }
        return new MxCellsWrapper<>(propertiesBuilder.build(), swimlaneItems);

    }

}
