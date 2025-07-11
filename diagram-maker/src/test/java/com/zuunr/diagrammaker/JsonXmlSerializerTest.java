package com.zuunr.diagrammaker;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.zuunr.json.JsonValue;
import com.zuunr.json.JsonValueFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonXmlSerializerTest {

    JsonValueFactory jsonValueFactory = new JsonValueFactory();

    @Test
    void readTreeTest() throws Exception {
        XmlMapper mapper = new XmlMapper();
        String mxCell1 = """
                <mxfile host="drawio-plugin" agent="Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36" modified="2025-05-24T21:34:12.935Z" version="22.1.22" etag="M-mlRtzzpZpIx5QfoOWe" type="embed">
                           <diagram name="Page-1" id="AKPzqUBiTfoBOGSDBTsb">
                             <mxGraphModel dx="599" dy="770" grid="1" gridSize="10" guides="1" tooltips="1" connect="1" arrows="1" fold="1" page="1" pageScale="1" pageWidth="827" pageHeight="1169" math="0" shadow="0">
                               <root>
                                 <mxCell id="0" />
                                 <mxCell id="1" parent="0" />
                                 <mxCell id="4vKqbE8X5UF8rX6KiNEY-9" value="Address" style="swimlane;fontStyle=0;childLayout=stackLayout;horizontal=1;startSize=30;horizontalStack=0;resizeParent=1;resizeParentMax=0;resizeLast=0;collapsible=1;marginBottom=0;whiteSpace=wrap;html=1;" parent="1" vertex="1">
                                   <mxGeometry x="360" y="495" width="140" height="90" as="geometry" />
                                 </mxCell>
                                 <mxCell id="4vKqbE8X5UF8rX6KiNEY-11" value="street" style="text;strokeColor=none;fillColor=none;align=left;verticalAlign=middle;spacingLeft=4;spacingRight=4;overflow=hidden;points=[[0,0.5],[1,0.5]];portConstraint=eastwest;rotatable=0;whiteSpace=wrap;html=1;" parent="4vKqbE8X5UF8rX6KiNEY-9" vertex="1">
                                   <mxGeometry y="30" width="140" height="30" as="geometry" />
                                 </mxCell>
                                 <mxCell id="4vKqbE8X5UF8rX6KiNEY-12" value="country" style="text;strokeColor=none;fillColor=none;align=left;verticalAlign=middle;spacingLeft=4;spacingRight=4;overflow=hidden;points=[[0,0.5],[1,0.5]];portConstraint=eastwest;rotatable=0;whiteSpace=wrap;html=1;" parent="4vKqbE8X5UF8rX6KiNEY-9" vertex="1">
                                   <mxGeometry y="60" width="140" height="30" as="geometry" />
                                 </mxCell>
                                 <mxCell id="other" value="Person" style="swimlane;fontStyle=0;childLayout=stackLayout;horizontal=1;startSize=30;horizontalStack=0;resizeParent=1;resizeParentMax=0;resizeLast=0;collapsible=1;marginBottom=0;whiteSpace=wrap;html=1;" parent="1" vertex="1">
                                   <mxGeometry x="90" y="430" width="140" height="60" as="geometry" />
                                 </mxCell>
                                 <mxCell id="4vKqbE8X5UF8rX6KiNEY-10" value="firstName" style="text;strokeColor=none;fillColor=none;align=left;verticalAlign=middle;spacingLeft=4;spacingRight=4;overflow=hidden;points=[[0,0.5],[1,0.5]];portConstraint=eastwest;rotatable=0;whiteSpace=wrap;html=1;" parent="other" vertex="1">
                                   <mxGeometry y="30" width="140" height="30" as="geometry" />
                                 </mxCell>
                                 <mxCell id="L3sqXbdfWd61IPe8-gYx-1" value="home" style="edgeStyle=orthogonalEdgeStyle;rounded=0;orthogonalLoop=1;jettySize=auto;html=1;entryX=0;entryY=0.25;entryDx=0;entryDy=0;endArrow=open;endFill=0;" parent="1" source="4vKqbE8X5UF8rX6KiNEY-10" target="4vKqbE8X5UF8rX6KiNEY-9" edge="1">
                                   <mxGeometry relative="1" as="geometry" />
                                 </mxCell>
                               </root>
                             </mxGraphModel>
                           </diagram>
                         </mxfile>
                """;

        JsonNode jsonNode1 = mapper.readTree("<wrapper>" + mxCell1 + "</wrapper>");
        JsonValue jsonValue1 = JsonValueFactory.create(jsonNode1.toString());
        JsonXmlSerializer serializer = new JsonXmlSerializer();

        String mxCell2 = serializer.serialize(jsonValue1);

        JsonNode jsonNode2 = mapper.readTree("<wrapper>" + mxCell2 + "</wrapper>");
        JsonValue jsonValue2 = JsonValueFactory.create(jsonNode2.toString());

        assertEquals(jsonValue1, jsonValue2);
    }
}
