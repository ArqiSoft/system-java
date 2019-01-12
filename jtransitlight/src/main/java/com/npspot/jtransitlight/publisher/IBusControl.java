package com.npspot.jtransitlight.publisher;

import com.npspot.jtransitlight.transport.JTransitLightTransportException;

public interface IBusControl extends IPublishEndpoint
{
	void dispose() throws JTransitLightTransportException;

    boolean isOpen();

	void useJsonSerializer();
	
	void setUseDurableExchanges(boolean useDurableExchanges) throws JTransitLightTransportException;
	
	void setWaitForAcknowledgement(boolean waitForAcknowledgement) throws JTransitLightTransportException;
	
	void setAcknowledgementTimeout(long acknowledgementTimeout) throws JTransitLightTransportException;
        
        
}
