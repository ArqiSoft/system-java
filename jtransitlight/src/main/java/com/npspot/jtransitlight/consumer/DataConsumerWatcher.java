package com.npspot.jtransitlight.consumer;


public interface DataConsumerWatcher {

    Long getLastSeqNo();

    Long getLastTimeStamp();

    void resetLastTimeStamp();

    void triggerReSync();
}
