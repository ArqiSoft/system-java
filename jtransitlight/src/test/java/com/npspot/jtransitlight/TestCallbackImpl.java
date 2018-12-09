/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.npspot.jtransitlight;

import com.npspot.jtransitlight.consumer.callback.message.MessageCallback;

import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Rami
 */
public class TestCallbackImpl implements MessageCallback {
    
    int messageCounter = 0;

    public int getMessageCounter() {
        return messageCounter;
    }

    @Override
    public void onMessage(byte[] message) throws MessageProcessingException {
        messageCounter++;
        try {
            String messageStr = new String(message, "UTF-8");
            // System.out.println("Got message: " + exchange + " \n" + headers + " \n" +messageStr);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(TestCallbackImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
