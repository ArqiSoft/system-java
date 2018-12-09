package com.npspot.jtransitlight.consumer.callback;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.npspot.jtransitlight.MessageProcessingException;
import com.npspot.jtransitlight.contract.GenericContractArray;
import com.npspot.jtransitlight.publisher.model.BusMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GenericContractArraySnapshotDeltaCallback<T> implements SnapshotDeltaCallback {
    private static final Logger LOGGER = LogManager.getLogger(GenericContractArraySnapshotDeltaCallback.class);

    private Class<T> tClass;
    private GenericArraySnapshotDeltaCallback<T> genericArraySnapshotDeltaCallback;
    private final ObjectMapper objectMapper;
    private final JavaType snapshotType;
    private final JavaType deltaType;

    public GenericContractArraySnapshotDeltaCallback(Class<T> tClass, GenericArraySnapshotDeltaCallback<T> genericArraySnapshotDeltaCallback) {
        this.genericArraySnapshotDeltaCallback = genericArraySnapshotDeltaCallback;
        this.tClass = tClass;
        objectMapper = new ObjectMapper();
        objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        TypeFactory t = TypeFactory.defaultInstance();
        snapshotType = t.constructCollectionType(ArrayList.class, tClass);
        JavaType genericContractType = t.constructParametrizedType(GenericContractArray.class, GenericContractArray.class, tClass);
        deltaType = t.constructParametrizedType(BusMessage.class, BusMessage.class, genericContractType);
    }

    @Override
    public void onSnapshot(byte[] message) throws MessageProcessingException {
        try {
            List<T> items = objectMapper.readValue(message, snapshotType);
            genericArraySnapshotDeltaCallback.snapshotReceived(items);
        } catch (IOException e) {
            LOGGER.error("Error parsing snapshot by generic contract callback to " + tClass.getName() + " " + e.getMessage() + ". Snapshot: \n" + new String(message));
            throw new MessageProcessingException("Can't parse message to provided type " + tClass.getName(), e);
        }
    }

    @Override
    public void onDelta(byte[] message) throws MessageProcessingException {
        try {
            BusMessage<GenericContractArray<T>> busMessage = objectMapper.readValue(message, deltaType);
            genericArraySnapshotDeltaCallback.deltaReceived(busMessage.getMessage());
        } catch (IOException e) {
            LOGGER.error("Error parsing delta to " + tClass.getName() + " " + e.getMessage() + ". Delta: \n" + new String(message));
            throw new MessageProcessingException("Can't parse message to provided type " + tClass.getName(), e);
        }

    }
}
