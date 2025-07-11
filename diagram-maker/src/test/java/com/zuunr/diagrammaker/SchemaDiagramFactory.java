package com.zuunr.diagrammaker;

import com.zuunr.json.JsonValue;
import com.zuunr.json.schema.JsonSchema;

public class SchemaDiagramFactory {


    private static final String swimlane = """
            <mxCell id="other" value="Other List" style="swimlane;fontStyle=0;childLayout=stackLayout;horizontal=1;startSize=30;horizontalStack=0;resizeParent=1;resizeParentMax=0;resizeLast=0;collapsible=1;marginBottom=0;whiteSpace=wrap;html=1;" vertex="1" parent="1">
                <mxGeometry x="300" y="400" width="140" height="120" as="geometry" />
            </mxCell>
            """;

    private JsonValue schema;

    public static String create(JsonValue schema, String diagramToPatch) {
        return null;
    }
}
