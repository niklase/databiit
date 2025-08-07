package com.zuunr.diagrammaker;

import java.io.*;
import java.util.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

public class AISolution {

    private static int xPosition = 20; // Initial x position for elements in the diagram
    private static int yPosition = 20; // Initial y position for elements in the diagram
    private static final int X_INCREMENT = 200; // Space between columns
    private static final int Y_INCREMENT = 100; // Space between rows

    public static void main(String[] args) throws Exception {
        // Example: JSON Schema
        Map<String, Object> schema = loadSampleSchema();

        // Generate Draw.io Compatible XML Document
        Document doc = createDrawioDocument(schema);

        // Save the XML to a file
        saveDocumentToFile(doc, "class_diagram.drawio");
        System.out.println("Class diagram saved as 'class_diagram.drawio'");
    }

    private static Map<String, Object> loadSampleSchema() {
        Map<String, Object> schema = new HashMap<>();
        schema.put("title", "ExampleClass");
        schema.put("type", "object");
        Map<String, Object> properties = new HashMap<>();
        properties.put("name", Map.of("type", "string"));
        properties.put("age", Map.of("type", "number"));
        properties.put("address", Map.of("$ref", "#/definitions/Address"));
        schema.put("properties", properties);
        Map<String, Object> definitions = new HashMap<>();
        definitions.put("Address", Map.of("type", "object", "properties", Map.of(
                "street", Map.of("type", "string"),
                "city", Map.of("type", "string")
        )));
        schema.put("definitions", definitions);
        return schema;
    }

    private static Document createDrawioDocument(Map<String, Object> schema) throws Exception {
        // Initialize XML Document
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();

        // Create Drawing Root
        Element mxfile = doc.createElement("mxfile");
        Element diagram = doc.createElement("diagram");
        diagram.setAttribute("name", "ClassDiagram");
        mxfile.appendChild(diagram);
        Element mxGraphModel = doc.createElement("mxGraphModel");
        Element root = doc.createElement("root");
        mxGraphModel.appendChild(root);
        diagram.appendChild(mxGraphModel);
        doc.appendChild(mxfile);

        // Add Basic Elements
        createBaseDiagramElements(root, doc);

        // Generate Diagram
        Map<String, Element> classElements = new HashMap<>();
        traverseSchema(schema, doc, root, classElements);

        return doc;
    }

    private static void traverseSchema(Map<String, Object> schema, Document doc, Element root, Map<String, Element> classElements) {
        String title = (String) schema.getOrDefault("title", "UnnamedClass");
        Element classElement = createClassElement(doc, title);
        root.appendChild(classElement);
        classElements.put(title, classElement);

        if (schema.containsKey("properties")) {
            Map<String, Object> properties = (Map<String, Object>) schema.get("properties");
            for (Map.Entry<String, Object> entry : properties.entrySet()) {
                String propertyName = entry.getKey();
                Map<String, Object> propertySchema = (Map<String, Object>) entry.getValue();
                if (propertySchema.containsKey("$ref")) {
                    String ref = (String) propertySchema.get("$ref");
                    String refTitle = ref.substring(ref.lastIndexOf("/") + 1); // Extract Title
                    Element refElement = classElements.get(refTitle);
                    if (refElement != null) {
                        createArrowElement(doc, root, classElement, refElement);
                    }
                } else {
                    createPropertyElement(doc, propertyName, classElement);
                }
            }
        }
    }

    private static void createPropertyElement(Document doc, String propertyName, Element classElement) {
        Element propertyElement = doc.createElement("property");
        propertyElement.setAttribute("label", propertyName);
        classElement.appendChild(propertyElement);
    }

    private static void createArrowElement(Document doc, Element root, Element source, Element target) {
        Element arrow = doc.createElement("mxCell");
        arrow.setAttribute("edge", "1");
        arrow.setAttribute("source", source.getAttribute("id"));
        arrow.setAttribute("target", target.getAttribute("id"));
        root.appendChild(arrow);
    }

    private static Element createClassElement(Document doc, String title) {
        Element classElement = doc.createElement("mxCell");
        classElement.setAttribute("id", title);
        classElement.setAttribute("value", title);
        classElement.setAttribute("geometry", "x=" + xPosition + ";y=" + yPosition);
        xPosition += X_INCREMENT;
        yPosition += Y_INCREMENT;
        return classElement;
    }

    private static void createBaseDiagramElements(Element root, Document doc) {
        Element defaultParent = doc.createElement("mxCell");
        defaultParent.setAttribute("id", "0");
        root.appendChild(defaultParent);

        Element layer = doc.createElement("mxCell");
        layer.setAttribute("id", "1");
        layer.setAttribute("parent", "0");
        root.appendChild(layer);
    }

    private static void saveDocumentToFile(Document doc, String fileName) throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(fileName));
        transformer.transform(source, result);
    }
}