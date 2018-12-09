package com.npspot.jtransitlight.publisher;

import com.npspot.jtransitlight.contract.ContractType;
import com.npspot.jtransitlight.heartbeat.HeartbeatSender;
import com.npspot.jtransitlight.publisher.sequence.SequenceProvider;
import com.npspot.jtransitlight.transport.Connection;
import com.npspot.jtransitlight.transport.JTransitLightTransportException;
import com.npspot.jtransitlight.utility.Utility;

import java.net.URI;
import java.util.concurrent.ThreadFactory;

/**
 * Factory class to get an instance of IBusControl.
 */
public class Bus {
    /**
     * Factory class to get an instance of IBusControl.
     */
    public static class Factory {
        /**
         * Factory method for getting an IBusControl in a context without managed threads (no EE).<br>
         * The control is set up to use:<br>
         * - RabbitMq transport.<br>
         * - JSON serializing.<br>
         * - Persistent exchanges.
         * - Wait for acknowledgement when submitting messages.
         * - Heartbeats
         *
         * @param host               Host name or IP address for RabbitMQ access.
         * @param username           User-name for RabbitMQ access.
         * @param password           Password for RabbitMq access.
         * @param handshakeTimeoutMs Timeout when establishing connection, milliseconds, use 0 for default
         * @param threadFactory      is for creating threads (EE has managed threads, outside of EE you may use Executors.defaultThreadFactory())
         * @param heartbeatInterval  Interval of sending heartbeat, -1 for no heartbeat
         * @return An IBusControl.
         * @throws JTransitLightTransportException
         */
        public static IBusControl createUsingRabbitMq(String host, String username, String password, int handshakeTimeoutMs, ThreadFactory threadFactory, long heartbeatInterval, ContractType contractType) throws JTransitLightTransportException {
            return createUsingRabbitMq(
                    Connection.getInstance(host, username, password, handshakeTimeoutMs),
                    threadFactory,
                    heartbeatInterval,
                    contractType
            );
        }

        /**
         * Factory method for getting an IBusControl in a context without managed threads (no EE).<br>
         * The control is set up to use:<br>
         * - RabbitMq transport.<br>
         * - JSON serializing.<br>
         * - Persistent exchanges.
         * - Wait for acknowledgement when submitting messages.
         * - Heartbeats
         *
         * @param uri                URI for RabbitMQ.
         * @param username           User-name for RabbitMQ access.
         * @param password           Password for RabbitMq access.
         * @param handshakeTimeoutMs Timeout when establishing connection, milliseconds, use 0 for default
         * @param threadFactory      is for creating threads (EE has managed threads, outside of EE you may use Executors.defaultThreadFactory())
         * @param heartbeatInterval  Interval of sending heartbeat, -1 for no heartbeat
         * @return An IBusControl.
         * @throws JTransitLightTransportException
         */
        public static IBusControl createUsingRabbitMq(URI uri, String username, String password, int handshakeTimeoutMs, ThreadFactory threadFactory, long heartbeatInterval, ContractType contractType) throws JTransitLightTransportException {
            return createUsingRabbitMq(
                    Connection.getInstance(uri, username, password, handshakeTimeoutMs),
                    threadFactory,
                    heartbeatInterval,
                    contractType
            );
        }

        /**
         * Factory method for getting an IBusControl with heartbeats.<br>
         * The control is set up to use:<br>
         * - RabbitMq transport.<br>
         * - JSON serializing.<br>
         * - Persistent exchanges.
         * - Wait for acknowledgement when submitting messages.
         * - Heartbeats
         *
         * @param uri                URI for RabbitMQ.
         * @param handshakeTimeoutMs Timeout when establishing connection, milliseconds, use 0 for default
         * @param threadFactory      is for creating threads (EE has managed threads, outside of EE you may use Executors.defaultThreadFactory())
         * @param heartbeatInterval  Interval of sending heartbeat, -1 for no heartbeat
         * @return An IBusControl.
         * @throws JTransitLightTransportException
         */
        public static IBusControl createUsingRabbitMq(URI uri, int handshakeTimeoutMs, ThreadFactory threadFactory, long heartbeatInterval, ContractType contractType) throws JTransitLightTransportException {
            return createUsingRabbitMq(Connection.getInstance(uri, handshakeTimeoutMs), threadFactory, heartbeatInterval, contractType);
        }

        /**
         * Factory method for getting an IBusControl without heartbeats.<br>
         * The control is set up to use:<br>
         * - RabbitMq transport.<br>
         * - JSON serializing.<br>
         * - Persistent exchanges.
         * - Wait for acknowledgement when submitting messages.
         *
         * @param uri                URI for RabbitMQ.
         * @param handshakeTimeoutMs Timeout when establishing connection, milliseconds, use 0 for default
         * @return An IBusControl.
         * @throws JTransitLightTransportException
         */
        public static IBusControl createUsingRabbitMq(URI uri, int handshakeTimeoutMs, ContractType contractType) throws JTransitLightTransportException {
            SequenceProvider sequenceProvider = Utility.getSequenceProvider(contractType);
            IBusControl busControl = new BusControl(Connection.getInstance(uri, handshakeTimeoutMs), sequenceProvider);
            return busControl;
        }

        private static IBusControl createUsingRabbitMq(Connection connection, ThreadFactory threadFactory, long heartbeatInterval, ContractType contractType) throws JTransitLightTransportException {
            SequenceProvider sequenceProvider = Utility.getSequenceProvider(contractType);
            IBusControl busControl = new BusControl(connection, sequenceProvider);
            if (0 < heartbeatInterval)
                HeartbeatSender.startSender(busControl, threadFactory, heartbeatInterval);
            return busControl;
        }

        /**
         * Factory method for getting an IBusControl.<br>
         * The control is set up to use:<br>
         * - In memory message storage for testing.
         *
         * @return An IBusControl.
         */
        public static IBusControl createUsingInMemory(ContractType contractType) {
            Connection connection = Connection.getInstance();
            SequenceProvider sequenceProvider = Utility.getSequenceProvider(contractType);
            IBusControl busControl = new BusControl(connection, sequenceProvider);
            return busControl;
        }
    }
}
