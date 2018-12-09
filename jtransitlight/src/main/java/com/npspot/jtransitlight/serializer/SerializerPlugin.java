package com.npspot.jtransitlight.serializer;

import com.npspot.jtransitlight.publisher.model.BusMessage;
import com.npspot.jtransitlight.JTransitLightException;
import com.npspot.jtransitlight.contract.Contract;

/**
 * Interface which all serializers must implement.
 */
public interface SerializerPlugin
{
	/**
	 * Serialize message.
	 * 
	 * @param busMessage
	 * @return
	 * @throws JTransitLightException
	 */
	String serialize(BusMessage<? extends Contract> busMessage) throws JTransitLightException;
}
