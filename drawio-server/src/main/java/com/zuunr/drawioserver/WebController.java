package com.zuunr.drawioserver;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class WebController {

    @GetMapping("/web/{pageName}")
    public String getPage(@PathVariable String pageName, Model model) {
        // Add data to the model if needed
        model.addAttribute("message", "Hello from FreeMarker!");

        // Return the FreeMarker file located under /resources/template/
        // (e.g., if pageName = "schema-editor", it will look for schema-editor.ftl)
        return pageName;
    }
}