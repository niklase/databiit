package com.zuunr.diagrammaker;

import com.zuunr.json.JsonArray;
import com.zuunr.json.JsonObject;
import com.zuunr.json.JsonValue;

import java.util.ArrayList;
import java.util.Map;

public class JsonXmlSerializer {


    protected String serialize(JsonArray pathToHere, JsonObject jsonObject) {
        if (pathToHere.isEmpty()) {
            return serialize(JsonArray.of(jsonObject.keys().head()), jsonObject.values().head().getJsonObject());
        }

        String elementName = pathToHere.last().isString() ? pathToHere.last().getString() : pathToHere.allButLast().last().getString();
        StringBuilder xml = new StringBuilder("<").append(elementName);
        ArrayList<Map.Entry<String, JsonValue>> nestedXml = new ArrayList<>();
        for (Map.Entry<String, JsonValue> entry : jsonObject.asMap().entrySet()) {
            if (entry.getValue().isString()) {
                xml.append(" ").append(entry.getKey()).append("=").append(entry.getValue());
            } else {
                nestedXml.add(entry);
            }
        }

        if (nestedXml.isEmpty()) {
            return xml.append(" />").toString();
        } else {
            xml.append(">");

            for (int i = 0; i < nestedXml.size(); i++) {
                Map.Entry<String, JsonValue> entry = nestedXml.get(i);
                if (entry.getValue().isJsonObject()) {
                    //xml.append("<").append(entry.getKey()).append(">").append(serialize(pathToHere.add(entry.getKey()), entry.getValue())).append("</" + entry.getKey() + ">");
                    xml.append(serialize(pathToHere.add(entry.getKey()), entry.getValue()));
                } else {
                    xml.append(serialize(pathToHere.add(entry.getKey()), entry.getValue()));
                }
            }

            xml.append("</").append(elementName).append(">");
            return xml.toString();
        }
    }

    protected String serialize(JsonArray pathToHere, JsonArray jsonArray) {
        StringBuilder xml = new StringBuilder();
        for (int i = 0; i < jsonArray.size(); i++) {
            xml.append(serialize(pathToHere, jsonArray.get(i)));
        }
        return xml.toString();
    }

    public String serialize(JsonValue jsonValue) {
        return serialize(JsonArray.EMPTY, jsonValue);
    }

    public String serialize(JsonArray pathToHere, JsonValue jsonValue) {
        if (jsonValue.isJsonObject()) {
            return serialize(pathToHere, jsonValue.getJsonObject());
        } else if (jsonValue.isJsonArray()) {
            return serialize(pathToHere, jsonValue.getJsonArray());
        } else {
            return "\"" + jsonValue.getValue() + "\"";
        }
    }

    /**
     * <p>Escapes String as described in https://json.org</p>
     *
     * @param strVal
     * @return
     */
    public String escapeString(String strVal) {

        StringBuilder escapedValueBuilder = new StringBuilder();

        for (int i = 0; i < strVal.length(); i++) {
            char current = strVal.charAt(i);
            if (current == '\n') {
                escapedValueBuilder.append("\\n");
            } else if (current == '\r') {
                escapedValueBuilder.append("\\r");
            } else if (current == '\f') {
                escapedValueBuilder.append("\\f");
            } else if (current == '\b') {
                escapedValueBuilder.append("\\b");
            } else if (current == '\t') {
                escapedValueBuilder.append("\\t");
            } else if (current == '\"') {
                escapedValueBuilder.append("\\\"");
            } else if (current == '\\') {
                escapedValueBuilder.append("\\\\");
            } else {
                escapedValueBuilder.append(current);
            }
        }
        return escapedValueBuilder.toString();
    }
}
