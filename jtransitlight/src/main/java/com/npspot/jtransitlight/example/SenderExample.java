/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.npspot.jtransitlight.example;

import com.npspot.jtransitlight.contract.ContractType;
import com.npspot.jtransitlight.publisher.Bus;
import com.npspot.jtransitlight.publisher.IBusControl;
import com.npspot.jtransitlight.transport.JTransitLightTransportException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Rami
 */
public class SenderExample {

    public static void main(String args[]) {

        long sequenceNumber = 0;
        int handshakeTimeoutMs = 5000;
        try {
            IBusControl control = Bus.Factory.createUsingRabbitMq(new URI("amqp://guest:guest@192.168.20.2/%2F"), handshakeTimeoutMs, ContractType.TRANSACTION_TYPE);

            while (true) {
                try {
                    TestContract contract = new TestContract();
                    contract.setMessageSequence(sequenceNumber++);// skip this for reference contract type, it will be set inside of JTransitLight
                    contract.setTestProperty("Test string " + sequenceNumber);
                    contract.setSnapshot(false);
                    control.publish(contract);

                } catch (Exception ex) {
                    Logger.getLogger(SenderExample.class.getName()).log(Level.SEVERE, "error", ex);
                }
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(SenderExample.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        } catch (JTransitLightTransportException | URISyntaxException ex) {
            Logger.getLogger(SenderExample.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
