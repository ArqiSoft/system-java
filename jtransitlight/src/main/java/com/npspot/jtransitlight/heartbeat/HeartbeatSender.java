package com.npspot.jtransitlight.heartbeat;

import java.util.concurrent.*;

import com.npspot.jtransitlight.publisher.IBusControl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Rami
*/
public class HeartbeatSender {
    private static volatile boolean started = false;
    
    private static final Logger LOGGER = LogManager.getLogger(HeartbeatSender.class);
    
    public static void startSender(IBusControl bus, ThreadFactory threadFactory, long interval) {
        LOGGER.info("start sender called with interval {} ms");
        if(!started && interval >= 1000) {
            HeartbeatSenderRunnable runnable = new HeartbeatSenderRunnable(bus, interval);
            threadFactory.newThread(runnable).start();
            started = true;
        }
    }

}
