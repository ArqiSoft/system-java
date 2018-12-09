/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.npspot.jtransitlight;

import com.npspot.jtransitlight.contract.ContractType;
import com.npspot.jtransitlight.publisher.Bus;
import com.npspot.jtransitlight.publisher.IBusControl;
import com.npspot.jtransitlight.transport.JTransitLightTransportException;
import java.net.URISyntaxException;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Rami
 */
public class TestInMemorySending {
    private IBusControl bus;
	
	@Before
	public void setup() throws JTransitLightTransportException, URISyntaxException
	{
//		bus = Bus.Factory.createUsingRabbitMq("localhost", "guest", "guest");
		bus = Bus.Factory.createUsingInMemory(ContractType.TRANSACTION_TYPE);
                
	}
	
	@Test
	public void testBusConnection() {
		Assert.assertNotNull("Expect to have a bus available", bus);
	}
        
        @Test 
        public void testInMemorySending() throws Exception {
             for(int i=0; i<1000; i++) {
                    try {
                        
                        TestContract contract = new TestContract();
		contract.setName("Ketil");
		contract.setYearOfBirth(1960);
                contract.setMessageSequence((long)i);
                        
                   bus.publish(contract);
                    } catch (Exception ex) {
                        System.out.println("Message NOT sent! " + i);
                        Assert.fail("Exception " + ex);
                        ex.printStackTrace();
                    }
                   Thread.sleep(1);
                   if(i%100 == 0) {
                       List<byte[]> messages = bus.fetchInternalMemoryMessages("NPS.Contracts.Test:TestContract_2");
                       if(i!=0) {
                        Assert.assertEquals(100, messages.size());
                       }
                       
                   }
                }
		
            try {
                //
                bus.dispose();
            } catch (JTransitLightTransportException ex) {
                ex.printStackTrace();
            }
        }
}
