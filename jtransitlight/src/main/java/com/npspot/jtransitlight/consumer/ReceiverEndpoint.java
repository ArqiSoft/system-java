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
import com.npspot.jtransitlight.transport.JTransitLightTransportException;

import java.io.IOException;
import java.util.List;

/**
 * @author Rami
 */
public interface ReceiverEndpoint {

    /**
     * Subscribe to and start receiving messages for this contract
     *
     * @param exchangeName List of exchange names
     * @param queueName    The queue to bind to this exchange
     * @param isDurable    Queue configuration parameter
     * @param callback     The callback interface to be called when a message is received
     * @throws com.npspot.jtransitlight.JTransitLightException if cought inside
     * @throws java.io.IOException if cought inside
     * @deprecated since {@link ConsumerSettings} appeared
     */
    @Deprecated
    void subscribe(String exchangeName, String queueName, boolean isDurable, MessageCallback callback) throws JTransitLightException, IOException;

    /**
     * Subscribe to and start receiving messages for this contract
     *
     * @param exchangeName List of exchange names
     * @param queueName    The queue to bind to this exchange
     * @param settings     Configured consumer settings
     * @param callback     The callback interface to be called when a message is received
     * @throws com.npspot.jtransitlight.JTransitLightException if cought inside
     * @throws java.io.IOException if cought inside
     */
    void subscribe(String exchangeName, String queueName, ConsumerSettings settings, MessageCallback callback) throws JTransitLightException, IOException;

    /**
     * Subscribe to and start receiving messages for this contract
     *
     * @param exchangeNames List of exchange names
     * @param queueName     The queue to bind to this exchange
     * @param isDurable     Queue configuration parameter
     * @param callback      The callback interface to be called when a message is received
     * @throws com.npspot.jtransitlight.JTransitLightException if cought inside
     * @throws java.io.IOException if cought inside
     * @deprecated since {@link ConsumerSettings} appeared
     */
    @Deprecated
    void subscribe(List<String> exchangeNames, String queueName, boolean isDurable, MessageCallback callback) throws JTransitLightException, IOException;

    /**
     * Subscribe to and start receiving messages for this contract
     *
     * @param exchangeNames List of exchange names
     * @param queueName     The queue to bind to this exchange
     * @param settings      Configured consumer settings
     * @param callback      The callback interface to be called when a message is received
     * @throws com.npspot.jtransitlight.JTransitLightException if cought inside
     * @throws java.io.IOException if cought inside
     */
    void subscribe(List<String> exchangeNames, String queueName, ConsumerSettings settings, MessageCallback callback) throws JTransitLightException, IOException;

    /**
     * Subscribe to and start receiving snapshots fetching them from REST endpoint with reSync interval
     *
     * @param restURL            Rest URL to fetch snapshot
     * @param restReSyncInterval ReSync interval
     * @param messageCallback    The callback interface to be called when a message is received
     * @throws com.npspot.jtransitlight.JTransitLightException if cought inside
     */
    void subscribeRefData(String restURL, long restReSyncInterval, SnapshotDeltaCallback messageCallback) throws JTransitLightException;

    @Deprecated
    void subscribeRefData(String exchangeName, String queueName, boolean isDurable, String restURL, boolean isHandleReSync, SnapshotDeltaCallback callback) throws JTransitLightException, IOException, MessageProcessingException;

    /**
     * Subscribe to and start receiving messages for this contract with initial snapshot fetched from REST endpoint
     *
     * @param exchangeName   Exchange names
     * @param queueName      The queue to bind to this exchange
     * @param settings       Configured consumer settings
     * @param restURL        URL
     * @param isHandleReSync Handles re-sync
     * @param callback       The callback interface to be called when a message is received
     * @throws com.npspot.jtransitlight.JTransitLightException if cought inside
     * @throws java.io.IOException if cought inside
     * @throws MessageProcessingException if cought inside
     */
    void subscribeRefData(String exchangeName, String queueName, ConsumerSettings settings, String restURL, boolean isHandleReSync, SnapshotDeltaCallback callback) throws JTransitLightException, IOException, MessageProcessingException;

    void subscribeTrxData(String restURL, long restReSyncInterval, long startTimeUTC, long endTimeUTC, SnapshotDeltaCallback messageCallback) throws JTransitLightException;

    @Deprecated
    void subscribeTrxData(String exchangeName, String queueName, boolean isDurable, String restURL, long startTimeUTC, long endTimeUTC, boolean isHandleReSync, SnapshotDeltaCallback callback) throws JTransitLightException, IOException, MessageProcessingException;

    void subscribeTrxData(String exchangeName, String queueName, ConsumerSettings settings, String restURL, long startTimeUTC, long endTimeUTC, boolean isHandleReSync, SnapshotDeltaCallback callback) throws JTransitLightException, IOException, MessageProcessingException;

    void triggerRestReSync(String restURL, SnapshotDeltaCallback messageCallback) throws IOException, MessageProcessingException;

    void unSubscribe(List<String> exchangeNames, String queueName) throws IOException;

    void unSubscribe(String exchangeName, String queueName) throws IOException;

    void unSubscribe(String restURL);

    void dispose() throws JTransitLightTransportException;
}