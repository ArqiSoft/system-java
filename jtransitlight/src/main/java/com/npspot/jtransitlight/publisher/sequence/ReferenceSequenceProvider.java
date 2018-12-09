package com.npspot.jtransitlight.publisher.sequence;

import com.npspot.jtransitlight.contract.Contract;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ReferenceSequenceProvider implements SequenceProvider {

    private Map<String, Long> registry = new ConcurrentHashMap<>();

    @Override
    public void processSequence(String exchange, Contract contract) {
        Long prevSequenceNumber = registry.get(exchange);
        Long sequence;
        if (prevSequenceNumber == null || prevSequenceNumber == Long.MAX_VALUE) {
            sequence = 0L;
        } else {
            sequence = prevSequenceNumber + 1;
        }
        contract.setMessageSequence(sequence);
        registry.put(exchange, sequence);
    }
}
