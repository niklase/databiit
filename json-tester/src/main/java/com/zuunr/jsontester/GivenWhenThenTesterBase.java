package com.zuunr.jsontester;

import com.zuunr.json.JsonObject;
import com.zuunr.json.JsonObjectMerger;
import com.zuunr.json.JsonValue;
import com.zuunr.json.JsonValueFactory;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class GivenWhenThenTesterBase {

    private static final JsonObjectMerger JSON_OBJECT_MERGER = new JsonObjectMerger();


    static protected Stream<Path> testFiles(Class<? extends GivenWhenThenTesterBase> testClass) throws Exception {

        URI uri = testClass.getResource(testClass.getSimpleName()).toURI();
        return Files.list(Paths.get(uri))
                .filter(path -> path.toString().endsWith(".json"));
    }

    protected final void executeTest(Path jsonFilePath) throws Exception {
        // Read JSON content (or parse, validate, etc.)
        String jsonContent = Files.readString(jsonFilePath);

        // Call the method you want to test, passing the JSON content
        // For example:

        JsonValue testCase = JsonValueFactory.create(jsonContent);

        JsonValue result = doGivenWhen(testCase.get("given"), testCase.get("when"));
        JsonValue then = testCase.get("then");

        if (testCase.get("exactMatch", true).getBoolean()) {
            assertEquals(result, then);
        } else {
            JsonObject thenToBeMerged = JsonObject.EMPTY.put("mergeMe", then); // {"a":[{"b":1},{"c":2}]}
            JsonObject resultToBeMerged = JsonObject.EMPTY.put("mergeMe", result); // {"a":[{"c":2}]}
            JsonObject thenMergedByResult = JSON_OBJECT_MERGER.merge(thenToBeMerged, resultToBeMerged);
            JsonObject resultMergedByThen = JSON_OBJECT_MERGER.merge(resultToBeMerged, thenToBeMerged);

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
