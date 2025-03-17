package com.zuunr.mermaidschema;

import com.zuunr.json.JsonObject;
import com.zuunr.json.JsonValue;
import com.zuunr.json.JsonValueFactory;
import freemarker.template.Configuration;
import freemarker.template.Template;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;

@RestController
public class MermaidController {

    private Configuration freemarkerConf;

    public MermaidController(@Autowired Configuration freemarkerConf) {
        this.freemarkerConf = freemarkerConf;
    }

    @GetMapping(value = "/class-diagram", produces = "text/html")
    public HttpServletResponse getClassDiagram(HttpServletResponse response) {
        try {

            /* ------------------------------------------------------------------------ */
            /* You usually do these for MULTIPLE TIMES in the application life-cycle:   */

            /* Create a data-model */
            JsonValue schema = JsonValueFactory.create("""
                {
                  "$ref": "/$defs/Person",
                  "$defs": {
                    "Person": {
                      "type": "object",
                      "properties": {
                        "name": { "type": "string" },
                        "age": { "type": "integer" },
                        "contact": {"$ref": "/$defs/Address"},
                        "friends": {
                            "type": "array",
                            "items": {"$ref": "/$defs/Person"}
                        }
                      }
                    },
                    "Address": {
                        "type": "object",
                        "properties": {
                          "street": { "type": "string" }
                        }
                    }
                  }
                }
                
                """);

            /* Get the template (uses cache internally) */
            Template template = freemarkerConf.getTemplate("mermaid.ftlh");

            /* Merge data-model with template */
            JsonObject model = JsonObject.EMPTY.put("classDiagram", schema.as(MermaidClassDiagram.class).toString());
            template.process(model.asMapsAndLists(), response.getWriter());
            // Note: Depending on what `out` is, you may need to call `out.close()`.
            // This is usually the case for file output, but not for servlet output.

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(500);
        }
        return response;
    }
}
