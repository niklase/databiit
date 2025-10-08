package com.zuunr.drawioserver;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.zuunr.json.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.FreeMarkerConfigurerBeanDefinitionParser;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

@Configuration
public class Config {

    @Bean
    public FreeMarkerConfigurer freemarkerConfigurer() {
        FreeMarkerConfigurer configurer = new FreeMarkerConfigurer();

        // Set the location of your templates
        configurer.setTemplateLoaderPath("classpath:/templates/");
        configurer.setDefaultEncoding("UTF-8");

        return configurer;
    }


    @Bean
    public FreeMarkerViewResolver freemarkerViewResolver() {
        FreeMarkerViewResolver resolver = new FreeMarkerViewResolver();

        // View resolver properties
        resolver.setSuffix(".ftl");
        resolver.setContentType("text/html; charset=UTF-8");
        resolver.setOrder(0); // Ensure highest precedence
        resolver.setCache(false); // Useful during development

        return resolver;
    }

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri; // Automatically loads the URI from application.yml

    @Bean
    public MongoClient mongoClient() {
        // Create and return the MongoClient instance using the URI
        return MongoClients.create(mongoUri);
    }

    @Bean
    ObjectMapper objectMapper() {

        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();

        module.addDeserializer(JsonObject.class, new JsonObjectDeserializer());
        module.addDeserializer(JsonValue.class, new JsonValueDeserializer());
        module.addSerializer(JsonObject.class, new JsonObjectSerializer());
        module.addSerializer(JsonArray.class, new JsonArraySerializer());
        module.addSerializer(JsonValue.class, new JsonValueSerializer());

        mapper.registerModule(module);
        mapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
        mapper.enable(DeserializationFeature.USE_LONG_FOR_INTS);
        mapper.setNodeFactory(JsonNodeFactory.withExactBigDecimals(true));
        return mapper;
    }
}