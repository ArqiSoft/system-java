package com.npspot.jtransitlight.publisher.model;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

/**
 * The actual message which will be sent on the bus.
 * The pay-load contract is one of the properties inside this message.
 *
 * @param <T>	The type of the pay-load.
 */
public class BusMessage<T>
{
	/**
	 * Unique message id.
	 */
	@JsonProperty private UUID messageId;
	@JsonIgnore public UUID getMessageId(){return messageId;}
	@JsonIgnore public void setMessageId(UUID messageId){this.messageId = messageId;}
	
	/**
	 * Conversation id.
	 */
	@JsonProperty private UUID conversationId;
	@JsonIgnore public UUID getConversationId(){return conversationId;}
	@JsonIgnore public void setConversationId(UUID conversationId){this.conversationId = conversationId;}
        
        /**
	 * Headers
	 */
	@JsonProperty private Map<String,String> headers;
	@JsonIgnore public Map<String,String> getHeaders(){return headers;}
	@JsonIgnore public void setHeaders(Map<String,String> headers){this.headers = headers;}
        
        /**
	 * Host
	 */
	@JsonProperty private Map<String,String> host;
	@JsonIgnore public Map<String,String> getHost(){return host;}
	@JsonIgnore public void setHost(Map<String,String> host){this.host = host;}
        
	/**
	 * Message type.
	 * This is built up by the text "urn:message:" and then the exchange name.
	 */
	@JsonProperty private String[] messageType = new String[1];
	@JsonIgnore public String getMessageType(){return messageType[0];}
	@JsonIgnore public void setMessageType(String messageType){this.messageType[0] = messageType;}
        
        /**
	 * Target Address
	 */
	@JsonProperty private String destinationAddress;
	@JsonIgnore public String getTargetAddress(){return destinationAddress;}
	@JsonIgnore public void setTargetAddress(String destinationAddress){this.destinationAddress = destinationAddress;}
        
        /**
	 * Source Address
	 */
	@JsonProperty private String sourceAddress;
	@JsonIgnore public String getSourceAddress(){return sourceAddress;}
	@JsonIgnore public void setSourceAddress(String sourceAddress){this.sourceAddress = sourceAddress;}

	/**
	 * The pay-load / contract data.
	 */
	@JsonProperty private T message;
	@JsonIgnore public T getMessage(){return message;}
	@JsonIgnore public void setMessage(T message){this.message = message;}
}
