package com.zuunr.example;

import com.zuunr.json.JsonArray;
import com.zuunr.json.JsonObject;
import com.zuunr.json.JsonObjectBuilder;
import com.zuunr.json.JsonValue;
import com.zuunr.jsontester.GivenWhenThenTesterBase;
import com.zuunr.rest.Request;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EndToEndGivenWhenThenTest extends GivenWhenThenTesterBase {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    static Stream<Path> testFiles() throws Exception {
        return testFiles((Class <? extends GivenWhenThenTesterBase>) new Object(){}.getClass().getEnclosingClass()); // NOSONAR
    }


    //@Test
    public void shouldReturnExpectedData_whenEndpointIsCalled() {
        String url = "http://localhost:" + port + "/test";

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("content-type", "application/json");
        httpHeaders.add("accept", "application/json");
        httpHeaders.add("x-api-key", "apisecret");

        JsonValue body = JsonObject.EMPTY
                .put("hello", "you")
                .put("hidden_in_response", "I do not show!")
                .jsonValue();

        HttpEntity<JsonValue> httpEntity = new HttpEntity<>(body, httpHeaders);
        ResponseEntity<JsonValue> response = restTemplate.exchange(URI.create(url), HttpMethod.POST, httpEntity, ParameterizedTypeReference.forType(JsonValue.class));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEqualTo(JsonObject.EMPTY.put("hello", "you").jsonValue());
    }

    /*
     * This method implementation and annotations may be copied as-is to any other subclass of GivenWhenThenBaseTester
     */
    @DisplayName("Call API")
    @ParameterizedTest(name = "{index} => JSON file: {0}")
    @MethodSource("testFiles")
    void test(Path testsFolderPath) throws Exception {
        executeTest(testsFolderPath);
    }

    @Override
    public JsonValue doGivenWhen(JsonValue given, JsonValue when) {
        JsonObject jsonRequest = when.getJsonObject();
        Request<JsonValue> request = Request.of(jsonRequest);
        RequestEntity.BodyBuilder bodyBuilder = RequestEntity.method(HttpMethod.valueOf(request.getMethod()), "http://localhost:"+port+request.getURI());
        bodyBuilder.headers(getHeaders(jsonRequest));
        RequestEntity<JsonValue> requestEntity = bodyBuilder.body(request.getBody());

        JsonValue body = restTemplate.exchange(requestEntity, JsonValue.class).getBody();
        JsonObjectBuilder resultBuilder = JsonObject.EMPTY.builder();
        if (body != null){
            resultBuilder.put("body", body);
        }

        return resultBuilder.build().jsonValue();
    }

    private HttpHeaders getHeaders(JsonObject jsonRequest) {
        HttpHeaders httpHeaders = new HttpHeaders();
        JsonObject headers = jsonRequest.get("headers", JsonObject.EMPTY).getJsonObject();
        JsonArray headerNames = headers.keys();
        JsonArray headerValues = headers.values();

        for (int i = 0; i < headerNames.size(); i++) {
            String headerName = headerNames.get(i).getString();
            JsonArray values = headerValues.get(i).getJsonArray();
            httpHeaders.addAll(headerName,  values.stream().map(JsonValue::getString).toList());
        }
        return httpHeaders;
    }
}

