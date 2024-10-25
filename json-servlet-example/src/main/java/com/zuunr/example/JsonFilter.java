package com.zuunr.example;

import com.zuunr.json.JsonArray;
import com.zuunr.json.JsonObject;
import com.zuunr.json.JsonValue;
import com.zuunr.json.JsonValueFactory;
import com.zuunr.json.schema.JsonSchema;
import com.zuunr.json.schema.validation.JsonSchemaValidator;
import com.zuunr.rest.CachedBodyRequestWrapper;
import com.zuunr.rest.CachedBodyResponseWrapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class JsonFilter extends OncePerRequestFilter {

    private PermissionSchemaProvider permissionSchemaProvider;
    private RequestAccessController requestAccessController;

    public JsonFilter(
            @Autowired PermissionSchemaProvider permissionSchemaProvider,
            @Autowired RequestAccessController requestAccessController
    ) {
        this.permissionSchemaProvider = permissionSchemaProvider;
        this.requestAccessController = requestAccessController;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest servletRequest, HttpServletResponse servletResponse, FilterChain filterChain) throws ServletException, IOException {

        CachedBodyRequestWrapper requestWrapper = new CachedBodyRequestWrapper(servletRequest);
        CachedBodyResponseWrapper responseWrapper = new CachedBodyResponseWrapper(servletResponse);

        try {
            JsonObject authorizedExchangeModel = getAuthorizedExchangeModel(requestWrapper);
            filterChain.doFilter(
                    requestWrapper,
                    responseWrapper
            );

            JsonValue responseJsonBody = JsonValueFactory.create(new ByteArrayInputStream(responseWrapper.getDataStream()));
            if (responseJsonBody == null) {
                return;
            }

            JsonSchema responseSchema = permissionSchemaProvider.getResponsePermissionSchema(requestWrapper.getRequestURI(), requestWrapper.getMethod().toUpperCase(), "PERMISSION1").as(JsonSchema.class);
            JsonObject filteredResponse = filterResponse(authorizedExchangeModel.put(JsonArray.of("response", "body"), responseJsonBody), responseWrapper, responseSchema);
            if (filteredResponse == null) {
                return;
            }
            writeResponseBody(filteredResponse.get("body").asJson(), servletResponse);

        } catch (AuthenticationException authenticationException) {
            responseWrapper.setStatus(401);
        } catch (AuthorizationException authorizationException) {
            responseWrapper.setStatus(403);
            writeResponseBody(authorizationException.validation.asJson(), servletResponse);
        }
    }

    private void writeResponseBody(String responseBody, ServletResponse servletResponse) {
        final ServletOutputStream servletOutputStream;

        try {
            servletOutputStream = servletResponse.getOutputStream();
        } catch (Exception e) {
            throw new RuntimeException("servletResponse.getOutputStream() failed", e);
        }

        try {
            servletOutputStream.write(responseBody.getBytes());
        } catch (Exception e) {
            new RuntimeException("servletOutputStream.write(response.getBytes()) failed", e);
        }
    }

    private JsonObject getAuthorizedExchangeModel(CachedBodyRequestWrapper requestWrapper) throws AuthenticationException, AuthorizationException {
        return requestAccessController.getAuthorizedExchange(requestWrapper);
    }

    private JsonObject filterResponse(JsonObject exchange, CachedBodyResponseWrapper responseWrapper, JsonSchema responseSchema) {
        JsonValue responseBody = JsonValueFactory.create(new String(responseWrapper.getDataStream()));
        JsonValue updatedExchange = new JsonSchemaValidator().filter(exchange.put(JsonArray.of("response", "body"), responseBody).jsonValue(), responseSchema);
        return updatedExchange.get("response").getJsonObject();
    }
}