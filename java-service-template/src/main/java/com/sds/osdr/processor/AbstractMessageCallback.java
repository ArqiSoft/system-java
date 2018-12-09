package com.sds.osdr.processor;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.npspot.jtransitlight.MessageProcessingException;
import com.npspot.jtransitlight.consumer.callback.message.MessageCallback;
import com.npspot.jtransitlight.contract.Contract;
import com.npspot.jtransitlight.publisher.model.BusMessage;
import com.sds.osdr.model.AbstractContract;
import com.sds.validation.JsonSchemaValidator;
import com.sds.validation.JsonValidationException;

public abstract class AbstractMessageCallback<T extends Contract> implements MessageCallback {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageCallback.class);

    private Class<Contract> tClass;
    private final ObjectMapper objectMapper;
    private final JavaType deltaType;
    private BlockingQueue<T> queue;

    public AbstractMessageCallback(Class<T> tClass, 
            BlockingQueue<T> queue) {
        objectMapper = new ObjectMapper();
        objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        TypeFactory t = TypeFactory.defaultInstance();
        deltaType = t.constructParametricType(BusMessage.class, tClass);
        this.queue = queue;
    }

    @Override
    public void onMessage(byte[] bytes) throws MessageProcessingException {
        try {
            BusMessage<T> busMessage = objectMapper.readValue(bytes, deltaType);
            final T message = (T) busMessage.getMessage();
            LOGGER.debug("Deserialized Message: {}", message);
            while (!queue.offer(message)) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            
        } catch (IOException|JsonValidationException e) {
            LOGGER.error("Error parsing message to " + tClass.getName() + " " + e.getMessage() + ". Message text: \n"
                    + new String(bytes));
            throw new MessageProcessingException("Can't parse message to provided type " + tClass.getName(), e);
        }
    }

}