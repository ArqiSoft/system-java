/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.npspot.jtransitlight.consumer;

import com.npspot.jtransitlight.JTransitLightException;
import com.npspot.jtransitlight.MessageProcessingException;
import com.npspot.jtransitlight.consumer.callback.SnapshotDeltaCallback;
import com.npspot.jtransitlight.consumer.callback.message.MessageCallback;
import com.npspot.jtransitlight.consumer.setting.ConsumerSettings;
import com.npspot.jtransitlight.serializer.Serializer;
import com.npspot.jtransitlight.transport.Connection;
import com.npspot.jtransitlight.transport.JTransitLightTransportException;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * @author Rami
 */
public class ReceiverBusControlImpl implements ReceiverBusControl {

    /**
     * A connection linking to the transport used.
     */
    private final Connection connection;

    /**
     * A serializer used to transform object to the format used in the messages
     * in the transport layer.
     */
    private final Serializer serializer;

    ReceiverBusControlImpl(Connection connection) {
        this.connection = connection;
        this.serializer = new Serializer();
    }

    @Override
    public void useJsonSerializer() {
        serializer.useJsonSerializer();
    }

    @Override
    public void subscribe(List<String> exchangeName, String queueName, boolean isDurable, MessageCallback callback) throws JTransitLightException, IOException {
        connection.subscribe(exchangeName, queueName, new ConsumerSettings(isDurable), callback);
    }

    @Override
    public void subscribe(List<String> exchangeNames, String queueName, ConsumerSettings settings, MessageCallback callback) throws JTransitLightException, IOException {
        if (settings == null) {
            settings = new ConsumerSettings();
        }
        connection.subscribe(exchangeNames, queueName, settings, callback);
    }

    @Override
    public void subscribe(String exchangeName, String queueName, boolean isDurable, MessageCallback callback) throws JTransitLightException, IOException {
        connection.subscribe(Collections.singletonList(exchangeName), queueName, new ConsumerSettings(isDurable), callback);
    }

    @Override
    public void subscribe(String exchangeName, String queueName, ConsumerSettings settings, MessageCallback callback) throws JTransitLightException, IOException {
        if (settings == null) {
            settings = new ConsumerSettings();
        }
        connection.subscribe(Collections.singletonList(exchangeName), queueName, settings, callback);
    }

    @Override
    public void subscribeRefData(String restURL, long restReSyncInterval, SnapshotDeltaCallback messageCallback) throws JTransitLightException {
        if (restReSyncInterval <= 0) {
            throw new JTransitLightException("Rest re-sync interval must be greater than 0");
        }

        connection.subscribeRefData(restURL, restReSyncInterval, messageCallback);
    }

    @Override
    public void subscribeRefData(String exchangeName, String queueName, boolean isDurable, String restURL, boolean isHandleReSync, SnapshotDeltaCallback messageCallback) throws JTransitLightException, IOException, MessageProcessingException {
        connection.subscribeRefData(exchangeName, queueName, new ConsumerSettings(isDurable), restURL, isHandleReSync, messageCallback);
    }

    @Override
    public void subscribeRefData(String exchangeName, String queueName, ConsumerSettings settings, String restURL, boolean isHandleReSync, SnapshotDeltaCallback messageCallback) throws JTransitLightException, IOException, MessageProcessingException {
        if (settings == null) {
            settings = new ConsumerSettings();
        }
        connection.subscribeRefData(exchangeName, queueName, settings, restURL, isHandleReSync, messageCallback);
    }

    @Override
    public void subscribeTrxData(String restURL, long restReSyncInterval, long startTimeUTC, long endTimeUTC, SnapshotDeltaCallback messageCallback) throws JTransitLightException {
        if (restReSyncInterval <= 0) {
            throw new JTransitLightException("Rest re-sync interval must be greater than 0");
        }

        connection.subscribeTrxData(restURL, restReSyncInterval, startTimeUTC, endTimeUTC, messageCallback);
    }

    @Override
    public void subscribeTrxData(String exchangeName, String queueName, boolean isDurable, String restURL, long startTimeUTC, long endTimeUTC, boolean isHandleReSync, SnapshotDeltaCallback messageCallback) throws JTransitLightException, IOException, MessageProcessingException {
        connection.subscribeTrxData(exchangeName, queueName, new ConsumerSettings(isDurable), restURL, startTimeUTC, endTimeUTC, isHandleReSync, messageCallback);
    }

    @Override
    public void subscribeTrxData(String exchangeName, String queueName, ConsumerSettings settings, String restURL, long startTimeUTC, long endTimeUTC, boolean isHandleReSync, SnapshotDeltaCallback messageCallback) throws JTransitLightException, IOException, MessageProcessingException {
        if (settings == null) {
            settings = new ConsumerSettings();
        }
        connection.subscribeTrxData(exchangeName, queueName, settings, restURL, startTimeUTC, endTimeUTC, isHandleReSync, messageCallback);
    }

    @Override
    public void triggerRestReSync(String restURL, SnapshotDeltaCallback messageCallback) throws IOException, MessageProcessingException {
        connection.triggerRestReSync(restURL, messageCallback);
    }

    @Override
    public void unSubscribe(List<String> exchangeName, String queueName) throws IOException {
        connection.unsubscribe(exchangeName, queueName);
    }

    @Override
    public void unSubscribe(String exchangeName, String queueName) throws IOException {
        connection.unsubscribe(Collections.singletonList(exchangeName), queueName);
    }

    @Override
    public void unSubscribe(String restURL) {
        connection.unsubscribe(restURL);
    }

    @Override
    public void dispose() throws JTransitLightTransportException {
        connection.dispose();
    }

    @Override
    public boolean isOpen() {
        return connection.isOpen();
    }
}