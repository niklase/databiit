package com.zuunr.diagrammaker;

import com.zuunr.json.*;
import com.zuunr.json.merge.MergeStrategy;

public class StyleMerger {

    private static JsonObjectMerger merger = new JsonObjectMerger(MergeStrategy.NULL_AS_VALUE_AND_ARRAY_AS_ATOM);

    public static JsonValue merge(JsonValue css1, JsonValue css2) {
        JsonObject css1JsonValue = cssAsJsonObject(css1.getString());
        JsonObject css2JsonValue = cssAsJsonObject(css2.getString());
        JsonObject mergedCss = merger.merge(css1JsonValue, css2JsonValue);
        JsonArray sortedKeys = mergedCss.keys().sort();
        StringBuilder mergedStyle = new StringBuilder();
        for (JsonValue key : sortedKeys) {
            JsonValue value = mergedCss.get(key.getString());
            mergedStyle.append(key.getString());
            if (!value.isNull()) {
                mergedStyle.append("=").append(mergedCss.get(key.getString()).getString());
            }
            mergedStyle.append(";");
        }
        return JsonValue.of(mergedStyle.toString());
    }

    private static JsonObject cssAsJsonObject(String css) {

        if (css.isEmpty()) {
            return JsonObject.EMPTY;
        }
        JsonObjectBuilder builder = JsonObject.EMPTY.builder();
        String[] semiSplit = css.split(";");

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
