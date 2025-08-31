package com.zuunr.diagrammaker.mxcell;

import com.zuunr.diagrammaker.JsonXmlSerializer;
import com.zuunr.json.JsonArray;
import com.zuunr.json.JsonObject;
import com.zuunr.json.JsonValue;

public class XmlDiagramBuilder {

    public static final String xmlDiagramOf(JsonValue jsonSchema) {

        JsonObject mxGraphModel = JsonObject.EMPTY.put("mxGraphModel", JsonObject.EMPTY
                .put("root", JsonObject.EMPTY
                        .put("mxCell", JsonArray.of(
                                JsonObject.EMPTY.put("id", "0"),
                                JsonObject.EMPTY.put("id", "1")
                                        .put("value", "layer1")
                                        .put("parent", "0"))
                                .addAll(Schema.mxCellsOf(jsonSchema))))
                .put("dx", "599")
                .put("dy", "770")
                .put("grid", "1")
                .put("gridSize", "10")
                .put("guides", "1")
                .put("tooltips", "1")
                .put("connect", "1")
                .put("arrows", "1")
                .put("fold", "1")
                .put("page", "1")
                .put("pageScale", "1")
                .put("pageWidth", "827")
                .put("pageHeight", "1169")
                .put("math", "0")
                .put("shadow", "0"));

        String xml = new JsonXmlSerializer().serialize(mxGraphModel.jsonValue());
        return new StringBuilder("""
                <mxfile host="embed.diagrams.net" agent="Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:138.0) Gecko/20100101 Firefox/138.0" version="28.0.6">
                <diagram id="O5m0Za5ys5D574SArSo_" name="Page-1">
                """)
                .append(xml)
                .append("</diagram></mxfile>").toString();
    }
}
