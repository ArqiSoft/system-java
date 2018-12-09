package com.npspot.jtransitlight;

public class MessageProcessingException extends Exception
{
	public MessageProcessingException(String message)
	{
		super(message);
	}
	public MessageProcessingException(Throwable throwable)
	{
		super(throwable);
	}
	public MessageProcessingException(String message, Throwable throwable)
	{
		super(message, throwable);
	}
}
