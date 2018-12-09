package com.npspot.jtransitlight.sequence;

import com.npspot.jtransitlight.TestContract;
import com.npspot.jtransitlight.contract.Contract;
import com.npspot.jtransitlight.publisher.sequence.TransactionSequenceProvider;
import com.npspot.jtransitlight.transport.JTransitLightTransportException;
import org.junit.Assert;
import org.junit.Test;

public class TrxDataSequenceProviderTest {

    private String exchange = "testExchange";

    @Test
    public void testValidContract() throws JTransitLightTransportException {
        Long expected = 1L;

        Contract contract = new TestContract();
        contract.setMessageSequence(expected);

        TransactionSequenceProvider transactionSequenceProvider = new TransactionSequenceProvider();
        transactionSequenceProvider.processSequence(exchange, contract);
        Assert.assertEquals("The same SN: ", expected, contract.getMessageSequence());
    }

    @Test(expected = JTransitLightTransportException.class)
    public void testInvalidContract() throws JTransitLightTransportException {
        TransactionSequenceProvider transactionSequenceProvider = new TransactionSequenceProvider();
        transactionSequenceProvider.processSequence(exchange, new TestContract());
    }

}
