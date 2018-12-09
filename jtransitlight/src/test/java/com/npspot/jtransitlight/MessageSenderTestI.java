package com.npspot.jtransitlight;

import java.net.*;
import java.util.concurrent.Executors;

import com.npspot.jtransitlight.contract.ContractType;
import com.npspot.jtransitlight.publisher.Bus;
import com.npspot.jtransitlight.publisher.IBusControl;
import org.junit.*;

import com.npspot.jtransitlight.transport.JTransitLightTransportException;

public class MessageSenderTestI
{
	private IBusControl bus;
	
	@Before
	public void setup() throws JTransitLightTransportException, URISyntaxException
	{
//		bus = Bus.Factory.createUsingRabbitMq("localhost", "guest", "guest");
		bus = Bus.Factory.createUsingRabbitMq(new URI("amqp://messaging1:messaging1@192.168.20.2"), 30000,Executors.defaultThreadFactory(),5000, ContractType.TRANSACTION_TYPE);
                
	}
	
	@Test
	public void testBusConnection()
	{
		Assert.assertNotNull("Expect to have a bus available", bus);
	}
	
	@Test
	public void testPublish() throws InterruptedException
	{
            
		
		
            
//		AreaPriceDistributionTime contract = new AreaPriceDistributionTime();
//		
//		Calendar deliveryDate = GregorianCalendar.getInstance();
//		deliveryDate.add(Calendar.DAY_OF_MONTH, 1);
//		deliveryDate.set(Calendar.HOUR, 0);
//		deliveryDate.set(Calendar.MINUTE, 0);
//		deliveryDate.set(Calendar.SECOND, 0);
//		deliveryDate.set(Calendar.MILLISECOND, 0);
//		contract.setDeliveryDate(deliveryDate);
//
//		Calendar distributionTime = GregorianCalendar.getInstance();
//		distributionTime.set(Calendar.HOUR_OF_DAY, 12);
//		distributionTime.set(Calendar.MINUTE, 42);
//		distributionTime.set(Calendar.SECOND, 0);
//		distributionTime.set(Calendar.MILLISECOND, 0);
//		contract.setDistributionTime(distributionTime);
//				
            
                for(int i=0; i<1000000; i++) {
                    try {
                        
                        TestContract contract = new TestContract();
		contract.setName("Ketil");
		contract.setYearOfBirth(1960);
                contract.setMessageSequence((long)i);
                        
                   bus.publish(contract);
                   System.out.println("Message sent! " + i);
                    } catch (Exception ex) {
                        System.out.println("Message NOT sent! " + i);
                        ex.printStackTrace();
                    }
                   Thread.sleep(2000);
                }
		
            try {
                //
                bus.dispose();
            } catch (JTransitLightTransportException ex) {
                ex.printStackTrace();
            }
	}
}
