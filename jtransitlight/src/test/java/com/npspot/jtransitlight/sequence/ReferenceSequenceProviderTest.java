package com.npspot.jtransitlight.sequence;

import com.npspot.jtransitlight.TestContract;
import com.npspot.jtransitlight.contract.Contract;
import com.npspot.jtransitlight.publisher.sequence.ReferenceSequenceProvider;
import com.npspot.jtransitlight.publisher.sequence.SequenceProvider;
import com.npspot.jtransitlight.transport.JTransitLightTransportException;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ReferenceSequenceProviderTest {

    private String exchange = "testExchange";

    @Test
    public void processSequenceNewExchangeTest() throws JTransitLightTransportException {
        SequenceProvider sequenceProvider = new ReferenceSequenceProvider();
        Contract contract = new TestContract();
        sequenceProvider.processSequence(exchange, contract);
        Assert.assertEquals("New exchange sequence", new Long(0), contract.getMessageSequence());
    }

    @Test
    public void processSequenceTest() throws NoSuchFieldException, IllegalAccessException, JTransitLightTransportException {
        Long prevSequence = (long) (Math.random() * Long.MAX_VALUE);
        Map<String, Long> registry = new ConcurrentHashMap<>();
        registry.put(exchange, prevSequence);

        SequenceProvider sequenceProvider = getCustomisedSequenceProvider(registry);

        Contract contract = new TestContract();
        sequenceProvider.processSequence(exchange, contract);
        Assert.assertEquals("Sequence", new Long(prevSequence + 1), contract.getMessageSequence());
    }

    @Test
    public void processSequenceMaxValue() throws NoSuchFieldException, IllegalAccessException, JTransitLightTransportException {
        Map<String, Long> registry = new ConcurrentHashMap<>();
        registry.put(exchange, Long.MAX_VALUE);

        SequenceProvider sequenceProvider = getCustomisedSequenceProvider(registry);

        Contract contract = new TestContract();
        sequenceProvider.processSequence(exchange, contract);
        Assert.assertEquals("Max sequence", new Long(0), contract.getMessageSequence());
    }

    private SequenceProvider getCustomisedSequenceProvider(Map<String, Long> registry) throws IllegalAccessException, NoSuchFieldException {
        SequenceProvider sequenceProvider = new ReferenceSequenceProvider();

        Field field = ReferenceSequenceProvider.class.getDeclaredField("registry");
        field.setAccessible(true);
        field.set(sequenceProvider, registry);
        return sequenceProvider;
    }

}
