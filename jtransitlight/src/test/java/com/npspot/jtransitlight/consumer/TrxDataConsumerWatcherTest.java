package com.npspot.jtransitlight.consumer;

import com.npspot.jtransitlight.MessageProcessingException;
import com.npspot.jtransitlight.consumer.callback.SnapshotDeltaCallback;
import com.npspot.jtransitlight.consumer.processor.TrxDataRestExecutor;
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
 * Created by Denys
 */
public class TrxDataConsumerWatcherTest {

    private final TrxDataRestExecutor restExecutor = mock(TrxDataRestExecutor.class);

    private ConsumerSettings settings;

    @Before
    public void setup() {
        settings = new ConsumerSettings();
    }

    @Test
    public void testDeltaMessageHandling() throws IOException {
        final String tradeDelta = createTradeDelta(1);
        final SnapshotDeltaCallback callback = createReceiverCallback(tradeDelta);

        final TrxDataConsumerWatcher trxDataConsumerWatcher = new TrxDataConsumerWatcher(null, restExecutor, true, settings, callback);
        trxDataConsumerWatcher.handleDelivery(null, createEnvelope(), createAMQPProperties(0L), tradeDelta.getBytes());
    }

    @Test
    public void testDeltaMessageHandlingWithNewSeq() throws IOException {
        final String tradeDelta = createTradeDelta(1);
        final SnapshotDeltaCallback callback = createReceiverCallback(tradeDelta);

        final TrxDataConsumerWatcher trxDataConsumerWatcher = new TrxDataConsumerWatcher(null, restExecutor, true, settings, callback);
        trxDataConsumerWatcher.handleDelivery(null, createEnvelope(), createAMQPProperties(3L), tradeDelta.getBytes());
        Assert.assertEquals(Long.valueOf(3), trxDataConsumerWatcher.getLastSeqNo());
    }

    @Test
    public void testDeltaMessageHandlingWithSeqGap() throws IOException {
        final String tradeDeltaOne = createTradeDelta(1);
        final String tradeDeltaTwo = createTradeDelta(1);
        SnapshotDeltaCallback callback = createReceiverCallback(tradeDeltaOne);

        final TrxDataConsumerWatcher trxDataConsumerWatcher = new TrxDataConsumerWatcher(null, restExecutor, true, settings, callback);
        trxDataConsumerWatcher.handleDelivery(null, createEnvelope(), createAMQPProperties(3L), tradeDeltaOne.getBytes());
        trxDataConsumerWatcher.handleDelivery(null, createEnvelope(), createAMQPProperties(5L), tradeDeltaTwo.getBytes());

        try {
            verify(restExecutor, times(1)).executeSequenceRequest(4, 4);
        } catch (Exception e) {
            fail("should not happen");
        }

        Assert.assertEquals(Long.valueOf(5), trxDataConsumerWatcher.getLastSeqNo());
    }

    @Test
    public void testTriggerReSync() throws IOException {
        final String tradeDeltaOne = createTradeDelta(1);
        SnapshotDeltaCallback callback = createReceiverCallback(tradeDeltaOne);

        final TrxDataConsumerWatcher trxDataConsumerWatcher = new TrxDataConsumerWatcher(null, restExecutor, true, settings, callback);
        trxDataConsumerWatcher.handleDelivery(null, createEnvelope(), createAMQPProperties(3L), tradeDeltaOne.getBytes());

        try {
            when(restExecutor.executeGetLastSeqId()).thenReturn(4L);
        } catch (IOException e) {
            fail("should not happen");
        }

        trxDataConsumerWatcher.triggerReSync();
        try {
            verify(restExecutor, times(1)).executeSequenceRequest(4, 4);
        } catch (Exception e) {
            fail("should not happen");
        }
        Assert.assertEquals(Long.valueOf(4), trxDataConsumerWatcher.getLastSeqNo());
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

    private String createTradeDelta(long tradeId) {
        return  "{\n" +
                "  \"SellOrderId\":2,\n" +
                "  \"BuyOrderId\":1,\n" +
                "  \"ContractId\":1342,\n" +
                "  \"Quantity\":1000,\n" +
                "  \"Price\":5,\n" +
                "  \"TradeTime\":\"2016-04-28T10:34:20.001+02:00\",\n" +
                "  \"TradeId\":" + tradeId + ",\n" +
                "  \"TradeStatus\":\"COMPLETED\",\n" +
                "  \"buyAreaId\":\"FI\",\n" +
                "  \"sellAreaId\":\"SE1\",\n" +
                "}";
    }

    private Envelope createEnvelope() {
        return new Envelope(1, false, "exchange", "routingKey");
    }

    private AMQP.BasicProperties createAMQPProperties(long seqNo) {
        Map<String, Object> headerProperties = new HashMap<>();
        headerProperties.put(Utility.SEQUENCE_NUMBER_HEADER, seqNo);
        return new AMQP.BasicProperties().builder().headers(headerProperties).build();
    }
}
