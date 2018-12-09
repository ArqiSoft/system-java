package com.npspot.jtransitlight;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Message properties either received by an incoming message (not implemented) or
 * prepared for an outgoing message.
 *
 * @param <T> The type of message received / to send.
 */
public class MessageContext<T> {
    /**
     * Message unique id.
     */
    private UUID messageId;

    /**
     * Conversation id.
     */
    private UUID conversationId;

    private Map<String, Object> headers = new HashMap<>();

    public UUID getMessgeId() {
        return messageId;
    }

    public void setMessgeId(UUID messgeId) {
        this.messageId = messgeId;
    }

    public UUID getConversationId() {
        return conversationId;
    }

    public void setConversationId(UUID conversationId) {
        this.conversationId = conversationId;
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
}
