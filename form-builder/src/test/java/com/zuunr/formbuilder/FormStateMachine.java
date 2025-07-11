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

    public JsonObject read(JsonObject request) {
        String method = request.get("method").getString().toUpperCase();
        boolean isRead = "GET".equals(method);

        JsonObject config = configRegistry.getConfig(request);
        JsonObject item = database.readItem(getItemId(request));
        return JsonObject.EMPTY.put("status", 200).put("body", item);
    }

    public JsonObject write(JsonObject request) {

        String method = request.get("method").getString().toUpperCase();
        boolean isCreate = "POST".equals(method);
        boolean isUpdate = "PATCH".equals(method);
        boolean isDelete = "DELETE".equals(method);

        JsonObject config = configRegistry.getConfig(request);

        JsonObject exchange = JsonObject.EMPTY.put("request", request);
        JsonObject errorResponse = validateRequest(exchange, config);
        if (errorResponse != null) {
            return errorResponse;
        }

        JsonObject currentState = null;
        if (isUpdate || isDelete) {
            currentState = database.readItem(getItemId(request));
            if (currentState == null) {
                return JsonObject.EMPTY.put("status", 404);
            }
        }

        JsonObject newState = null;
        if (isCreate) {
            newState = request.get("body").getJsonObject();
        } else if (isUpdate) {
            JsonObjectMerger merger = new JsonObjectMerger(MergeStrategy.NULL_AS_DELETE_AND_ARRAY_AS_ATOM);
            newState = merger.merge(currentState, request.get("body").getJsonObject().remove("id"));
        }

        JsonValue transitionSchema = config.get("transitionSchema", JsonValue.FALSE);

        JsonObject exchangeWithState = exchange
                .put("currentState", currentState == null ? JsonValue.NULL : currentState.jsonValue())
                .put("newState", newState == null ? JsonValue.NULL : newState.jsonValue());

        JsonObject validationResult = validator.validate(exchangeWithState.jsonValue(), transitionSchema, OutputStructure.DETAILED);
        if (!JsonValue.TRUE.equals(validationResult.get("valid"))) {
            return JsonObject.EMPTY.put("status", 409).put("body", validationResult);
        }

        if (isCreate) {
            newState = database.createItem(newState);
            return JsonObject.EMPTY
                    .put("status", 201)
                    .put("headers", JsonObject.EMPTY.put("location", JsonArray.of(URI.create(request.get("uri").getString()).getPath() + "/" + newState.get("id").getString())))
                    .put("body", newState);
        } else if (isDelete){
            database.deleteItem(getItemId(request));
            return JsonObject.EMPTY
                    .put("status", 204);
        } else if (isUpdate) {
            database.writeItem(newState);
            return JsonObject.EMPTY
                    .put("status", 200)
                    .put("body", newState);
        }
        return JsonObject.EMPTY.put("status", 500);
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