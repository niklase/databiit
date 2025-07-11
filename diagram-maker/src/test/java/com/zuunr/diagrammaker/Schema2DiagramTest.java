package com.zuunr.diagrammaker;

import com.zuunr.json.*;
import com.zuunr.json.pointer.JsonPointer;
import com.zuunr.json.schema.Keywords;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Schema2DiagramTest {

    @Test
    void test() {
        JsonValue personSchema = JsonValueFactory.create("""
                {
                    "properties": {
                        "firstName": {"type": "string"},
                        "lastName": {"type": "string"},
                        "home": {"type": "object", "properties": {"street": {"type": "string"}, "country": {"type": "object", "properties": {"name": {"type": "string"}}}}}
                    }
                }
                """);

        JsonObject flatSchema = JsonSchemaFlattener.flatten(JsonArray.EMPTY, personSchema);

        JsonValue mxCell = JsonValueFactory.create("""
                {
                              "mxGeometry": {
                                "width": "140",
                                "height": "60",
                                "x": "90",
                                "y": "430",
                                "as": "geometry"
                              },
                              "parent": "1",
                              "id": "other",
                              "style": "swimlane;fontStyle=0;childLayout=stackLayout;horizontal=1;startSize=30;horizontalStack=0;resizeParent=1;resizeParentMax=0;resizeLast=0;collapsible=1;marginBottom=0;whiteSpace=wrap;html=1;",
                              "value": "Person",
                              "vertex": "1"
                            }
                """);

        JsonObject schemaRegister = JsonObject.EMPTY.put("https://example.com/schemas/person", personSchema);

        // JsonArray mxCells = createMxCellsForOneSchema(flatSchema, JsonArray.of(""));
        JsonArray mxCells = createMxCells(flatSchema);

        String xml = new JsonXmlSerializer().serialize(JsonObject.EMPTY.put("mxGraphModel", JsonObject.EMPTY
                .put("root", JsonObject.EMPTY
                        .put("mxCell", mxCells))
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
                .put("shadow", "0")
        ).jsonValue());

        String xmlStart = """
                <mxfile host="drawio-plugin" agent="Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36" modified="2025-05-24T21:34:12.935Z" version="22.1.22" etag="M-mlRtzzpZpIx5QfoOWe" type="embed">
                  <diagram name="Page-1" id="AKPzqUBiTfoBOGSDBTsb">
                """;

        String result = new StringBuilder()
                .append(xmlStart)
                .append(xml)
                .append("</diagram></mxfile>").toString();

        System.out.print("result: " + result);
    }

    private JsonArray createMxCellsForOneSchema(JsonObject schemaRegister, JsonPointer schemaRegisterId) {
        String clearTextId = schemaRegisterId.getJsonPointerString().getString();
        JsonValue mxCellId = encodeId(clearTextId);
        JsonObject schema = schemaRegister.get(schemaRegisterId.getJsonPointerString().getString()).getJsonObject();

        JsonObject mxCell = JsonObject.EMPTY
                .put("id", mxCellId)
                .put("mxGeometry", JsonObject.EMPTY
                        .put("width", "140")
                        .put("height", "HEIGHT")
                        .put("x", "90")
                        .put("y", "430")
                        .put("as", "geometry"))
                .put("parent", "1")
                .put("style", "swimlane;fontStyle=0;childLayout=stackLayout;horizontal=1;startSize=30;horizontalStack=0;resizeParent=1;resizeParentMax=0;resizeLast=0;collapsible=1;marginBottom=0;whiteSpace=wrap;html=1;")
                .put("value", schema.get("title", schemaRegisterId.getJsonPointerString().getString().isEmpty()
                        ? JsonValue.of("_ROOT_")
                        : schemaRegisterId.asArray().last()).getString())
                .put("vertex", "1");

        JsonArray mxCellsBuilder = JsonArray.EMPTY;
        JsonObject properties = schema.get("properties", JsonObject.EMPTY).getJsonObject();
        JsonArray propertyKeys = properties.keys().sort();

        int primitivePropertyIndex = 0;
        for (int i = 0; i < propertyKeys.size(); i++) {
            String key = propertyKeys.get(i).getString();
            JsonValue value = properties.get(key);

            JsonObject primitivePropertyMxCell = mxCellOfPrimitiveProperty(key, value);
            if (primitivePropertyMxCell != null) {
                mxCellsBuilder = mxCellsBuilder
                        .add(primitivePropertyMxCell
                                .put("id", encodeId(mxCellId + "#" + key))
                                .put("parent", mxCellId)
                                .put(JsonArray.of("mxGeometry", "y"), "" + (30 * (primitivePropertyIndex + 1)))
                        );
                primitivePropertyIndex++;
            } else {
                // Create arrow
                JsonObject mxCellOfObjectProperty = JsonObject.EMPTY
                        .put("id", encodeId(mxCellId + "#" + key))
                        .put("value", key)
                        .put("style", "edgeStyle=orthogonalEdgeStyle;rounded=0;orthogonalLoop=1;jettySize=auto;html=1;")
                        .put("edge", "1")
                        .put("parent", "1")
                        .put("source", mxCellId)
                        .put("mxGeometry", JsonObject.EMPTY.put("relative", "1").put("as", "geometry"));
                //<mxCell id="8-51mRvOmae2Kr2BzAAm-1" value="qwerty" style="edgeStyle=orthogonalEdgeStyle;rounded=0;orthogonalLoop=1;jettySize=auto;html=1;" edge="1" parent="1" source="Lw==" target="L34xaG9tZX4xY291bnRyeQ==">
                String plainId = schemaRegisterId.asArray().add(key).as(JsonPointer.class).getJsonPointerString().getString();
                JsonValue target = encodeId(plainId);
                mxCellOfObjectProperty = mxCellOfObjectProperty.put("target", target);
                mxCellsBuilder = mxCellsBuilder.add(mxCellOfObjectProperty);
            }
        }

        mxCell = mxCell.put(JsonArray.of("mxGeometry", "height"), "" + (30 * (mxCellsBuilder.size() + 1)));
        return mxCellsBuilder.addFirst(mxCell);
    }

    private JsonObject mxCellOfPrimitiveProperty(String propertyName, JsonValue schema) {

        JsonValue type = schema.get("type");
        if (type.isString()) {
            if (Keywords.OBJECT.equals(type.getString()) || Keywords.ARRAY.equals(type.getString())) {
                return null;
            }
        }
        if (type.isJsonArray()) {
            return null;
        }

        JsonObject mxCell = JsonValueFactory.create("""
                {
                    "mxGeometry": {
                        "width": "140",
                        "height": "30",
                        "y": "30",
                        "as": "geometry"
                    },
                    "parent": "PARENT",
                    "id": "ID",
                    "style": "text;strokeColor=none;fillColor=none;align=left;verticalAlign=middle;spacingLeft=4;spacingRight=4;overflow=hidden;points=[[0,0.5],[1,0.5]];portConstraint=eastwest;rotatable=0;whiteSpace=wrap;html=1;",
                    "value": "firstName",
                    "vertex": "1"
                }
                """).getJsonObject();

        mxCell = mxCell.put("value", propertyName + ": " + schema.get("type").getString());
        return mxCell;
    }

    public JsonObject mxCellOfObjectProperty(String propertyName, JsonValue schema){
        JsonObject mxCell = JsonValueFactory.create("""
                {
                    "mxGeometry": {
                        "width": "50",
                        "height": "50",
                        "relative": "1",
                        "as": "geometry"
                    },
                    "parent": "1",
                    "id": "ID",
                    "style": "endArrow=open;html=1;rounded=0;exitX=0.5;exitY=1;exitDx=0;exitDy=0;entryX=0;entryY=0.75;entryDx=0;entryDy=0;endFill=0;",
                    "value": "",
                    "vertex": "1",
                    "edge": "1"
                    "source": "SOURCE_ID",
                    "target": "TARGET_ID"
                }
                """).getJsonObject();

        JsonValue ref = schema.get("$ref");
        // mx
        return mxCell;

    }

    public JsonArray createMxCells(JsonObject schemaRegistry) {
        JsonArrayBuilder builder = JsonArray.EMPTY.builder()
                .add(JsonObject.EMPTY.put("id", "0"))
                .add(JsonObject.EMPTY.put("id", "1").put("parent", "0"));

        JsonArray sortedKeys = schemaRegistry.keys().sort();
        for (JsonValue key : sortedKeys) {
            JsonArray schemaCells = createMxCellsForOneSchema(schemaRegistry, key.as(JsonPointer.class));
            builder.addAll(schemaCells);
        }

        return builder.build();
    }

    public JsonValue encodeId(String clearTextId) {
        return JsonValue.of(Base64.getEncoder().encodeToString(clearTextId.getBytes(StandardCharsets.UTF_8)));
    }
}
