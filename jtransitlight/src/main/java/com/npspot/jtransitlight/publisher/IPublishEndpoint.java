package com.npspot.jtransitlight.publisher;

import com.npspot.jtransitlight.JTransitLightException;
import com.npspot.jtransitlight.MessageContext;
import com.npspot.jtransitlight.contract.Contract;

import java.io.IOException;
import java.util.List;

public interface IPublishEndpoint {
    /**
     * Publishes a message to all subscribed consumers for the message type as specified by the generic parameter.
     *
     * @param contract The message to be published.
     * @throws JTransitLightException
     */
    void publish(Contract contract);

    /**
     * Publishes a message to all subscribed consumers for the message type as specified by the generic parameter.
     *
     * @param contract       The message to be published.
     * @param createExchange Auto creates exchange if it does not exists.
     * @throws JTransitLightException
     */
    void publish(Contract contract, boolean createExchange) throws IOException, JTransitLightException;


    /**
     * Publishes a heartbeat message to all subscribed consumers.
     * The interval of publishing is handled elsewhere
     *
     * @param contract The heartbeat to send
     * @throws JTransitLightException
     * @see HeartbeatSender
     */
    void publishHeartbeat(Contract contract) throws JTransitLightException;

    /**
     * Publishes a message to all subscribed consumers for the message type as specified by the generic parameter.
     *
     * @param contract The message to be published.
     * @param context
     * @throws JTransitLightException
     */
    void publish(Contract contract, MessageContext<Contract> context) throws JTransitLightException;

    /**
     * Publishes a heartbeat message to all subscribed consumers.
     * The interval of publishing is handled elsewhere
     *
     * @param contract The heartbeat to send
     * @param context
     * @throws JTransitLightException
     * @see HeartbeatSender
     */
    void publishHeartbeat(Contract contract, MessageContext<Contract> context) throws JTransitLightException;

    /**
     * Set the encoding used when transforming messages from serialized format to a transport byte array.
     *
     * @param encodingName Encoding name according to Java naming.
     */
    void setMessageEncoding(String encodingName);

    List<byte[]> fetchInternalMemoryMessages(String contractId);

    void cleanInternalMemoryMessages();
}
