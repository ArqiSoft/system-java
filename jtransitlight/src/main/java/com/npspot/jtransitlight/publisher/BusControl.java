package com.npspot.jtransitlight.publisher;

import com.npspot.jtransitlight.publisher.sequence.SequenceProvider;
import com.npspot.jtransitlight.serializer.Serializer;
import com.npspot.jtransitlight.transport.*;

public class BusControl extends PublishEndpoint implements IBusControl
{
	/**
	 * A connection linking to the transport used.
	 */
	private Connection connection;
	
	/**
	 * A serializer used to transform object to the format used in the messages in the transport layer.
	 */
	private Serializer serializer;
	

	BusControl(Connection connection, SequenceProvider sequenceProvider)
	{
		super(sequenceProvider);
		this.connection = connection;
		this.serializer = new Serializer();
	}

	@Override
	public void dispose() throws JTransitLightTransportException
	{
		connection.dispose();
	}

	@Override
	public boolean isOpen() {
		return connection.isOpen();
	}
	
	@Override
	public void useJsonSerializer()
	{
		serializer.useJsonSerializer();
	}

	@Override
	public void setUseDurableExchanges(boolean useDurableExchanges) throws JTransitLightTransportException
	{
		connection.setUseDurableExchanges(useDurableExchanges);
	}

	@Override
	public void setWaitForAcknowledgement(boolean waitForAcknowledgement) throws JTransitLightTransportException
	{
		connection.setWaitForAcknowledgement(waitForAcknowledgement);
	}

	@Override
	public void setAcknowledgementTimeout(long acknowledgementTimeout) throws JTransitLightTransportException
	{
		connection.setAcknowledgementTimeout(acknowledgementTimeout);
	}

	@Override
	Connection getConnection()
	{
		return connection;
	}

	@Override
	Serializer getSerializer()
	{
		return serializer;
	}
}
