package com.npspot.jtransitlight.consumer.processor;

import com.npspot.jtransitlight.MessageProcessingException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by Denys on 04/11/2016.
 */
public interface RestExecutor {

    void executeSnapshotRequest() throws IOException, MessageProcessingException;

    byte[] getSnapshot() throws IOException;

    Long executeGetLastSeqId() throws IOException;
}
