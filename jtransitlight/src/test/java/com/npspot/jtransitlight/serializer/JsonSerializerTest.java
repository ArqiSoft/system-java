package com.npspot.jtransitlight.serializer;

import com.npspot.jtransitlight.JTransitLightException;
import com.npspot.jtransitlight.TestContract;
import com.npspot.jtransitlight.publisher.model.BusMessage;
import com.npspot.jtransitlight.utility.Utility;
import org.junit.Assert;
import org.junit.Test;

import java.util.UUID;
import org.junit.Ignore;

public class JsonSerializerTest {

    @Ignore("unstable")
    @Test
    public void testDefaultSerialize() throws JTransitLightException {
        TestContract contract = new TestContract();
        contract.setName("Ketil");
        contract.setYearOfBirth(1960);

        UUID conversationId = Utility.getUniqueId();
        UUID messageId = Utility.getUniqueId();

        BusMessage<TestContract> busMessage = new BusMessage<TestContract>();
        busMessage.setConversationId(conversationId);
        busMessage.setMessageId(messageId);
        busMessage.setMessageType(Utility.getContractExchangeId(contract));
        busMessage.setMessage(contract);
        busMessage.setTargetAddress("rabbitmq://localhost:15672/" + Utility.getContractExchangeId(contract));

        Serializer serializer = new Serializer();
        String serializedMessage = serializer.serialize(busMessage);
        System.out.println(serializedMessage);

        String expected2 = "{\"messageId\":\"" + messageId + "\",\"conversationId\":\"" + conversationId + "\",\"headers\":null,\"host\":null,\"messageType\":[\"NPS.Contracts.Test:TestContract_2\"],\"destinationAddress\":\"rabbitmq://localhost:15672/NPS.Contracts.Test:TestContract_2\",\"sourceAddress\":null,\"message\":{\"messageSequence\":null,\"snapshot\":false,\"name\":\"Ketil\",\"yearOfBirth\":1960,\"queueName\":\"\",\"id\":\"00000000-0000-0000-0000-000000000000\",\"supplierSettings\":{\"persistent\":false}}}";
        Assert.assertEquals("Expect message", expected2, serializedMessage);
    }
}
