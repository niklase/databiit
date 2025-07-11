package com.zuunr.diagrammaker;

import com.zuunr.json.JsonArray;
import com.zuunr.json.JsonObject;
import com.zuunr.json.schema.JsonSchema;

public class SchemaRegister {

    JsonObject register = JsonObject.EMPTY;

    public void addSchema(JsonSchema schema){
        JsonSchemaFlattener.flatten(JsonArray.EMPTY, schema.asJsonValue());
    }


}
