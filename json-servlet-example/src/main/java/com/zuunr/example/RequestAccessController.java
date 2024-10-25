package com.zuunr.example;

import com.zuunr.api.openapi.OAS3Deserializer;
import com.zuunr.http.RequestUtil;
import com.zuunr.json.JsonObject;
import com.zuunr.json.JsonValue;
import com.zuunr.json.schema.JsonSchema;
import com.zuunr.json.schema.validation.JsonSchemaValidator;
import com.zuunr.json.schema.validation.OutputStructure;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;


public class RequestAccessController {

    private static final JsonSchemaValidator JSON_SCHEMA_VALIDATOR = new JsonSchemaValidator();
    private PermissionSchemaProvider permissionSchemaProvider;

    public RequestAccessController(PermissionSchemaProvider permissionSchemaProvider) {
        this.permissionSchemaProvider = permissionSchemaProvider;
    }

    private static final JsonObject DEFAULT_OAS_DESER_OP = JsonObject.EMPTY
            .put("requestBody", JsonObject.EMPTY.put("content", JsonObject.EMPTY.put("application/json", JsonObject.EMPTY.put("schema", JsonObject.EMPTY))));

    public JsonObject getAuthorizedExchange(HttpServletRequest httpServletRequest) throws AuthorizationException {

        JsonObject request = RequestUtil.createRequest(httpServletRequest);
        JsonValue body = request.get("body");

        JsonObject exchange = JsonObject.EMPTY.put("request", request.put("body", body.asJson())); // TODO: MAke sure deserilalization is done from servlet reqeust directly

        JsonObject exchangeModel = OAS3Deserializer.deserializeRequest(exchange, DEFAULT_OAS_DESER_OP);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        JsonObject validationResult = null;
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            JsonValue requestSchema = permissionSchemaProvider.getRequestPermissionSchema(
                    request.get("uri").getString(),
                    request.get("method").getString(),
                    authority.getAuthority() //authentication.getAuthorities();
            );
            validationResult = JSON_SCHEMA_VALIDATOR.validate(exchangeModel.jsonValue(), requestSchema.as(JsonSchema.class), OutputStructure.DETAILED);
            if (validationResult.get("valid").getBoolean()){
                return exchangeModel;
            }
        }

        if (validationResult == null) {
            validationResult = JSON_SCHEMA_VALIDATOR.validate(exchangeModel.jsonValue(), JsonObject.EMPTY.put("properties", JsonObject.EMPTY.put("request", false)).as(JsonSchema.class), OutputStructure.DETAILED);
        }
        throw new AuthorizationException(validationResult);
    }
}
