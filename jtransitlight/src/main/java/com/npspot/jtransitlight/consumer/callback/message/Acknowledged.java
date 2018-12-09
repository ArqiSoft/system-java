package com.npspot.jtransitlight.consumer.callback.message;

/**
 * Interfaces provides the Acknowledged status of the message accepted.
 * <p>
 * This flag is only considered, if the auto ack rabbit mechanism is implicitly turned off, which means the client
 * becomes responsible for handling ack status depends on its needs.
 *
 * @see com.npspot.jtransitlight.transport.Connection
 */
public interface Acknowledged {

    default boolean isAcknowledged() {

        return false;
    }
}
