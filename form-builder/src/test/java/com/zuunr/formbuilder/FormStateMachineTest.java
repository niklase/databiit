package com.zuunr.formbuilder;

import com.zuunr.json.JsonObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


class FormStateMachineTest {

    @Test
    void test() {
        FormStateMachine formStateMachine = new FormStateMachine();
        JsonObject requestBody = JsonObject.EMPTY.put("status", "TODO");
        JsonObject response = formStateMachine.create(JsonObject.EMPTY.put("request", JsonObject.EMPTY.put("uri", "/tasks").put("body", requestBody)));

        JsonObject responseBody = response.get("body").getJsonObject();
        assertEquals(responseBody.remove("id"), requestBody);
        assertNotNull(responseBody.get("id"));

        JsonObject responseOfNonExisting = formStateMachine.update(JsonObject.EMPTY.put("uri", "/tasks/123").put("body", JsonObject.EMPTY.put("status", "TODO")));
        assertEquals(404, responseOfNonExisting.get("status").getJsonNumber().intValue());

        String location = response.get("headers").get("location").get(0).getString();
        JsonObject responseOfUpdate = formStateMachine.update(JsonObject.EMPTY.put("uri", location).put("body", JsonObject.EMPTY.put("status", "TODO")));
        assertEquals(409, responseOfUpdate.get("status").getJsonNumber().intValue());

        JsonObject responseOfValidUpdate = formStateMachine.update(JsonObject.EMPTY.put("uri", location).put("body", JsonObject.EMPTY.put("status", "DOING")));
        assertEquals(200, responseOfValidUpdate.get("status").getJsonNumber().intValue());


    }
}
