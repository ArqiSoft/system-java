package com.npspot.jtransitlight.publisher;

import com.npspot.jtransitlight.JTransitLightException;
import com.npspot.jtransitlight.MessageContext;
import com.npspot.jtransitlight.contract.Contract;

import java.io.IOException;
import java.util.List;

public interface IPublishEndpoint {
    void publish(Contract contract);

    void publish(Contract contract, boolean createExchange) throws IOException, JTransitLightException;

    void publishHeartbeat(Contract contract) throws JTransitLightException;

    void publish(Contract contract, MessageContext<Contract> context) throws JTransitLightException;

    void publishHeartbeat(Contract contract, MessageContext<Contract> context) throws JTransitLightException;

    void setMessageEncoding(String encodingName);

    List<byte[]> fetchInternalMemoryMessages(String contractId);

    void cleanInternalMemoryMessages();
}
