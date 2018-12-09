package com.npspot.jtransitlight.transport.rabbitmq;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;


public class rabbitMqConnectionTestI
{
	private static String exchangeName = "NPS.Contracts.Test:TestContract";
	private Consumer consumer;
	
	@Before
	public void setup() throws IOException, TimeoutException
	{
		registerConsumer();
	}
	
	@Test
	public void testRabbitMqConnection() throws IOException, TimeoutException
	{
		Channel channel = getRabbitMqChannel();
		Assert.assertNotNull(channel);
	}
	
	@Test
	public void testRabbitMqPublish() throws IOException, TimeoutException
	{
		Channel channel = getRabbitMqChannel();
		channel.confirmSelect();
		channel.exchangeDeclare(exchangeName, "fanout");
		channel.basicPublish(exchangeName, "", null, "Java test message.".getBytes());
		try
		{
			channel.waitForConfirms(10000);
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Assert.assertNotNull(channel);
	}


	private void registerConsumer() throws IOException, TimeoutException
	{
		Channel channel = getRabbitMqChannel();
	    channel.exchangeDeclare(exchangeName, "fanout");
	    String queueName = channel.queueDeclare(exchangeName, true, false, false, null).getQueue();
	    channel.queueBind(queueName, exchangeName, "");
	    consumer = new DefaultConsumer(channel) 
	    {
	    	@Override
	    	public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException 
	    	{
	    		String message = new String(body, "UTF-8");
	    		System.out.println(" [x] Received '" + message + "'");
	    	}
	    };
	    channel.basicConsume(queueName,  true, consumer);
	}
	
	private Channel getRabbitMqChannel() throws IOException, TimeoutException
	{
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		factory.setUsername("guest");
		factory.setPassword("guest");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();
		return channel;
	}

}
