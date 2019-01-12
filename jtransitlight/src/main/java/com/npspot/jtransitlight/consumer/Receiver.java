/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.npspot.jtransitlight.consumer;

import com.npspot.jtransitlight.transport.Connection;
import com.npspot.jtransitlight.transport.JTransitLightTransportException;
import java.net.URI;
import java.util.concurrent.ThreadFactory;

/**
 *
 * @author Rami
 */
public class Receiver {

    /**
     * Factory class to get an instance of IBusControl.
     */
    public static class Factory {

        /**
         * Factory method for getting a ReceiverControl.<br>
         * The control is set up to use:<br>
         * - RabbitMq transport.<br>
         * - JSON serializing.<br>
         * - Persistent exchanges.
         *
         * @param host	Host name or IP address for RabbitMQ access.
         * @param username	User-name for RabbitMQ access.
         * @param password	Password for RabbitMq access.
         * @param handshakeTimeoutMs Timeout when establishing connection,
         * milliseconds, use 0 for default
         * @param threadFactory is for creating threads (EE has managed threads,
         * outside of EE you may use Executors.defaultThreadFactory())
         * @param heartbeatInterval Interval of sending heartbeat, -1 for no
         * heartbeat
         * @return	An IBusControl.
         * @throws JTransitLightTransportException if cought inside
         */
        public static ReceiverBusControl createUsingRabbitMq(String host, String username, String password, int handshakeTimeoutMs, ThreadFactory threadFactory, long heartbeatInterval) throws JTransitLightTransportException {
            return createUsingRabbitMq(
                    Connection.getInstance(host, username, password, handshakeTimeoutMs),
                    threadFactory,
                    heartbeatInterval
            );
        }

        /**
         * Factory method for getting an IBusControl in a context without
         * managed threads (no EE).<br>
         * The control is set up to use:<br>
         * - RabbitMq transport.<br>
         * - JSON serializing.<br>
         * - Persistent exchanges. - Wait for acknowledgement when submitting
         * messages. - Heartbeats
         *
         * @param uri	URI for RabbitMQ.
         * @param username	User-name for RabbitMQ access.
         * @param password	Password for RabbitMq access.
         * @param handshakeTimeoutMs Timeout when establishing connection,
         * milliseconds, use 0 for default
         * @param threadFactory is for creating threads (EE has managed threads,
         * outside of EE you may use Executors.defaultThreadFactory())
         * @param heartbeatInterval Interval of sending heartbeat, -1 for no
         * heartbeat
         * @return	An IBusControl.
         * @throws JTransitLightTransportException if cought inside
         */
        public static ReceiverBusControl createUsingRabbitMq(URI uri, String username, String password, int handshakeTimeoutMs, ThreadFactory threadFactory, long heartbeatInterval) throws JTransitLightTransportException {
            return createUsingRabbitMq(
                    Connection.getInstance(uri, username, password, handshakeTimeoutMs),
                    threadFactory,
                    heartbeatInterval
            );
        }

        /**
         * Factory method for getting an IBusControl with heartbeats.<br>
         * The control is set up to use:<br>
         * - RabbitMq transport.<br>
         * - JSON serializing.<br>
         * - Persistent exchanges. - Wait for acknowledgement when submitting
         * messages. - Heartbeats
         *
         * @param uri	URI for RabbitMQ.
         * @param handshakeTimeoutMs Timeout when establishing connection,
         * milliseconds, use 0 for default
         * @param threadFactory is for creating threads (EE has managed threads,
         * outside of EE you may use Executors.defaultThreadFactory())
         * @param heartbeatInterval Interval of sending heartbeat, -1 for no
         * heartbeat
         * @return	An IBusControl.
         * @throws JTransitLightTransportException
         */
//		public static IBusControl createUsingRabbitMq(URI uri, int handshakeTimeoutMs, ThreadFactory threadFactory, long heartbeatInterval) throws JTransitLightTransportException
//		{
//			return createUsingRabbitMq( Connection.getInstance(uri, handshakeTimeoutMs), threadFactory, heartbeatInterval );
//		}
        /**
         * Factory method for getting an IBusControl without heartbeats.<br>
         * The control is set up to use:<br>
         * - RabbitMq transport.<br>
         * - JSON serializing.<br>
         * - Persistent exchanges. - Wait for acknowledgement when submitting
         * messages.
         *
         * @param uri	URI for RabbitMQ.
         * @param handshakeTimeoutMs Timeout when establishing connection,
         * milliseconds, use 0 for default
         * @return	An IBusControl.
         * @throws JTransitLightTransportException if cought inside
         */
        public static ReceiverBusControl createUsingRabbitMq(URI uri, int handshakeTimeoutMs) throws JTransitLightTransportException {
            ReceiverBusControl busControl = new ReceiverBusControlImpl(Connection.getInstance(uri, handshakeTimeoutMs));
            return busControl;
        }

        private static ReceiverBusControl createUsingRabbitMq(Connection connection, ThreadFactory threadFactory, long heartbeatInterval) throws JTransitLightTransportException {
            ReceiverBusControl busControl = new ReceiverBusControlImpl(connection);

            return busControl;
        }

        /**
         * Factory method for getting an IBusControl.<br>
         * The control is set up to use:<br>
         * - In memory message storage for testing.
         *
         * @return	An IBusControl.
         */
        public static ReceiverBusControl createUsingInMemory() {
            Connection connection = Connection.getInstance();
            ReceiverBusControl busControl = new ReceiverBusControlImpl(connection);
            return busControl;
        }

    }

}
