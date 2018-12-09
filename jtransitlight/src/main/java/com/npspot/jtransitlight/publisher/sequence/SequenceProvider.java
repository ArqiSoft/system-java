package com.npspot.jtransitlight.publisher.sequence;

import com.npspot.jtransitlight.contract.Contract;
import com.npspot.jtransitlight.transport.JTransitLightTransportException;

public interface SequenceProvider {

    void processSequence(String exchange, Contract contract) throws JTransitLightTransportException;

}
