package com.zuunr.formbuilder;

import com.zuunr.json.JsonObject;
import com.zuunr.json.schema.generation.SchemaGenerator;

public class FormSchemaGenerator {

    public JsonObject generateFormSchema(JsonObject requestForm, JsonObject currentState) {
        SchemaGenerator schemaGenerator = new SchemaGenerator();

        schemaGenerator.generateSchema(currentState);


    }
}
