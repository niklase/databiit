package com.zuunr.diagrammaker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.zuunr.json.JsonArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Diagram {
    private static XmlMapper xmlMapper = new XmlMapper();

    private static String EMPTY_DIAGRAM_XML = """
            <mxfile host="drawio-plugin" agent="Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36" modified="2025-05-24T21:34:12.935Z" version="22.1.22" etag="M-mlRtzzpZpIx5QfoOWe" type="embed">
              <diagram name="Page-1" id="AKPzqUBiTfoBOGSDBTsb">
                <mxGraphModel dx="599" dy="770" grid="1" gridSize="10" guides="1" tooltips="1" connect="1" arrows="1" fold="1" page="1" pageScale="1" pageWidth="827" pageHeight="1169" math="0" shadow="0">
                  <root>
                    <mxCell id="0" />
                    <mxCell id="1" parent="0" />
                  </root>
                </mxGraphModel>
              </diagram>
            </mxfile>
            
            """;


    private Map<String, Object> diagramMap;
    private ArrayList<Object> mxCellList = new ArrayList<>();
    private Map<String, Object> mxCellMap = new HashMap<>();

    public static Diagram create(String xmlDiagram) {
        return new Diagram(xmlDiagram);
    }

    public static Diagram create() {
        return new Diagram(EMPTY_DIAGRAM_XML);
    }

    private Diagram(String xmlDiagram) {

        diagramMap = deserializeXml(xmlDiagram);
        System.out.println("Parsed Data: " + diagramMap);

        JsonArray rootPath = JsonArray.of("diagram", "mxGraphModel", "root");

        Map<String, Object> root = (Map<String, Object>) getDiagramData(rootPath, null);

        mxCellList = (ArrayList) root.get("mxCell");

        for (Object mxCellObj : mxCellList) {
            Map<String, Object> mxCell = (Map<String, Object>) mxCellObj;
            String id = (String) mxCell.get("id");
            mxCellMap.put(id, mxCell);
        }
    }

    public String toString() {
        try {
            return xmlMapper.writeValueAsString(diagramMap);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private Object getDiagramData(JsonArray path, Object defaultIfNull) {

        Object result = diagramMap;
        for (int i = 0; i < path.size(); i++) {
            result = ((Map<String, Object>) result).get(path.get(i).getString());
            if (result == null) {
                return defaultIfNull;
            }
        }
        return result;
    }

    public Map<String, Object> getMxCell(String id) {
        return (Map<String, Object>) mxCellMap.get(id);
    }

    public Diagram addMxCell(String mxCell) {

        Map<String, Object> map = deserializeXml(mxCell);
        String id = (String ) map.get("id");
        mxCellList.add(map);
        mxCellMap.put(id, map);

        return this;
    }

    private static Map<String, Object> deserializeXml(String xml) {
        // Deserialize XML to a Map

        try {
            return xmlMapper.readValue(xml, Map.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
