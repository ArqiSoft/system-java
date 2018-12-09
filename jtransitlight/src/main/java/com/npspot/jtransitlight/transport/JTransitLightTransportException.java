package com.npspot.jtransitlight.transport;

import com.npspot.jtransitlight.JTransitLightException;

public class JTransitLightTransportException extends JTransitLightException
{
	public JTransitLightTransportException(String message)
	{
		super(message);
	}
	public JTransitLightTransportException(Throwable throwable)
	{
		super(throwable);
	}
	public JTransitLightTransportException(String message, Throwable throwable)
	{
		super(message, throwable);
	}
}
