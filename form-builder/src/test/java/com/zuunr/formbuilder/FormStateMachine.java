package com.zuunr.formbuilder;

import com.zuunr.json.JsonArray;
import com.zuunr.json.JsonObject;
import com.zuunr.json.JsonObjectMerger;
import com.zuunr.json.JsonValue;
import com.zuunr.json.merge.MergeStrategy;
import com.zuunr.json.schema.JsonSchema;
import com.zuunr.json.schema.validation.JsonSchemaValidator;
import com.zuunr.json.schema.validation.OutputStructure;
import com.zuunr.json.util.ApiErrorCreator;
import com.zuunr.json.util.StringSplitter;

import java.net.URI;

public class FormStateMachine {

    JsonSchemaValidator validator = new JsonSchemaValidator();

    private final ConfigRegistry configRegistry = new ConfigRegistry();
    private final Database database = new Database();

    public JsonObject create(JsonObject exchange) {

        JsonObject request = exchange.get("request").getJsonObject();
        JsonObject config = configRegistry.getConfig(request);

        JsonObject errorResponse = validateRequest(request, config);
        if (errorResponse != null) {
            return JsonObject.EMPTY.put("response", errorResponse);
        }

        JsonObject createdItem = database.createItem(request.get("body").getJsonObject());
        return JsonObject.EMPTY
                .put("status", 200)
                .put("headers", JsonObject.EMPTY
                        .put("location", JsonArray.of(
                                URI.create(request.get("uri").getString()).getPath() + "/" + createdItem.get("id").getString())))
                .put("body", createdItem);
    }

    public JsonObject update(JsonObject request) {

        JsonObject config = configRegistry.getConfig(request);

        JsonObject exchange = JsonObject.EMPTY.put("request", request);
        JsonObject errorResponse = validateRequest(exchange, config);
        if (errorResponse != null) {
            return errorResponse;
        }

        JsonObject currentState = database.readItem(getItemId(request));

        if (currentState == null) {
            return JsonObject.EMPTY.put("status", 404);
        }

        JsonObjectMerger merger = new JsonObjectMerger(MergeStrategy.NULL_AS_DELETE_AND_ARRAY_AS_ATOM);
        JsonObject newState = merger.merge(currentState, request.get("body").getJsonObject().remove("id"));

        JsonValue transitionSchema = config.get("transitionSchema", JsonValue.FALSE);

        JsonObject exchangeWithState = exchange
                .put("currentState", currentState)
                .put("newState", newState);
        JsonObject validationResult = validator.validate(exchangeWithState.jsonValue(), transitionSchema, OutputStructure.DETAILED);
        if (!JsonValue.TRUE.equals(validationResult.get("valid"))) {
            return JsonObject.EMPTY.put("status", 409).put("body", validationResult);
        }
        database.writeItem(newState);
        return JsonObject.EMPTY.put("status", 200).put("body", newState);
    }

    private String getItemId(JsonObject request) {
        return StringSplitter.splitString(URI.create(request.get("uri").getString()).getPath(), '/').last().getString();
    }

    private JsonObject validateRequest(JsonObject request, JsonObject machineConfig) {

        // Validate request
        JsonValue requestSchema = machineConfig.get("requestSchema");

        JsonObject validationResult = validator.validate(request.jsonValue(), requestSchema, OutputStructure.DETAILED);
        if (!JsonValue.TRUE.equals(validationResult.get("valid"))) {
            return JsonObject.EMPTY.put("status", 400).put("body", ApiErrorCreator.ERROR_ARRAY_WITH_VIOLATIONS_ARRAY.createErrors(validationResult, request.jsonValue(), requestSchema.as(JsonSchema.class)));
        }
        return null;
    }
}