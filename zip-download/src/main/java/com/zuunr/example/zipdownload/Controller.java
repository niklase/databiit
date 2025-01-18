package com.zuunr.example.zipdownload;

import com.zuunr.json.JsonObject;
import com.zuunr.json.JsonValue;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class Controller {

    @GetMapping("/zip-file")
    public void getZip(HttpServletResponse response) throws IOException {
        JsonObject classes = JsonObject.EMPTY.put("com.example.MyClass", "package com.example;\n\npublic class MyClass {\n\n}");
        response.setHeader(HttpHeaders.CONTENT_TYPE, "application/zip");
        ZipFileCreator.createZipFromMap(convertJsonObjectToMap(classes), response.getOutputStream());
    }

    private Map<String, String> convertJsonObjectToMap(JsonObject jsonObject) {
        return jsonObject.asMapsAndLists().entrySet()
                .stream()
                .collect(Collectors.toMap(
                        entry -> (String) entry.getKey(),
                        entry -> (String) entry.getValue()
                ));
    }
}
