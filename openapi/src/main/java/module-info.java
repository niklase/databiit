module com.zuunr.api.openapi {
    requires com.zuunr.json;
    requires spring.web;
    exports com.zuunr.api.http;
    exports com.zuunr.api.openapi;
    opens com.zuunr.api.openapi to com.zuunr.json, org.junit.platform.commons, org.junit.jupiter.api;
}