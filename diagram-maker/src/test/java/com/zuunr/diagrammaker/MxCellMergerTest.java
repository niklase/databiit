package com.zuunr.diagrammaker;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.zuunr.json.JsonArray;
import com.zuunr.json.JsonObject;
import com.zuunr.json.JsonValue;
import com.zuunr.json.JsonValueFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MxCellMergerTest {

    private static XmlMapper xmlMapper = new XmlMapper();

    private static String DRAWIO_XML = """
            <mxfile host="drawio-plugin" agent="Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36" modified="2025-05-24T21:34:12.935Z" version="22.1.22" etag="M-mlRtzzpZpIx5QfoOWe" type="embed">
              <diagram name="Page-1" id="AKPzqUBiTfoBOGSDBTsb">
                <mxGraphModel dx="599" dy="770" grid="1" gridSize="10" guides="1" tooltips="1" connect="1" arrows="1" fold="1" page="1" pageScale="1" pageWidth="827" pageHeight="1169" math="0" shadow="0">
                  <root>
                    <mxCell id="0" />
                    <mxCell id="1" parent="0" />
                    <mxCell id="##ID##" value="##VALUE##" style="swimlane;fontStyle=0;childLayout=stackLayout;horizontal=1;startSize=30;horizontalStack=0;resizeParent=1;resizeParentMax=0;resizeLast=0;collapsible=1;marginBottom=0;whiteSpace=wrap;html=1;" parent="1" vertex="1">
                      <mxGeometry x="90" y="430" width="140" height="60" as="geometry" />
                    </mxCell>
                  </root>
                </mxGraphModel>
              </diagram>
            </mxfile>
            """;

    private static JsonValue DRAWIO_TEMPLATE = drawioTemplate("##ID##", "##VALUE##");

    private static JsonValue drawioTemplate(String cellId, String value) {
        try {
            JsonNode jsonNode = xmlMapper.readTree("<wrapper>" + DRAWIO_XML.replace("##ID##", cellId).replace("##VALUE##", value) + "</wrapper>");
            JsonValue fileAsJson = JsonValueFactory.create(jsonNode.toString());
            return fileAsJson;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private static JsonArray pathToMxCells = JsonArray.of("mxfile", "diagram", "mxGraphModel", "root", "mxCell");

    private static MxCellMerger mxCellMerger = new MxCellMerger();

    private static JsonArray renderMxCells(String cellId, String value) {
        JsonValue fileAsJson = drawioTemplate(cellId, value);
        JsonArray mxCells = fileAsJson.get(pathToMxCells).getJsonArray();
        return mxCells;
    }



    @Test
    void customValueOverridesNonAutoAnnotatedIdOfAutomaticDrawio() throws Exception {
        JsonArray result = test(JsonObject.EMPTY
                        .put("id", "someid-7")
                        .put("value", "CUSTOM VALUE"),
                JsonObject.EMPTY
                        .put("id", "someid-7")
                        .put("value", "AUTO VALUE"));
        assertEquals(JsonValue.of("CUSTOM VALUE"), result.get(2).get("value"));
    }

    @Test
    void customValueOverriddenbyAutoAnnotatedIdOfAutomaticDrawio() throws Exception {
        JsonArray result = test(JsonObject.EMPTY
                        .put("id", "auto:someid-7")
                        .put("value", "CUSTOM VALUE"),
                JsonObject.EMPTY
                        .put("id", "auto:someid-7")
                        .put("value", "AUTO VALUE"));
        assertEquals(JsonValue.of("AUTO VALUE"), result.get(2).get("value"));
    }

    @Test
    void autoValueGetsAddedWhenThereIsNoCustomValue() throws Exception {
        JsonArray result = test(JsonObject.EMPTY
                        .put("id", "someid-7")
                        .put("value", "CUSTOM VALUE"),
                JsonObject.EMPTY
                        .put("id", "auto:someid-7")
                        .put("value", "AUTO VALUE"));

        assertEquals(result.size(), 4, "both the custom value and the auto value should be present in the result.");
        boolean autoValueFound = false;
        boolean customValueFound = false;
        for (JsonValue cell : result) {
            if (cell.get("id").getString().equals("auto:someid-7")) {
                assertEquals(JsonValue.of("AUTO VALUE"), cell.get("value"));
                autoValueFound = true;

            }
            if (cell.get("id").getString().equals("someid-7")) {
                assertEquals(JsonValue.of("CUSTOM VALUE"), cell.get("value"));
                customValueFound = true;

            }

        }
        assertEquals(true, autoValueFound, "The automatically generated id was not found in the merged result.");
        assertEquals(true, customValueFound, "The custom value was not found in the merged result.");
    }


    JsonArray test(JsonObject custom, JsonObject auto) throws Exception {

        JsonArray mxCellsAuto = renderMxCells(auto.get("id").getString(), auto.get("value").getString());
        JsonArray mxCellsCustom = renderMxCells(custom.get("id").getString(), custom.get("value").getString());

        JsonArray mergedMxCells = mxCellMerger.merge(mxCellsCustom, mxCellsAuto);

        JsonValue mergedFilesAsJson = DRAWIO_TEMPLATE.put(pathToMxCells, mergedMxCells.jsonValue());


        JsonXmlSerializer xmlSerializer = new JsonXmlSerializer();
        String mergedXml = xmlSerializer.serialize(mergedFilesAsJson);
        System.out.println("MERGED XML: ");
        System.out.println(mergedXml);
        System.out.println("");

        return mergedMxCells;
    }


}
