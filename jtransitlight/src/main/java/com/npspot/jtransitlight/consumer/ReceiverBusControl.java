/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.npspot.jtransitlight.consumer;

/**
 *
 * @author Rami
 */
public interface ReceiverBusControl extends ReceiverEndpoint{

    /**
     * Configure the system to use JSON serializing (which is the default).
     */
    void useJsonSerializer();

    boolean isOpen();
}
