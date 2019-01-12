package com.npspot.jtransitlight.transport;

import com.npspot.jtransitlight.MessageProcessingException;
import com.npspot.jtransitlight.consumer.DataConsumerWatcher;
import com.npspot.jtransitlight.consumer.ErrorHeaders;
import com.npspot.jtransitlight.consumer.RefDataConsumerWatcher;
import com.npspot.jtransitlight.consumer.TrxDataConsumerWatcher;
import com.npspot.jtransitlight.consumer.callback.SnapshotDeltaCallback;
import com.npspot.jtransitlight.consumer.callback.message.MessageCallback;
import com.npspot.jtransitlight.consumer.processor.RefDataRestExecutor;
import com.npspot.jtransitlight.consumer.processor.RestExecutor;
import com.npspot.jtransitlight.consumer.processor.TrxDataRestExecutor;
import com.npspot.jtransitlight.consumer.setting.ConsumerSettings;
import com.npspot.jtransitlight.publisher.model.RabbitMQMessageContext;
import com.npspot.jtransitlight.transport.rabbitmq.RabbitMqReceiver;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Transport connection class hiding the actual implementation of the transport.
 * Currently supporting RabbitMQ and an in memory alternative for testing.
 */
public class Connection {
    /**
     * Implementation of RabbitMQ transport connection.
     */
    private RabbitMqReceiver transport;
    private final ScheduledExecutorService slowTickExecutor;
    private final ConcurrentMap<String, DataConsumerWatcher> lastTimeToConsumerWatcher = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, ScheduledExecutorService> restExecutorsMap = new ConcurrentHashMap<>();
    private boolean disposed = false;

    /**
     * Map used for holding messages when using in memory testing.
     */
    private Map<String, List<byte[]>> inMemory = new ConcurrentHashMap<>();

    private Map<String, String> sourceAddressCache = new ConcurrentHashMap<>();

    private final Logger LOGGER = LogManager.getLogger(Connection.class);

    /**
     * Close underlying transport (RabbitMQ) connection.
     *
     * @throws JTransitLightTransportException
     */
    public void dispose() throws JTransitLightTransportException {
        if (transport != null) {
            transport.dispose();
            transport = null;
            disposed = true;
        }

        if (slowTickExecutor != null) {
            slowTickExecutor.shutdown();
        }

        if (!restExecutorsMap.isEmpty()) {
            restExecutorsMap.forEach((s, scheduledExecutorService) -> scheduledExecutorService.shutdown());
        }
    }

    /**
     * Checks if connection is open
     * @return if connection is open
     */
    public boolean isOpen() {
        return transport.isOpen();
    }

    /**
     * Create a transport connection with RabbitMQ.
     *
     * @param host               Host name or IP address for RabbitMQ access.
     * @param username           User-name for RabbitMQ access.
     * @param password           Password for RabbitMq access.
     * @param handshakeTimeoutMs Timeout when establishing connection, use 0 for default.
     * @return An instance of this class with an initialized RabbitMQ connection.
     * @throws JTransitLightTransportException
     */
    public static Connection getInstance(String host, String username, String password, int handshakeTimeoutMs) throws JTransitLightTransportException {
        URI uri = null;
        RabbitMqReceiver transport = new RabbitMqReceiver(uri, host, username, password, handshakeTimeoutMs);
        return new Connection(transport);
    }

    /**
     * Create a transport connection with RabbitMQ.
     *
     * @param uri                URI for RabbitMQ.
     * @param username           User-name for RabbitMQ access.
     * @param password           Password for RabbitMq access.
     * @param handshakeTimeoutMs Timeout when establishing connection, use 0 for default.
     * @return An instance of this class with an initialized RabbitMQ connection.
     * @throws JTransitLightTransportException
     */
    public static Connection getInstance(URI uri, String username, String password, int handshakeTimeoutMs) throws JTransitLightTransportException {
        String host = null;
        RabbitMqReceiver transport = new RabbitMqReceiver(uri, host, username, password, handshakeTimeoutMs);
        return new Connection(transport);
    }

