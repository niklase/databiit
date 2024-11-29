package com.zuunr.example;

import com.zuunr.http.ResponseUtil;
import com.zuunr.json.JsonArray;
import com.zuunr.json.JsonObject;
import com.zuunr.json.JsonObjectBuilder;
import com.zuunr.json.JsonValue;
import com.zuunr.jsontester.GivenWhenThenTesterBase;
import com.zuunr.rest.Request;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.net.URI;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApiTest extends GivenWhenThenTesterBase {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    static Stream<Path> testFiles() throws Exception {
        return testFiles((Class<? extends GivenWhenThenTesterBase>) new Object() {
        }.getClass().getEnclosingClass()); // NOSONAR
    }

    /*
     * This method implementation and annotations may be copied as-is to any other subclass of GivenWhenThenBaseTester
     */
    @DisplayName("Call API")
    @ParameterizedTest(name = "{index} => test file: {0}")
    @MethodSource("testFiles")
    void test(Path testsFolderPath) throws Exception {
        executeTest(testsFolderPath);
    }

    @Override
    public JsonValue doGivenWhen(JsonValue given, JsonValue when) {
        JsonObject jsonRequest = when.getJsonObject();
        Request<JsonValue> request = Request.of(jsonRequest);
        RequestEntity.BodyBuilder bodyBuilder = RequestEntity.method(HttpMethod.valueOf(request.getMethod()), "http://localhost:" + port + request.getURI());
        bodyBuilder.headers(getHeaders(jsonRequest));
        RequestEntity<JsonValue> requestEntity = bodyBuilder.body(request.getBody());


        ResponseEntity<JsonValue> responseEntity = restTemplate.exchange(requestEntity, JsonValue.class);
        return ResponseUtil.createResponse(responseEntity).jsonValue();
    }

    private HttpHeaders getHeaders(JsonObject jsonRequest) {
        HttpHeaders httpHeaders = new HttpHeaders();
        JsonObject headers = jsonRequest.get("headers", JsonObject.EMPTY).getJsonObject();
        JsonArray headerNames = headers.keys();
        JsonArray headerValues = headers.values();

        for (int i = 0; i < headerNames.size(); i++) {
            String headerName = headerNames.get(i).getString();
            JsonArray values = headerValues.get(i).getJsonArray();
            httpHeaders.addAll(headerName, values.stream().map(JsonValue::getString).toList());
        }
        return httpHeaders;
    }

    private JsonObject headersAsLowerCase(JsonObject headers) {
        JsonObjectBuilder headersBuilder = JsonObject.EMPTY.builder();
        JsonArray headerKeys = headers.keys();
        JsonArray headerValues = headers.values();
        for (int i = 0; i < headerKeys.size(); i++) {
            headersBuilder.put(headerKeys.get(i).getString().toLowerCase(), headerValues.get(i));
        }
        return headersBuilder.build();
    }
}

