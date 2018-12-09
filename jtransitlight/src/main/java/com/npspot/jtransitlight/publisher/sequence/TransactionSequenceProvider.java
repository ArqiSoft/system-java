package com.npspot.jtransitlight.publisher.sequence;

import com.npspot.jtransitlight.contract.Contract;
import com.npspot.jtransitlight.transport.JTransitLightTransportException;

public class TransactionSequenceProvider implements SequenceProvider {

    @Override
    public void processSequence(String exchange, Contract contract) throws JTransitLightTransportException {
        if (contract.getMessageSequence() == null) {
            throw new JTransitLightTransportException("Sequence message for transaction cotract must be present");
        }
    }
}
