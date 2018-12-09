package com.npspot.jtransitlight.consumer.callback.message;

import com.npspot.jtransitlight.MessageProcessingException;

public interface MessageCallback extends Acknowledged {

    void onMessage(byte[] bytes) throws MessageProcessingException;

}
