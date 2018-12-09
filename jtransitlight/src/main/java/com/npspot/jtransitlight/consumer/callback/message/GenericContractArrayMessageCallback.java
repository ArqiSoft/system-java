package com.npspot.jtransitlight.consumer.callback.message;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.npspot.jtransitlight.MessageProcessingException;
import com.npspot.jtransitlight.contract.GenericContractArray;
import com.npspot.jtransitlight.publisher.model.BusMessage;

import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GenericContractArrayMessageCallback<T> implements MessageCallback {

    private static final Logger LOGGER = LogManager.getLogger(GenericContractArrayMessageCallback.class);

    private Class<T> tClass;
    private GenericArrayMessageCallback<T> genericArrayMessageCallback;
    private final ObjectMapper objectMapper;
    private final JavaType deltaType;

    public GenericContractArrayMessageCallback(Class<T> tClass, GenericArrayMessageCallback<T> genericArrayMessageCallback) {
        this.genericArrayMessageCallback = genericArrayMessageCallback;
        this.tClass = tClass;
        objectMapper = new ObjectMapper();
        objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        TypeFactory t = TypeFactory.defaultInstance();
        JavaType genericContractType = t.constructParametrizedType(GenericContractArray.class, GenericContractArray.class, tClass);
        deltaType = t.constructParametrizedType(BusMessage.class, BusMessage.class, genericContractType);
    }

    @Override
    public void onMessage(byte[] bytes) throws MessageProcessingException {
        try {
            BusMessage<GenericContractArray<T>> busMessage = objectMapper.readValue(bytes, deltaType);
            genericArrayMessageCallback.onMessage(busMessage.getMessage());
        } catch (IOException e) {
            LOGGER.error("Error parsing message to " + tClass.getName() + " " + e.getMessage() + ". Message text: \n" + new String(bytes));
            throw new MessageProcessingException("Can't parse message to provided type " + tClass.getName(), e);
        }
    }

    @Override
    public boolean isAcknowledged() {
        return genericArrayMessageCallback.isAcknowledged();
    }
}
