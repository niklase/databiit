package com.zuunr.diagrammaker;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.zuunr.json.JsonArray;

import java.util.Map;

public class DrawioParser {

    public static void main(String[] args) throws Exception {
        XmlMapper xmlMapper = new XmlMapper();

        // Deserialize XML to a Map
        Map<String, Object> data = xmlMapper.readValue(DrawioParser.class.getResourceAsStream("/diagram.drawio"), Map.class);

        System.out.println("Parsed Data: " + data);

        JsonArray rootPath = JsonArray.of("diagram", "mxGraphModel", "root");

        Map<String, Object> root = getElement(data, rootPath);

        // Modify the data
        // data.put("newKey", "newValue");

        // Serialize Map back to XML
        String updatedXml = xmlMapper.writeValueAsString(data);
        System.out.println("Updated XML: " + updatedXml);
    }

    public static String getAttribute(Map<String, Object> data, JsonArray path) {
        return (String) getDrawioData(data, path);
    }

    public static Map<String, Object> getElement(Map<String, Object> data, JsonArray path) {
        return (Map<String, Object>) getDrawioData(data, path);
    }

    public static Object getDrawioData(Map data, JsonArray path) {

        Object result = data;
        for (int i = 0; i < path.size(); i++) {
            result = ((Map<String, Object>) result).get(path.get(i).getString());
        }
        return result;
    }
}