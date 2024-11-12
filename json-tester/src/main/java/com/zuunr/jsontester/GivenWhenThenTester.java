package com.zuunr.jsontester;

import com.zuunr.json.JsonObject;
import com.zuunr.json.JsonObjectFactory;
import com.zuunr.json.JsonObjectMerger;
import com.zuunr.json.JsonValue;


import static org.junit.jupiter.api.Assertions.assertEquals;


abstract public class GivenWhenThenTester {

    JsonObjectFactory jsonObjectFactory = new JsonObjectFactory();
    JsonObjectMerger jsonObjectMerger = new JsonObjectMerger();
    private JsonObject testCase;

    protected void executeTest() {
        String methodName = new Throwable().getStackTrace()[1].getMethodName();
        Class thisClass = this.getClass();
        JsonObject testCase = jsonObjectFactory.createJsonObject(thisClass.getResourceAsStream(methodName + ".json"));

        JsonValue result = doGivenWhen(testCase.get("given"), testCase.get("when"));
        JsonValue then = testCase.get("then");

        if (testCase.get("exactMatch", true).getBoolean()) {
            assertEquals(result, then, methodName);
        } else {
            JsonObject thenToBeMerged = JsonObject.EMPTY.put("mergeMe", then); // {"a":[{"b":1},{"c":2}]}
            JsonObject resultToBeMerged = JsonObject.EMPTY.put("mergeMe", result); // {"a":[{"c":2}]}
            JsonObject thenMergedByResult = jsonObjectMerger.merge(thenToBeMerged, resultToBeMerged);
            JsonObject resultMergedByThen = jsonObjectMerger.merge(resultToBeMerged, thenToBeMerged);

            if (!resultMergedByThen.get("mergeMe").equals(result)) {
                assertEquals(result, then);
            }
            assertEquals(thenMergedByResult.get("mergeMe"), resultMergedByThen.get("mergeMe"));
        }
    }

    /**
     * Should return the value of given when
     *
     * @param given
     * @param when
     * @return
     */
    public abstract JsonValue doGivenWhen(JsonValue given, JsonValue when);


}
