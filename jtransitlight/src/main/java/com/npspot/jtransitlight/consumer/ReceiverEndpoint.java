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
     * @throws com.npspot.jtransitlight.JTransitLightException
     * @throws java.io.IOException
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
     * @throws com.npspot.jtransitlight.JTransitLightException
     * @throws java.io.IOException
     */
    void subscribe(String exchangeName, String queueName, ConsumerSettings settings, MessageCallback callback) throws JTransitLightException, IOException;

    /**
     * Subscribe to and start receiving messages for this contract
     *
     * @param exchangeNames List of exchange names
     * @param queueName     The queue to bind to this exchange
     * @param isDurable     Queue configuration parameter
     * @param callback      The callback interface to be called when a message is received
     * @throws com.npspot.jtransitlight.JTransitLightException
     * @throws java.io.IOException
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
     * @throws com.npspot.jtransitlight.JTransitLightException
     * @throws java.io.IOException
     */
    void subscribe(List<String> exchangeNames, String queueName, ConsumerSettings settings, MessageCallback callback) throws JTransitLightException, IOException;

    /**
     * Subscribe to and start receiving snapshots fetching them from REST endpoint with reSync interval
     *
     * @param restURL            Rest URL to fetch snapshot
     * @param restReSyncInterval ReSync interval
     * @param messageCallback    The callback interface to be called when a message is received
     */
    void subscribeRefData(String restURL, long restReSyncInterval, SnapshotDeltaCallback messageCallback) throws JTransitLightException;

    /**
     * Subscribe to and start receiving messages for this contract with initial snapshot fetched from REST endpoint
     *
     * @param exchangeName Exchange names
     * @param queueName    The queue to bind to this exchange
     * @param isDurable    Queue configuration parameter
     * @param callback     The callback interface to be called when a message is received
     * @throws com.npspot.jtransitlight.JTransitLightException
     * @throws java.io.IOException
     * @deprecated since {@link ConsumerSettings} appeared
     */
    @Deprecated
    void subscribeRefData(String exchangeName, String queueName, boolean isDurable, String restURL, boolean isHandleReSync, SnapshotDeltaCallback callback) throws JTransitLightException, IOException, MessageProcessingException;

    /**
     * Subscribe to and start receiving messages for this contract with initial snapshot fetched from REST endpoint
     *
     * @param exchangeName Exchange names
     * @param queueName    The queue to bind to this exchange
     * @param settings     Configured consumer settings
     * @param callback     The callback interface to be called when a message is received
     * @throws com.npspot.jtransitlight.JTransitLightException
     * @throws java.io.IOException
     */
    void subscribeRefData(String exchangeName, String queueName, ConsumerSettings settings, String restURL, boolean isHandleReSync, SnapshotDeltaCallback callback) throws JTransitLightException, IOException, MessageProcessingException;

    /**
     * Subscribe to and start receiving snapshots fetching them from REST endpoint with reSync interval
     *
     * @param restURL            Rest URL to fetch snapshot
     * @param restReSyncInterval ReSync interval
     * @param messageCallback    The callback interface to be called when a message is received
     */
    void subscribeTrxData(String restURL, long restReSyncInterval, long startTimeUTC, long endTimeUTC, SnapshotDeltaCallback messageCallback) throws JTransitLightException;

    /**
     * Subscribe to and start receiving messages for this contract with initial snapshot fetched from REST endpoint
     *
     * @param exchangeName Exchange names
     * @param queueName    The queue to bind to this exchange
     * @param isDurable    Queue configuration parameter
     * @param callback     The callback interface to be called when a message is received
     * @throws com.npspot.jtransitlight.JTransitLightException
     * @throws java.io.IOException
     * @deprecated since {@link ConsumerSettings} appeared
     */
    @Deprecated
    void subscribeTrxData(String exchangeName, String queueName, boolean isDurable, String restURL, long startTimeUTC, long endTimeUTC, boolean isHandleReSync, SnapshotDeltaCallback callback) throws JTransitLightException, IOException, MessageProcessingException;

    /**
     * Subscribe to and start receiving messages for this contract with initial snapshot fetched from REST endpoint
     *
     * @param exchangeName Exchange names
     * @param queueName    The queue to bind to this exchange
     * @param settings     Configured consumer settings
     * @param callback     The callback interface to be called when a message is received
     * @throws com.npspot.jtransitlight.JTransitLightException
     * @throws java.io.IOException
     */
    void subscribeTrxData(String exchangeName, String queueName, ConsumerSettings settings, String restURL, long startTimeUTC, long endTimeUTC, boolean isHandleReSync, SnapshotDeltaCallback callback) throws JTransitLightException, IOException, MessageProcessingException;

    /**
     * Trigger REST reSync
     *
     * @param restURL Rest URL
     */
    void triggerRestReSync(String restURL, SnapshotDeltaCallback messageCallback) throws IOException, MessageProcessingException;

    /**
     * Unsubscribe and stop receiving messages from this contract
     *
     * @param exchangeNames
     * @param queueName
     * @throws java.io.IOException
     */
    void unSubscribe(List<String> exchangeNames, String queueName) throws IOException;

    /**
     * Unsubscribe and stop receiving messages from this contract
     *
     * @param exchangeName
     * @param queueName
     * @throws java.io.IOException
     */
    void unSubscribe(String exchangeName, String queueName) throws IOException;

    /**
     * Unsubscribe and stop receiving messages from this rest URL
     *
     * @param restURL rest URL
     */
    void unSubscribe(String restURL);

    /**
     * Close RabbitMq connection.
     * Note that this renders this ReceiverEndpoint instance useless.
     *
     * @throws JTransitLightTransportException
     */
    void dispose() throws JTransitLightTransportException;
}