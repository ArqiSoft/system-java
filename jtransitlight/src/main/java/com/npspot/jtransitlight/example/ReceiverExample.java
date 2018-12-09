/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.npspot.jtransitlight.example;

import com.npspot.jtransitlight.JTransitLightException;
import com.npspot.jtransitlight.MessageProcessingException;
import com.npspot.jtransitlight.consumer.Receiver;
import com.npspot.jtransitlight.consumer.ReceiverBusControl;
import com.npspot.jtransitlight.consumer.callback.message.MessageCallback;
import com.npspot.jtransitlight.transport.JTransitLightTransportException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Rami
 */
public class ReceiverExample {
     private void receiveExampleMessages() {
        
        try {
            //First we get an instance of ReceiverBusControl using the Receiver Factory
            int handshakeTimeoutMs = 5000;
            ReceiverBusControl receiverBus = Receiver.Factory.createUsingRabbitMq(new URI("amqp://guest:guest@192.168.20.2/%2F"), handshakeTimeoutMs);
            
            //Next we determine the exchange we want to connect to
            //In MassTransit in C#, the exchange name is determined by the namespace and the class name
            //In JTransitLight the exchange name is freely definable and if the publisher is using JTransitLight
            //They will have implemented the Contract interface, which defines two methods getNamespace() and getContractName()
            /**
             *   // Contract
             *
             * @JsonIgnore final private static String namespace =
             * "NPS.Contracts.Intraday";
             * @JsonIgnore final private static String contractName =
             * "TradeEvent";
             */
            //The exchange name is the namespace + ":" + contractName
            //So in the above example it would be NPS.Contracts.Intraday:TradeEvent
            CallbackExample exampleCallback = new CallbackExample();
            
            //We subscribe to the exchange, which in practice means binding the queue to the list of the exchanges provided
            //It is possible to bind a queue to multiple exchanges.
            //The queue name is arbitrary 
            //NOTE that each call to subscribe creates a new DefaultConsumer in RabbitMQ and calls exchangeDeclare and queueBind
            receiverBus.subscribe(Arrays.asList("com.nps.test.connection.errors:TestContract"),"receiverQueueName", false,exampleCallback);
            
            //That is all, now exampleCallback.deltaReceived is called whenever a message is received.
            
            
        } catch (URISyntaxException | IOException ex) {
            Logger.getLogger(ReceiverExample.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JTransitLightTransportException ex) {
            Logger.getLogger(ReceiverExample.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JTransitLightException ex) {
            Logger.getLogger(ReceiverExample.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    private class CallbackExample implements MessageCallback {

        @Override
        public void onMessage(byte[] message) throws MessageProcessingException {
            //Do something with the message
            System.out.println("Received delta: " + new String(message));
        }
    }
    
    public static void main(String args[]) {
        ReceiverExample receiver = new ReceiverExample();
        receiver.receiveExampleMessages();
    }
}
