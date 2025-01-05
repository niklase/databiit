package com.zuunr.api.objectgen;

import com.zuunr.json.JsonObject;
import com.zuunr.json.PojoFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class PojoFactoryTest {

    @Test
    void test() {
        PersonTestPojo testPojo = new PojoFactory().createPojo(JsonObject.EMPTY.put("name", "Peter"), PersonTestPojo.class);
        assertEquals("Peter", testPojo.getName());
        assertNull(testPojo.getAge());
    }
}
