package com.npspot.jtransitlight.consumer;

import com.npspot.jtransitlight.MessageProcessingException;
import com.npspot.jtransitlight.consumer.callback.SnapshotDeltaCallback;
import com.npspot.jtransitlight.consumer.processor.RefDataRestExecutor;
import com.npspot.jtransitlight.consumer.setting.ConsumerSettings;
import com.npspot.jtransitlight.utility.Utility;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Envelope;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Denys on 07/11/2016.
 */
public class RefDataConsumerWatcherTest {

    private final RefDataRestExecutor restExecutor = mock(RefDataRestExecutor.class);
    private ConsumerSettings settings;

    @Before
    public void setup() {
        settings = new ConsumerSettings();
    }

    @Test
    public void testDeltaMessageHandling() throws IOException {
        final String deliveryAreaDelta = createDeliveryAreaDelta("FI");
        final SnapshotDeltaCallback callback = createReceiverCallback(deliveryAreaDelta);

        final RefDataConsumerWatcher refDataConsumerWatcher = new RefDataConsumerWatcher(null, restExecutor, true, settings, callback);
        refDataConsumerWatcher.handleDelivery(null, createEnvelope(), createAMQPProperties(0L), deliveryAreaDelta.getBytes());
    }

    @Test
    public void testDeltaMessageHandlingWithNewSeq() throws IOException {
        final String deliveryAreaDelta = createDeliveryAreaDelta("FI");
        final SnapshotDeltaCallback callback = createReceiverCallback(deliveryAreaDelta);

        final RefDataConsumerWatcher refDataConsumerWatcher = new RefDataConsumerWatcher(null, restExecutor, true, settings, callback);
        refDataConsumerWatcher.handleDelivery(null, createEnvelope(), createAMQPProperties(3L), deliveryAreaDelta.getBytes());
        Assert.assertEquals(Long.valueOf(3), refDataConsumerWatcher.getLastSeqNo());
    }

    @Test
    public void testDeltaMessageHandlingWithSeqGap() throws IOException {
        final String deliveryAreaDeltaFI = createDeliveryAreaDelta("FI");
        final String deliveryAreaDeltaNO = createDeliveryAreaDelta("NO");

        SnapshotDeltaCallback snapshotDeltaCallback = createReceiverCallback(deliveryAreaDeltaFI);

        final RefDataConsumerWatcher refDataConsumerWatcher = new RefDataConsumerWatcher(null, restExecutor, true, settings, snapshotDeltaCallback);

        refDataConsumerWatcher.handleDelivery(null, createEnvelope(), createAMQPProperties(3L), deliveryAreaDeltaFI.getBytes());
        refDataConsumerWatcher.handleDelivery(null, createEnvelope(), createAMQPProperties(5L), deliveryAreaDeltaNO.getBytes());

        try {
            verify(restExecutor, times(1)).executeSnapshotRequest();
        } catch (IOException | MessageProcessingException e) {
            fail("should not happen");
        }

        Assert.assertEquals(Long.valueOf(5), refDataConsumerWatcher.getLastSeqNo());
    }

    @Test
    public void testTriggerReSync() throws IOException {
        final String deliveryAreaDeltaFI = createDeliveryAreaDelta("FI");
        SnapshotDeltaCallback snapshotDeltaCallback = createReceiverCallback(deliveryAreaDeltaFI);
        final RefDataConsumerWatcher refDataConsumerWatcher = new RefDataConsumerWatcher(null, restExecutor, true, settings, snapshotDeltaCallback);
        refDataConsumerWatcher.handleDelivery(null, createEnvelope(), createAMQPProperties(2L), deliveryAreaDeltaFI.getBytes());

        try {
            when(restExecutor.executeGetLastSeqId()).thenReturn(4L);
            refDataConsumerWatcher.triggerReSync();

            verify(restExecutor, times(1)).executeSnapshotRequest();
        } catch (IOException | MessageProcessingException e) {
            fail("should not happen");
        }
    }

    private Envelope createEnvelope() {
        return new Envelope(1, false, "exchange", "routingKey");
    }

    private AMQP.BasicProperties createAMQPProperties(long seqNo) {
        Map<String, Object> headerProperties = new HashMap<>();
        headerProperties.put(Utility.SEQUENCE_NUMBER_HEADER, seqNo);
        return new AMQP.BasicProperties().builder().headers(headerProperties).build();
    }

    private SnapshotDeltaCallback createReceiverCallback(final String deliveryAreaDelta) {
        return new SnapshotDeltaCallback() {
            @Override
            public void onSnapshot(byte[] message) throws MessageProcessingException {
                fail("This method should not be called");
            }

            @Override
            public void onDelta(byte[] message) throws MessageProcessingException {
                Assert.assertEquals(new String(message), deliveryAreaDelta);
            }
        };
    }

    private String createDeliveryAreaDelta(String deliveryArea) {
        return  "{\n" +
                " \"dlvryAreaId:\" \"" + deliveryArea + "\",\n" +
                " \"revisionNo\" : 1,\n" +
                " \"name\" : \"" + deliveryArea + "\",\n" +
                " \"longName\" : \"long name for the delivery area\",\n" +
                " \"state\" : \"ACTI\",\n" +
                " \"prodName\" : [ ],\n" +
                " \"entryCreatedDate\" : 2016-04-27T17:33:09.701+0000,\n" +
                " \"mktAreaId\": \"" + deliveryArea + "\"\n" +
                " }";
    }
}
