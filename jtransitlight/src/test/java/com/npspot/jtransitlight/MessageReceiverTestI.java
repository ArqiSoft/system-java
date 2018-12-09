package com.npspot.jtransitlight;

import com.npspot.jtransitlight.consumer.Receiver;
import com.npspot.jtransitlight.consumer.ReceiverBusControl;
import com.npspot.jtransitlight.contract.ContractType;
import com.npspot.jtransitlight.publisher.Bus;
import com.npspot.jtransitlight.publisher.IBusControl;
import com.npspot.jtransitlight.transport.JTransitLightTransportException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.Executors;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * @author Rami
 */
public class MessageReceiverTestI {
    private IBusControl sender;
    private ReceiverBusControl receiver;

    @Before
    public void setup() throws JTransitLightTransportException, URISyntaxException {
//		sender = Bus.Factory.createUsingRabbitMq("localhost", "guest", "guest");
        sender = Bus.Factory.createUsingRabbitMq(new URI("amqp://guest:guest@192.168.20.2"), 30000, Executors.defaultThreadFactory(), 5000, ContractType.TRANSACTION_TYPE);
        receiver = Receiver.Factory.createUsingRabbitMq(new URI("amqp://guest:guest@192.168.20.2"), 30000);

    }

    @Test
    public void testBusConnection() {
        Assert.assertNotNull("Expect to have a bus available", sender);
    }

    @Test
    public void testPublishAndReceive() throws InterruptedException, JTransitLightException, IOException {

        TestCallbackImpl receiverCallback = new TestCallbackImpl();
        receiver.subscribe(Arrays.asList("NPS.Contracts.Test:TestContract_2"), "receiverQueue", false, receiverCallback);

        Error errorReceiver = new Error();
        receiver.subscribe(Arrays.asList("NPS.Contracts.Test:TestContract_2_error"), "errorQueue", false, errorReceiver);

        for (int i = 0; i < 10000; i++) {
            try {

                TestContract contract = new TestContract();
                contract.setName("Ketil");
                contract.setYearOfBirth(1960);
                contract.setMessageSequence((long) i);
                contract.setSnapshot(false);

                sender.publish(contract);
                System.out.println("Message sent! " + i);
            } catch (Exception ex) {
                System.out.println("Message NOT sent! " + i);
                ex.printStackTrace();
            }
        }

        while (receiverCallback.getMessageCounter() < 10000) {
            Thread.sleep(100);
        }
        System.out.println("done");

        try {
            receiver.unSubscribe(Collections.singletonList("NPS.Contracts.Test:TestContract_2"), "receiverQueue");
            receiver.unSubscribe(Collections.singletonList("NPS.Contracts.Test:TestContract_2_error"), "errorQueue");
            receiver.dispose();
            sender.dispose();
        } catch (JTransitLightTransportException ex) {
            ex.printStackTrace();
        }
    }
}
