package com.zuunr.jsontester;

import com.zuunr.json.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class GivenWhenThenTesterBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(GivenWhenThenTesterBase.class);

    private static final JsonObjectMerger JSON_OBJECT_MERGER = new JsonObjectMerger();
    private static URI TEST_FOLDER_URI;

    static protected URI getTestFolder() {
        return TEST_FOLDER_URI;
    }

    static protected Stream<Path> testFiles(Class<? extends GivenWhenThenTesterBase> testClass) throws Exception {

        TEST_FOLDER_URI = testClass.getResource(testClass.getSimpleName()).toURI();
        return Files.list(Paths.get(TEST_FOLDER_URI))
                .filter(path -> path.toString().endsWith(".json")).map(Path::getFileName);
    }

    protected final void executeTest(Path jsonFileName) throws Exception {
        // Read JSON content (or parse, validate, etc.)
        Path testFilePath = Path.of(TEST_FOLDER_URI.getPath()+"/"+jsonFileName.toString());
        LOGGER.info("file:" + testFilePath);
        String jsonContent = Files.readString(testFilePath);

        // Call the method you want to test, passing the JSON content
        // For example:

        JsonValue testCase = JsonValueFactory.create(jsonContent);

        JsonValue result = doGivenWhen(testCase.get("given"), testCase.get("when"));
        JsonValue then = testCase.get("then");

        if (testCase.get("meta", JsonObject.EMPTY).get("additionalPropertiesAllowed", false).getBoolean()) {
            JsonObject thenToBeMerged = JsonObject.EMPTY.put("mergeMe", then); // {"a":[{"b":1},{"c":2}]}
            JsonObject resultToBeMerged = JsonObject.EMPTY.put("mergeMe", result); // {"a":[{"c":2}]}
            JsonObject thenMergedByResult = JSON_OBJECT_MERGER.merge(thenToBeMerged, resultToBeMerged);
            JsonObject resultMergedByThen = JSON_OBJECT_MERGER.merge(resultToBeMerged, thenToBeMerged);

            if (!resultMergedByThen.get("mergeMe").equals(result)) {
                assertEquals(then, result);
            }
            assertEquals(thenMergedByResult.get("mergeMe"), resultMergedByThen.get("mergeMe"));
        } else {
            assertEquals(then, result);
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
