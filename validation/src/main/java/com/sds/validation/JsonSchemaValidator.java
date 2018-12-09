package com.sds.validation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ConcurrentHashMap;

import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonSchemaValidator {
    
    ConcurrentHashMap<String, Schema> cache = new ConcurrentHashMap<>();

    private static final String PAYLOAD_ELEMENT = "message";
    private static final String PAYLOAD_TYPE_ELEMENT = "messageType";
    private static final Object SCHEMA_LOCATION = "/schemas";
    
    private final static Logger LOGGER = LoggerFactory.getLogger(JsonSchemaValidator.class);


    public void validateMassTransitMessage(byte[] data) {
        validateMassTransitMessage(new String(data));
    }
    
    public void validateMassTransitMessage(String data) {
        JSONObject json = new JSONObject(data);
        JSONObject message = (JSONObject) json.get(PAYLOAD_ELEMENT);
        JSONArray messageTypes = (JSONArray) json.get(PAYLOAD_TYPE_ELEMENT);
        String schemaName = messageTypes.getString(0).replace("urn:message:", "");
        LOGGER.debug("Validating json element {} against schema {}", PAYLOAD_ELEMENT, schemaName);
        validate(message, schemaName);
    }
    
    
    public void validate(byte[] data, String schemaName) {
        LOGGER.debug("Validating json object against schema {}", schemaName);
        validate(new JSONObject(new String(data)), schemaName);
    }
    
    public void validate(byte[] data, String element, String schemaName) {
        LOGGER.debug("Validating json element {} against schema {}", element, schemaName);
        validate((JSONObject)(new JSONObject(new String(data)).get(element)), schemaName);
    }

    public void validate(String data, String schemaName) {
        LOGGER.debug("Validating json object against schema {}", schemaName);
        validate(new JSONObject(data), schemaName);
    }
    
    public void validate(String data, String element, String schemaName) {
        LOGGER.debug("Validating json element {} against schema {}", element, schemaName);
        validate((JSONObject)(new JSONObject(data).get(element)), schemaName);
    }
    
    public void validate(JSONObject json, String schemaName) {
        Schema schema;
        if (cache.containsKey(schemaName)) {
            schema = cache.get(schemaName);
        } else {
            try (InputStream inputStream = getClass().getResourceAsStream(
                    String.format("%s/%s.json", SCHEMA_LOCATION, 
                            schemaName.replace(":", ".")))) {
                if (inputStream == null) {
                    throw new FileNotFoundException(
                            String.format("Schema not found for type %s", schemaName));
                }
                JSONObject rawSchema = new JSONObject(new JSONTokener(inputStream));
                schema = SchemaLoader.load(rawSchema);
                cache.put(schemaName, schema);
            } catch (IOException e) {
                LOGGER.warn("Error validating json object {}", e.getMessage());
                throw new JsonValidationException(e.getMessage());
            }
        }
        try {
            schema.validate(json);
        } catch (ValidationException e) {
            LOGGER.warn("Error validating json object {}", e.getAllMessages().toString());
            throw new JsonValidationException(e.getAllMessages().toString());
        } catch (Exception e) {
            LOGGER.warn("Error validating json object {}", e.getMessage());
            throw new JsonValidationException(e.getMessage());
        }
    }

}
