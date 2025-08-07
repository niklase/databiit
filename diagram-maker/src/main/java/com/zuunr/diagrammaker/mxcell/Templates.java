package com.zuunr.diagrammaker.mxcell;

import com.zuunr.json.JsonObject;
import com.zuunr.json.JsonValueFactory;

public class Templates {
    public static final JsonObject ARROW = JsonObject.EMPTY
            .put("id", "ID")
            .put("value", "VALUE")
            .put("style", "edgeStyle=orthogonalEdgeStyle;rounded=0;orthogonalLoop=1;jettySize=auto;html=1;endArrow=open;endFill=0;")
            .put("edge", "1")
            .put("parent", "1")
            .put("source", "SOURCE")
            .put("target", "TARGET")
            .put("mxGeometry", JsonObject.EMPTY
                    .put("relative", "1")
                    .put("as", "geometry"));

    public static final JsonObject SWIMLANE_ITEM_TEMPLATE = JsonValueFactory.create("""
            {
                "mxGeometry": {
                    "width": "140",
                    "height": "30",
                    "y": "Y-POSITION",
                    "as": "geometry"
                },
                "parent": "PARENT",
                "id": "ID",
                "style": "text;strokeColor=none;fillColor=none;align=left;verticalAlign=middle;spacingLeft=4;spacingRight=4;overflow=hidden;points=[[0,0.5],[1,0.5]];portConstraint=eastwest;rotatable=0;whiteSpace=wrap;html=1;",
                "value": "firstName",
                "vertex": "1"
            }
            """).getJsonObject();
}
