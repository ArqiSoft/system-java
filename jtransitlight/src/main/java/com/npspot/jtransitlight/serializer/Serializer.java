package com.npspot.jtransitlight.serializer;

import com.npspot.jtransitlight.publisher.model.BusMessage;
import com.npspot.jtransitlight.JTransitLightException;
import com.npspot.jtransitlight.contract.Contract;
import com.npspot.jtransitlight.serializer.json.JsonSerializer;

/**
 * Class used to encapsulate the actual serializer implementation.
 *
 */
public class Serializer
{
	/**
	 * Reference to the chosen serializer.
	 */
	private SerializerPlugin plugin = new JsonSerializer();
	
	/**
	 * Set up the serializer to use JSON.
	 */
	public void useJsonSerializer()
	{
		plugin = new JsonSerializer();
	}
	
	/**
	 * Use the configured serializer to serialize a message.
	 * 
	 * @param busMessage
	 * @return
	 * @throws JTransitLightException
	 */
	public String serialize(BusMessage<? extends Contract> busMessage) throws JTransitLightException
	{
		String serializedMessage = plugin.serialize(busMessage);
		return serializedMessage;
	}
	
}
