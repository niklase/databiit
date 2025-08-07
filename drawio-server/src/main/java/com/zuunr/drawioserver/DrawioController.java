package com.zuunr.drawioserver;

import com.zuunr.diagrammaker.mxcell.XmlDiagramBuilder;
import com.zuunr.json.JsonValueFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class DrawioController {

    // Serve the Draw.io editor page
    @GetMapping("/")
    public String renderEditorPage(Model model) {
        return "drawio";  // Points to a Thymeleaf or HTML file named `drawio.html`
    }

    // Dummy endpoint to load a diagram
    @GetMapping("/load-diagram")
    @ResponseBody
    public String loadDiagram() {
        // Return a dummy diagram in XML format
        /*
        return """
               <mxGraphModel>
                   <root>
                       <mxCell id="0" />
                       <mxCell id="1" parent="0" />
                       <mxCell id="2" value="Hello, Draw.io!" vertex="1" parent="1">
                           <mxGeometry x="20" y="20" width="120" height="40" as="geometry" />
                       </mxCell>
                   </root>
               </mxGraphModel>
               """;
        */
        String jsonSchema = """
                {
                    "$defs": {
                        "Person": {
                            "title": "Person",
                            "type": "object",
                            "properties": {
                                "name": {
                                    "type": "string"
                                },
                                "homeAddress": {
                                    "$ref": "#/$defs/Address"
                                },
                                "pets": {
                                    "type": "object",
                                    "properties": {
                                        "petName": {
                                            "type": "string"
                                        }
                                    }
                                }
                            }
                        },
                        "Address": {
                            "title": "Address",
                            "type": "object",
                            "properties": {
                                "street": {
                                    "type": "string"
                                }
                            }
                        }
                    }
                }
                """;

        return XmlDiagramBuilder.xmlDiagramOf(JsonValueFactory.create(jsonSchema));
    }

    // Dummy endpoint to save a diagram
    @PostMapping("/save-diagram")
    public void saveDiagram(@RequestBody String diagram, HttpServletResponse response) throws IOException {
        // Log the diagram XML (In a real app, persist it to a database or file system)
        System.out.println("Received Diagram XML:\n" + diagram);

        // Respond with success
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write("Diagram saved successfully!");
    }
}