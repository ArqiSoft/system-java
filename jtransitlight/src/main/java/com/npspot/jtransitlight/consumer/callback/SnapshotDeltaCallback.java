package com.npspot.jtransitlight.consumer.callback;

import com.npspot.jtransitlight.MessageProcessingException;
import com.npspot.jtransitlight.consumer.callback.message.Acknowledged;

/**
 * Callback interface for receiving messages.
 *
 * @author Rami
 */
public interface SnapshotDeltaCallback extends Acknowledged {

    void onSnapshot(byte[] message) throws MessageProcessingException;

    void onDelta(byte[] message) throws MessageProcessingException;
}