    /**
     * Create a transport connection with RabbitMQ.
     *
     * @param uri                URI for RabbitMQ.
     * @param handshakeTimeoutMs Timeout when establishing connection, use 0 for default.
     * @return An instance of this class with an initialized RabbitMQ connection.
     * @throws JTransitLightTransportException
     */
    public static Connection getInstance(URI uri, int handshakeTimeoutMs) throws JTransitLightTransportException {
        String host = null;
        String username = null;
        String password = null;
        RabbitMqReceiver transport = new RabbitMqReceiver(uri, host, username, password, handshakeTimeoutMs);
        return new Connection(transport);
    }

    /**
     * Create a transport with in memory storage for teting.
     *
     * @return
     */
    public static Connection getInstance() {
        return new Connection(null);
    }

    /**
     * Construct an instance with reference to transport.
     *
     * @param transport
     */
    private Connection(RabbitMqReceiver transport) {
        this.transport = transport;

        this.slowTickExecutor = Executors.newSingleThreadScheduledExecutor();
        this.slowTickExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (lastTimeToConsumerWatcher.size() > 0) {
                    for (String key : lastTimeToConsumerWatcher.keySet()) {
                        DataConsumerWatcher dataConsumerWatcher = lastTimeToConsumerWatcher.get(key);
                        long timeDelta = System.currentTimeMillis() - dataConsumerWatcher.getLastTimeStamp();
                        if (timeDelta > TimeUnit.SECONDS.toMillis(5)) {
                            dataConsumerWatcher.triggerReSync();
                        } else if (timeDelta < 0) {
                            dataConsumerWatcher.resetLastTimeStamp();
                        }
                    }
                }
            }
        }, 10, 10, TimeUnit.SECONDS);
    }

    /**
     * Publish a message to the given exchange.
     *
     * @param rabbitMQMessageContext rabbitMQMessageContext rabbitmq message info
     * @throws JTransitLightTransportException
     */
    public void publish(RabbitMQMessageContext rabbitMQMessageContext) throws JTransitLightTransportException {
        if (disposed)
            throw new JTransitLightTransportException("Error: Trying to publish with a disposed connection!");
        else if (transport != null)
            transport.publish(rabbitMQMessageContext);
        else {
            List<byte[]> messages = inMemory.get(rabbitMQMessageContext.getExchange());
            if (messages == null) {
                messages = Collections.synchronizedList(new LinkedList<byte[]>());
                inMemory.put(rabbitMQMessageContext.getExchange(), messages);
            }

            messages.add(rabbitMQMessageContext.getMessage());
        }

    }

    public void publishError(String exchangeName, String messageId, byte[] message, Map<String, String> headers) throws JTransitLightTransportException {
        if (disposed) {
            throw new JTransitLightTransportException("Error: Trying to publish with a disposed connection!");
        } else if (transport != null) {
            transport.publishError(exchangeName, messageId, message, headers);
        }
    }

    public void subscribe(List<String> exchangeName, String queueName, ConsumerSettings settings, final MessageCallback callback) throws JTransitLightTransportException, IOException {
        transport.declareAndBindQueue(queueName, exchangeName, settings);

        Consumer consumer = new DefaultConsumer(transport.getChannel()) {

            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                try {
                    callback.onMessage(body);
                } catch (Exception ex) {
                    try {
                        LOGGER.error("Exception in SnapshotDeltaCallback handleDelivery", ex);
                        String exchange = envelope.getExchange();
                        System.out.println("publishing error message: " + exchange);
                        publishError(exchange, properties.getMessageId(), body, ErrorHeaders.getErrorHeaders(ex));
                    } catch (JTransitLightTransportException ex1) {
                        LOGGER.warn("Unable to publish message to error exchange", ex);
                    }
                } finally {

                    if (!settings.isAutoAck() && callback.isAcknowledged()) {

                        LOGGER.info("Message {} has been successfully isAcknowledged and released.", () -> new String(body));
                        this.getChannel().basicAck(envelope.getDeliveryTag(), false);
                    }
                }
            }
        };
        transport.getChannel().basicConsume(queueName, settings.isAutoAck(), consumer);
    }

    public void subscribeRefData(String restURL, long restReSyncInterval, final SnapshotDeltaCallback messageCallback) {
        final RefDataRestExecutor restExecutor = new RefDataRestExecutor(restURL, messageCallback);

        ScheduledExecutorService restScheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        restScheduledExecutorService.scheduleAtFixedRate(() -> {
            try {
                receiveAndParseSnapshot(restExecutor, restURL);
            } catch (IOException | MessageProcessingException e) {
                LOGGER.error("Cannot re-sync: " + restURL + ". Exception: " + e);
            }
        }, 0, restReSyncInterval, TimeUnit.MILLISECONDS);

        restExecutorsMap.put(restURL, restScheduledExecutorService);
    }

    public void subscribeRefData(String exchangeName, String queueName, ConsumerSettings settings, String restURL, boolean isHandleReSync, final SnapshotDeltaCallback messageCallback) throws JTransitLightTransportException, IOException, MessageProcessingException {
        transport.declareAndBindQueue(queueName, exchangeName, settings);

        final RefDataRestExecutor restExecutor = new RefDataRestExecutor(restURL, messageCallback);
        receiveAndParseSnapshot(restExecutor, restURL);

        final RefDataConsumerWatcher consumer =
                new RefDataConsumerWatcher(transport.getChannel(), restExecutor, isHandleReSync, settings, messageCallback);


        if (isHandleReSync) {
            lastTimeToConsumerWatcher.put(exchangeName + "|" + queueName, consumer);
        }

        try {
            transport.getChannel().basicConsume(queueName, settings.isAutoAck(), consumer);
        } catch (IOException e) {
            LOGGER.error("Basic consume failed: queue " + queueName + ". " + e.getMessage());
            throw e;
        }
        LOGGER.info("Snapshot received and parsed from " + restURL + " successfully. Subscribed to " + exchangeName + " - " + queueName + " successfully.");
    }

    public void subscribeTrxData(String restURL, long restReSyncInterval, long startTimeUTC, long endTimeUTC, final SnapshotDeltaCallback messageCallback) {
        final TrxDataRestExecutor restExecutor = new TrxDataRestExecutor(restURL, startTimeUTC, endTimeUTC, messageCallback);

        ScheduledExecutorService restScheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        restScheduledExecutorService.scheduleAtFixedRate(() -> {
            try {
                receiveAndParseSnapshot(restExecutor, restURL);
            } catch (IOException | MessageProcessingException e) {
                LOGGER.error("Cannot re-sync: " + restURL + ". Exception: " + e);
            }
        }, 0, restReSyncInterval, TimeUnit.MILLISECONDS);

        restExecutorsMap.put(restURL, restScheduledExecutorService);
    }

    public void subscribeTrxData(String exchangeName, String queueName, ConsumerSettings settings, String restURL, long startTimeUTC, long endTimeUTC, boolean isHandleReSync, final SnapshotDeltaCallback messageCallback) throws JTransitLightTransportException, IOException, MessageProcessingException {
        transport.declareAndBindQueue(queueName, exchangeName, settings);

        final TrxDataRestExecutor restExecutor = new TrxDataRestExecutor(restURL, startTimeUTC, endTimeUTC, messageCallback);
        receiveAndParseSnapshot(restExecutor, restURL);
        final TrxDataConsumerWatcher consumer =
                new TrxDataConsumerWatcher(transport.getChannel(), restExecutor, isHandleReSync, settings, messageCallback);

        if (isHandleReSync) {
            lastTimeToConsumerWatcher.put(exchangeName + "|" + queueName, consumer);
        }

        try {
            transport.getChannel().basicConsume(queueName, settings.isAutoAck(), consumer);
        } catch (IOException e) {
            LOGGER.error("Basic consume failed: queue " + queueName + ". " + e.getMessage());
            throw e;
        }
    }

    private void receiveAndParseSnapshot(RestExecutor restExecutor, String restUrl) throws IOException, MessageProcessingException {
        try {
            restExecutor.executeSnapshotRequest();
        } catch (IOException e) {
            LOGGER.error("Error receiving snapshot from url: " + restUrl + ". " + e.getMessage());
            throw e;
        } catch (MessageProcessingException e) {
            LOGGER.error("Error processing received snapshot with provided callback. " + e.getMessage());
            throw e;
        }
    }

    /**
     * Set or reset the use durable exchanges property.
     *
     * @param useDurableExchanges
     * @throws JTransitLightTransportException
     */
    public void setUseDurableExchanges(boolean useDurableExchanges) throws JTransitLightTransportException {
        if (disposed)
            throw new JTransitLightTransportException("Error: Trying to set properties on a disposed connection!");
        else if (transport != null)
            transport.setUseDurableExchanges(useDurableExchanges);
    }

    /**
     * Set or reset waiting for acknowledgement when publishing messages.
     *
     * @param waitForAcknowledgement
     * @throws JTransitLightTransportException if disposed
     */
    public void setWaitForAcknowledgement(boolean waitForAcknowledgement) throws JTransitLightTransportException {
        if (disposed)
            throw new JTransitLightTransportException("Error: Trying to set properties on a disposed connection!");
        else if (transport != null)
            transport.setWaitForAcknowledgement(waitForAcknowledgement);
    }

    /**
     * Set the Publish message acknowledgement timeout in milliseconds.
     *
     * @param acknowledgementTimeout Timeout for the aks
     * @throws JTransitLightTransportException if disposed
     */
    public void setAcknowledgementTimeout(long acknowledgementTimeout) throws JTransitLightTransportException {
        if (disposed)
            throw new JTransitLightTransportException("Error: Trying to set properties on a disposed connection!");
        else if (transport != null)
            transport.setAcknowledgementTimeout(acknowledgementTimeout);
    }

    public String getDestinationString() {
        if (transport != null) {
            return transport.getConnectionString();
        }
        return "";
    }

    public List<byte[]> fetchInternalMessages(String contractId) {
        List<byte[]> messages = inMemory.get(contractId);
        inMemory.remove(contractId);
        return messages;
    }

    public String getSourceAddressString(String exchangeName, String queueName) {
        if (sourceAddressCache.containsKey(exchangeName)) {
            return sourceAddressCache.get(exchangeName);
        }
        try {
            StringBuilder builder = new StringBuilder();
            builder.append("bus-");
            builder.append(InetAddress.getLocalHost().getHostName());
            builder.append("-");
            builder.append(exchangeName);
            builder.append(queueName);
            sourceAddressCache.put(exchangeName, builder.toString());
            return sourceAddressCache.get(exchangeName);
        } catch (UnknownHostException ex) {
            LOGGER.error("Error, unknown host", ex);
            return null;
        }
    }

    public void triggerRestReSync(String restURL, final SnapshotDeltaCallback messageCallback) throws IOException, MessageProcessingException {
        final RefDataRestExecutor restExecutor = new RefDataRestExecutor(restURL, messageCallback);
        receiveAndParseSnapshot(restExecutor, restURL);
    }

    public void unsubscribe(String exchangeName, String queueName) throws IOException {
        unsubscribe(Arrays.asList(exchangeName), queueName);
    }

    public void unsubscribe(List<String> exchangeName, String queueName) throws IOException {
        transport.unbindQueue(queueName, exchangeName);

        for (int i = 0; i < exchangeName.size(); ++i) {
            lastTimeToConsumerWatcher.remove(exchangeName + "|" + queueName);
        }
    }

    public void unsubscribe(String restURL) {
        if (restExecutorsMap.containsKey(restURL)) {
            ScheduledExecutorService restScheduledExecutorService = restExecutorsMap.get(restURL);
            restScheduledExecutorService.shutdown();
            restExecutorsMap.remove(restURL);
        }
    }

    public void cleanInternalMessages() {
        this.inMemory.clear();
    }

    public RabbitMqReceiver getTransport() {
        return transport;
    }
}
