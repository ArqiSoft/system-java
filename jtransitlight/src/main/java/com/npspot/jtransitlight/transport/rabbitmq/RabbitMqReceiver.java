/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.npspot.jtransitlight.transport.rabbitmq;

import com.npspot.jtransitlight.consumer.setting.ConsumerSettings;
import com.npspot.jtransitlight.transport.JTransitLightTransportException;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * @author Rami
 */
public class RabbitMqReceiver extends RabbitMq {

    public RabbitMqReceiver(URI uri, String host, String username, String password, int handshakeTimeoutMs) throws JTransitLightTransportException {
        super(uri, host, username, password, handshakeTimeoutMs);
    }

    public Channel getChannel() {
        return channel;
    }

    public Channel getErrorChannel() {
        return errorChannel;
    }

    public void declareAndBindQueue(String queueName, String exchange, ConsumerSettings settings) throws IOException {
        declareAndBindQueue(queueName, Collections.singletonList(exchange), settings);
    }

    public void declareAndBindQueue(String queueName, List<String> exchanges, ConsumerSettings settings) throws IOException {
        if (channel.isOpen()) {
            channel.queueDeclare(queueName, settings.isDurable(), settings.isExclusive(), settings.isAutoDelete(), this.args(settings));
            for (String exchangeName : exchanges) {
                declareFanoutAndErrorExchanges(exchangeName);
                channel.queueBind(queueName, exchangeName, "*");
            }
        }
    }

    private Map<String, Object> args(ConsumerSettings settings) {

        Map<String, Object> args = new HashMap<>();
        if (settings == null) {
            return args;
        }

        if (settings.getMessageTtl() != null) {
            args.put("x-message-ttl", settings.getMessageTtl());
        }
        if (settings.getQueueTtl() != null) {
            args.put("x-expires", settings.getQueueTtl());
        }
        return args;
    }

    public void declareFanoutAndErrorExchanges(String exchangeName) throws IOException {
        if (channel.isOpen()) {
            channel.exchangeDeclare(exchangeName, "fanout", useDurableExchanges);
            //declare the error exchange
            if (!exchangeName.endsWith("_error")) {
                channel.exchangeDeclare(exchangeName + "_error", "fanout", useDurableExchanges);
            }
        }
    }

    public void unbindQueue(String queueName, List<String> exchanges) throws IOException {
        if (channel.isOpen()) {
            for (String exchangeName : exchanges) {
                channel.queueUnbind(queueName, exchangeName, "*");
            }
            channel.queueDelete(queueName);
        }
    }

    public void publishError(String exchangeName, String messageId, byte[] message, Map<String, String> headerMap) throws JTransitLightTransportException {
        try {
            if (waitForAcknowledgement) {
                errorChannel.confirmSelect();
            }
            errorChannel.exchangeDeclare(exchangeName, "fanout", useDurableExchanges);
            Map<String, Object> headers = new HashMap<>();
            headers.put("Content-Type", "application/vnd.masstransit+json");
            headers.putAll(headerMap);
            AMQP.BasicProperties properties = new AMQP.BasicProperties().builder()
                    .contentType("application/vnd.masstransit+json")
                    .deliveryMode(2)
                    .priority(1)
                    .headers(headers)
                    .messageId(messageId)
                    .build();
            errorChannel.basicPublish(exchangeName + "_error", "", properties, message);
            if (waitForAcknowledgement) {
                errorChannel.waitForConfirms(acknowledgementTimeout);
            }
        } catch (IOException | TimeoutException | InterruptedException exception) {
            throw new JTransitLightTransportException("Unable to publish a message to RabbitMQ!", exception);
        }
    }
}
