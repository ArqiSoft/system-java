package com.npspot.jtransitlight.transport.rabbitmq;

import com.npspot.jtransitlight.publisher.model.RabbitMQMessageContext;
import com.npspot.jtransitlight.publisher.model.SupplierSettings;
import com.npspot.jtransitlight.transport.JTransitLightTransportException;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * Implementation of RabbitMQ transport for JTransitLight.
 */
public class RabbitMq {
    protected boolean useDurableExchanges;
    /**
     * Wait for acknowledgement when publishing flag.
     */
    protected boolean waitForAcknowledgement;
    /**
     * Timeout when waiting for acknowledgement from RabbitMQ.
     */
    protected long acknowledgementTimeout;
    /**
     * Rabbit MQ connection.
     */
    Connection connection;
    /**
     * Rabbit MQ channel.
     */
    Channel channel;
    Channel errorChannel;
    private List<String> declaredExchanges = new ArrayList<>();


    /**
     * Create a RabbitMQ connection object.<br>
     * The constructor use all parameters with value different from null.<br>
     * Default is to use durable exchanges.<br>
     * Default acknowledgement strategy is to wait for acknowledgement.<br>
     * Default acknowledgement timeout is set to 15s.<br>
     *
     * @param uri                URI for RabbitMQ.
     * @param host               Host name or IP address for RabbitMQ access.
     * @param username           User-name for RabbitMQ access.
     * @param password           Password for RabbitMq access.
     * @param handshakeTimeoutMs Timeout when establishing connection, pass 0 to use default.
     * @throws JTransitLightTransportException
     */
    public RabbitMq(URI uri, String host, String username, String password, int handshakeTimeoutMs) throws JTransitLightTransportException {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            if (uri != null)
                factory.setUri(uri.toString());
            if (host != null)
                factory.setHost(host);
            if (username != null)
                factory.setUsername(username);
            if (password != null)
                factory.setPassword(password);

            if (0 < handshakeTimeoutMs)
                factory.setHandshakeTimeout(handshakeTimeoutMs);
            factory.setAutomaticRecoveryEnabled(true);
            connection = factory.newConnection();
            channel = connection.createChannel();
            errorChannel = connection.createChannel();

            useDurableExchanges = true;
            waitForAcknowledgement = true;
            acknowledgementTimeout = 15000;

        } catch (IOException | TimeoutException | KeyManagementException | NoSuchAlgorithmException | URISyntaxException exception) {
            throw new JTransitLightTransportException("Unable to get a channel to RabbitMQ!", exception);
        }
    }

    public void dispose() throws JTransitLightTransportException {
        try {
            connection.close();
        } catch (IOException exception) {
            throw new JTransitLightTransportException("Unable to close RabbitMQ connection!", exception);
        }
    }

    /**
     * Checks if connection is open
     *
     * @return if connection is open
     */
    public boolean isOpen() {
        return connection.isOpen();
    }

    /**
     * Publish a message (byte array) to the given exchange.
     *
     * @param context rabbit mq message info
     * @throws JTransitLightTransportException
     */
    public void publish(RabbitMQMessageContext context) throws JTransitLightTransportException {
        try {
            String exchangeName = context.getExchange();
            if (!declaredExchanges.contains(exchangeName)) {
                channel.exchangeDeclare(exchangeName, "fanout", useDurableExchanges);
                declaredExchanges.add(exchangeName);
            }
            Map<String, Object> headers = context.getHeaders();
            headers.put("Content-Type", "application/vnd.masstransit+json");

            SupplierSettings settings = context.getSupplierSettings() != null ? context.getSupplierSettings() : new SupplierSettings();
            AMQP.BasicProperties properties = new AMQP.BasicProperties().builder()
                    .contentType("application/vnd.masstransit+json")
                    .deliveryMode(settings.isPersistent() ? 2 : 1)
                    .priority(1)
                    .headers(headers)
                    .messageId(context.getMessageId())
                    .build();
            channel.basicPublish(exchangeName, "", properties, context.getMessage());
        } catch (IOException exception) {
            throw new JTransitLightTransportException("Unable to publish a message to RabbitMQ!", exception);
        }

    }

    /**
     * Turn on or off the use of durable exchanges.
     *
     * @param useDurableExchanges
     */
    public void setUseDurableExchanges(boolean useDurableExchanges) {
        this.useDurableExchanges = useDurableExchanges;
    }

    /**
     * Turn on or off waiting for acknowledgement after each publish.
     *
     * @param waitForAcknowledgement
     */
    public void setWaitForAcknowledgement(boolean waitForAcknowledgement) {
        this.waitForAcknowledgement = waitForAcknowledgement;
    }

    /**
     * Set the timeout period (in milliseconds) for how long we wait for acknowledgement after publishing.
     *
     * @param acknowledgementTimeout
     */
    public void setAcknowledgementTimeout(long acknowledgementTimeout) {
        this.acknowledgementTimeout = acknowledgementTimeout;
    }

    public String getConnectionString() {
        return "rabbitmq://" + connection.getAddress().toString() + ":" + connection.getPort() + "/";
    }


}
