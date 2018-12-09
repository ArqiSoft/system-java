package com.npspot.jtransitlight.serializer.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.npspot.jtransitlight.publisher.model.BusMessage;
import com.npspot.jtransitlight.JTransitLightException;
import com.npspot.jtransitlight.contract.Contract;
import com.npspot.jtransitlight.serializer.SerializerPlugin;

/**
 * Implementation of the JSON serializer using the Jackson JSON library.
 */
public class JsonSerializer implements SerializerPlugin {

    private ObjectMapper mapper;

    public JsonSerializer() {
        mapper = new ObjectMapper();
    }

    @Override
    public String serialize(BusMessage<? extends Contract> busMessage) throws JTransitLightException {
        try {
            String serializedMessage = mapper.writeValueAsString(busMessage);
            return serializedMessage;
        } catch (JsonProcessingException exception) {
            throw new JTransitLightException("Error serializing contract to JSON!", exception);
        }
    }
}
