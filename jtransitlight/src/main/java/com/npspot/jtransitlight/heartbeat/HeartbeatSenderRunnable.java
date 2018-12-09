/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.npspot.jtransitlight.heartbeat;

import com.npspot.jtransitlight.publisher.IBusControl;
import com.npspot.jtransitlight.contract.ContractMapping;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 *
 * @author Rami
 */
public class HeartbeatSenderRunnable implements Runnable {
    
    private final IBusControl bus;
    private final long interval;
    private final Logger LOGGER = LogManager.getLogger(HeartbeatSenderRunnable.class);
    
    public HeartbeatSenderRunnable(IBusControl bus, long intervalMillis) {
        this.bus = bus;
        this.interval = intervalMillis;
    }

    @Override
    public void run() {
         LOGGER.info("Heartbeat sender starting with interval {} ms",interval);
        
        while(true) {
           Set<String> activeContracts = ContractMapping.getActiveContracts();
           if(!activeContracts.isEmpty()) {
               for(String contractId: activeContracts) {
                   try {
                       Long lastSequence = ContractMapping.getCurrentSequence(contractId);
                       HeartbeatContract hb = new HeartbeatContract(lastSequence, contractId, System.currentTimeMillis());
                       bus.publishHeartbeat(hb);
                   } catch (Exception ex) {
                       LOGGER.error("Unable to send heartbeat! for {} due to \n{}",contractId, ex);
                   }
               }
           }
            try {
                Thread.sleep(interval);
            } catch (InterruptedException ex) {
                LOGGER.error("interrupted",ex);
            }
        }
    }
}
