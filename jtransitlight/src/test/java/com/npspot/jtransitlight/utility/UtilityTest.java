package com.npspot.jtransitlight.utility;

import com.npspot.jtransitlight.JTransitLightException;
import com.npspot.jtransitlight.TestContract;
import com.npspot.jtransitlight.contract.ContractType;
import com.npspot.jtransitlight.publisher.sequence.ReferenceSequenceProvider;
import com.npspot.jtransitlight.publisher.sequence.SequenceProvider;
import com.npspot.jtransitlight.publisher.sequence.TransactionSequenceProvider;
import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.core.IsInstanceOf.instanceOf;


public class UtilityTest {
    @Test
    public void testGetContractExchangeId() throws JTransitLightException {
        TestContract contract = new TestContract();
        String actual = Utility.getContractExchangeId(contract);
        String expected = "NPS.Contracts.Test:TestContract_2";
        Assert.assertEquals("Expect exchange name", expected, actual);
    }

    @Test
    public void getSequenceProviderTest() {
        SequenceProvider trxSequence = Utility.getSequenceProvider(ContractType.TRANSACTION_TYPE);
        Assert.assertThat(trxSequence, instanceOf(TransactionSequenceProvider.class));
        SequenceProvider refSequence = Utility.getSequenceProvider(ContractType.REFERENCE_TYPE);
        Assert.assertThat(refSequence, instanceOf(ReferenceSequenceProvider.class));
    }
}
