package com.npspot.jtransitlight.publisher;

import com.npspot.jtransitlight.transport.JTransitLightTransportException;

public interface IBusControl extends IPublishEndpoint
{
	/**
	 * Close RabbitMq connection.
	 * Note that this renders this IBusControl instance useless.
	 * @throws JTransitLightTransportException 
	 */
	void dispose() throws JTransitLightTransportException;

    boolean isOpen();

    /**
	 * Configure the system to use JSON serializing (which is the default).
	 */
	void useJsonSerializer();
	
	/**
	 * Set or reset the use durable exchanges property.
	 * 
	 * @param useDurableExchanges
	 * @throws JTransitLightTransportException 
	 */
	void setUseDurableExchanges(boolean useDurableExchanges) throws JTransitLightTransportException;
	
	/** Set or reset waiting for acknowledgement when publishing messages.
	 * @see https://www.rabbitmq.com/confirms.html
	 * 
	 * @param waitForAcknowledgement
	 * @throws JTransitLightTransportException 
	 */
	void setWaitForAcknowledgement(boolean waitForAcknowledgement) throws JTransitLightTransportException;
	
	/**
	 * Set the Publish message acknowledgement timeout in milliseconds.
	 * 
	 * @param acknowledgementTimeout	Timeout in milliseconds.
	 * @throws JTransitLightTransportException 
	 */
	void setAcknowledgementTimeout(long acknowledgementTimeout) throws JTransitLightTransportException;
        
        
}
