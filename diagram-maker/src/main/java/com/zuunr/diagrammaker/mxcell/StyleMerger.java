package com.zuunr.diagrammaker.mxcell;

import com.zuunr.json.*;
import com.zuunr.json.merge.MergeStrategy;

public class StyleMerger {

    private static JsonObjectMerger merger = new JsonObjectMerger(MergeStrategy.NULL_AS_VALUE_AND_ARRAY_AS_ATOM);

    public static JsonValue merge(JsonValue css1, JsonValue css2) {

        css1 = css1 == null ? JsonValue.EMPTY_STRING : css1;
        css2 = css2 == null ? JsonValue.EMPTY_STRING : css2;

        JsonObject css1JsonValue = styleAsJsonObject(css1.getString());
        JsonObject css2JsonValue = styleAsJsonObject(css2.getString());
        JsonObject mergedCss = merger.merge(css1JsonValue, css2JsonValue);

        return JsonValue.of(styleObjectAsString(mergedCss));
    }

    public static final String styleObjectAsString(JsonObject styleObject) {
        StringBuilder mergedStyle = new StringBuilder();
        JsonArray sortedKeys = styleObject.keys().sort();
        for (JsonValue key : sortedKeys) {
            JsonValue value = styleObject.get(key.getString());
            mergedStyle.append(key.getString());
            if (!value.isNull()) {
                mergedStyle.append("=").append(styleObject.get(key.getString()).getString());
            }
            mergedStyle.append(";");
        }
        return mergedStyle.toString();
    }

    public static JsonObject styleAsJsonObject(String styleString) {

        if (styleString.isEmpty()) {
            return JsonObject.EMPTY;
        }
        JsonObjectBuilder builder = JsonObject.EMPTY.builder();
        String[] semiSplit = styleString.split(";");

        for (String s : semiSplit) {
            int index = s.indexOf("=");

            String key;
            JsonValue value = JsonValue.NULL;
            if (index == -1) {
                key = s;
            } else {
                key = s.substring(0, index);
                value = JsonValue.of(s.substring(index + 1));
            }
            builder.put(key, value);
        }
        return builder.build();
    }
}
