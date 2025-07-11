package com.zuunr.diagrammaker;

import com.zuunr.json.JsonArray;
import com.zuunr.json.JsonObject;
import com.zuunr.json.JsonValue;
import org.junit.jupiter.api.Test;

public class SimpleTest {

    @Test
    void test() {
        String json = "{\"aaa\": [\"bbb\", \"ccc\"]}";
        // JsonValue jsonValue = JsonValue.of("hej");
        //JsonValue jsonValue = JsonValueFactory.create(json);
        JsonValue jsonValue = JsonObject.EMPTY.put("aaa", JsonArray.of("bbb", "ccc")).jsonValue();


        System.out.println(jsonValue);
    }
}
