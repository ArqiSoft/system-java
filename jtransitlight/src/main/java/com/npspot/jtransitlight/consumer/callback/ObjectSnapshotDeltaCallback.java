package com.npspot.jtransitlight.consumer.callback;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.npspot.jtransitlight.MessageProcessingException;
import com.npspot.jtransitlight.publisher.model.BusMessage;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ObjectSnapshotDeltaCallback<T> implements SnapshotDeltaCallback {

    private static final Logger LOGGER = LogManager.getLogger(ObjectSnapshotDeltaCallback.class);

    private Class<T> tClass;
    private GenericArraySnapshotDeltaCallback<T> genericArraySnapshotDeltaCallback;
    private final ObjectMapper objectMapper;
    private final JavaType snapshotType;
    private final JavaType deltaType;
    private final Boolean isDeltaArray; // Is delta array or just object?

    public ObjectSnapshotDeltaCallback(Class<T> tClass, GenericArraySnapshotDeltaCallback<T> genericArraySnapshotDeltaCallback) {
        this(tClass, genericArraySnapshotDeltaCallback, false);
    }


    public ObjectSnapshotDeltaCallback(Class<T> tClass, GenericArraySnapshotDeltaCallback<T> genericArraySnapshotDeltaCallback, boolean isDeltaArray) {
        this.genericArraySnapshotDeltaCallback = genericArraySnapshotDeltaCallback;
        this.tClass = tClass;
        this.isDeltaArray = isDeltaArray;
        objectMapper = new ObjectMapper();
        objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.registerModule(new JavaTimeModule());
        TypeFactory t = TypeFactory.defaultInstance();
        snapshotType = t.constructCollectionType(ArrayList.class, tClass);
        deltaType = (isDeltaArray) ? t.constructParametrizedType(BusMessage.class, BusMessage.class, snapshotType)
                : t.constructParametrizedType(BusMessage.class, BusMessage.class, tClass);
    }

    @Override
    public void onSnapshot(byte[] message) throws MessageProcessingException {
        try {
            List<T> items = objectMapper.readValue(message, snapshotType);
            genericArraySnapshotDeltaCallback.snapshotReceived(items);
        } catch (IOException e) {
            LOGGER.error("Error parsing snapshot to " + tClass.getName() + " " + e.getMessage() + ". Snapshot: \n" + new String(message));
            throw new MessageProcessingException("Can't parse message to provided type " + tClass.getName(), e);
        }
    }

    @Override
    public void onDelta(byte[] message) throws MessageProcessingException {
        try {
            if (isDeltaArray) {
                BusMessage<List<T>> busMessage = objectMapper.readValue(message, deltaType);
                genericArraySnapshotDeltaCallback.deltaReceived(busMessage.getMessage());
            } else {
                BusMessage<T> busMessage = objectMapper.readValue(message, deltaType);
                genericArraySnapshotDeltaCallback.deltaReceived(Collections.singletonList(busMessage.getMessage()));
            }
        } catch (IOException e) {
            LOGGER.error("Error parsing delta to " + tClass.getName() + " " + e.getMessage() + ". Delta: \n" + new String(message));
            throw new MessageProcessingException("Can't parse message to provided type " + tClass.getName(), e);
        }

    }
}
