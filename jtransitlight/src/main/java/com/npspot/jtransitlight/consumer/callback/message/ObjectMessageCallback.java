package com.npspot.jtransitlight.consumer.callback.message;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.npspot.jtransitlight.MessageProcessingException;
import com.npspot.jtransitlight.consumer.callback.GenericArraySnapshotDeltaCallback;
import com.npspot.jtransitlight.publisher.model.BusMessage;

import java.io.IOException;
import java.util.Collections;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ObjectMessageCallback<T> implements MessageCallback {
    private static final Logger LOGGER = LogManager.getLogger(ObjectMessageCallback.class);

    private Class<T> tClass;
    private GenericArraySnapshotDeltaCallback<T> genericArraySnapshotDeltaCallback;
    private final ObjectMapper objectMapper;
    private final JavaType deltaType;

    public ObjectMessageCallback(Class<T> tClass, GenericArraySnapshotDeltaCallback<T> genericArraySnapshotDeltaCallback) {
        this.genericArraySnapshotDeltaCallback = genericArraySnapshotDeltaCallback;
        this.tClass = tClass;
        objectMapper = new ObjectMapper();
        objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        TypeFactory t = TypeFactory.defaultInstance();
        deltaType = t.constructParametrizedType(BusMessage.class, BusMessage.class, tClass);
    }

    @Override
    public void onMessage(byte[] bytes) throws MessageProcessingException {
        try {
            BusMessage<T> busMessage = objectMapper.readValue(bytes, deltaType);
            genericArraySnapshotDeltaCallback.deltaReceived(Collections.singletonList(busMessage.getMessage()));
        } catch (IOException e) {
            LOGGER.error("Error parsing message to " + tClass.getName() + " " + e.getMessage() + ". Message text: \n" + new String(bytes));
            throw new MessageProcessingException("Can't parse message to provided type " + tClass.getName(), e);
        }

    }
}
