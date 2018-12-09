package com.npspot.jtransitlight.publisher.model;

import java.util.HashMap;
import java.util.Map;

public class RabbitMQMessageContext {

    private byte[] message;

    private String exchange;

    private String messageId;

    private SupplierSettings supplierSettings;

    private Map<String, Object> headers = new HashMap<>();

    public byte[] getMessage() {
        return message;
    }

    public void setMessage(byte[] message) {
        this.message = message;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public void addHeader(String key, Object value) {
        headers.put(key, value);
    }

    public void addHeaders(Map<String, Object> values) {
        headers.putAll(values);
    }

    public Map<String, Object> getHeaders() {
        return headers;
    }

    public SupplierSettings getSupplierSettings() {
        return supplierSettings;
    }

    public void setSupplierSettings(SupplierSettings supplierSettings) {
        this.supplierSettings = supplierSettings;
    }
}
