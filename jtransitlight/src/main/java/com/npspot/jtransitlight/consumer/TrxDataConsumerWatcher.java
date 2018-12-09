package com.npspot.jtransitlight.consumer;

import com.npspot.jtransitlight.consumer.callback.SnapshotDeltaCallback;
import com.npspot.jtransitlight.consumer.processor.TrxDataRestExecutor;
import com.npspot.jtransitlight.consumer.setting.ConsumerSettings;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

import static com.npspot.jtransitlight.utility.Utility.IS_SNAPSHOT_HEADER;
import static com.npspot.jtransitlight.utility.Utility.SEQUENCE_NUMBER_HEADER;
import org.apache.logging.log4j.LogManager;


public class TrxDataConsumerWatcher extends DefaultConsumer implements DataConsumerWatcher {

    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(TrxDataConsumerWatcher.class);

    private final AtomicLong lastSeq = new AtomicLong(-1);
    private final AtomicLong lastTimeStamp = new AtomicLong();
    private final TrxDataRestExecutor restExecutor;
    private final boolean isHandleReSync;
    private final ConsumerSettings settings;
    private final SnapshotDeltaCallback callback;

    public TrxDataConsumerWatcher(Channel channel, TrxDataRestExecutor restExecutor, boolean isHandleReSync, ConsumerSettings settings, SnapshotDeltaCallback callback) {
        super(channel);
        this.restExecutor = restExecutor;
        this.isHandleReSync = isHandleReSync;
        this.callback = callback;
        this.settings = settings;
        this.lastTimeStamp.set(System.currentTimeMillis());
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        try {
            if(isHandleReSync) {
                handleSeqNum((long) properties.getHeaders().get(SEQUENCE_NUMBER_HEADER));
            }
            if (properties.getHeaders().containsKey(IS_SNAPSHOT_HEADER) && (boolean) properties.getHeaders().get(IS_SNAPSHOT_HEADER)) {
                callback.onSnapshot(body);
            } else {
                callback.onDelta(body);
            }
        } catch (Exception ex) {
            LOGGER.error("Cannot handle received message: " + new String(body) + " with properties " + properties + ". Exception: " + ex);
        } finally {

            if (!settings.isAutoAck() && callback.isAcknowledged()) {

                LOGGER.trace("Message {} has been successfully acknowledged and released.", () -> new String(body));
                this.getChannel().basicAck(envelope.getDeliveryTag(), false);
            }
        }
    }

    private void handleSeqNum(long receivedSeqNum) throws Exception {
        //NOTE: in case if publisher reset seq
        if(receivedSeqNum == 0) {
            lastSeq.set(0L);
        } else {
            if (lastSeq.get() != -1 && (lastSeq.get() + 1) < receivedSeqNum) {
                restExecutor.executeSequenceRequest(lastSeq.get() + 1, receivedSeqNum - 1);
            }
            lastSeq.set(receivedSeqNum);
        }
        lastTimeStamp.set(System.currentTimeMillis());
    }

    @Override
    public Long getLastSeqNo() {
        return lastSeq.get();
    }

    @Override
    public Long getLastTimeStamp() {
        return lastTimeStamp.get();
    }

    @Override
    public void resetLastTimeStamp() {
        lastTimeStamp.set(System.currentTimeMillis());
    }

    @Override
    public void triggerReSync() {
        try {
            Long lastSeqIdRemote = restExecutor.executeGetLastSeqId();
            if (lastSeqIdRemote != null) {
                if (lastSeq.get() != -1 && lastSeq.get() < lastSeqIdRemote) {
                    restExecutor.executeSequenceRequest(lastSeq.get() + 1, lastSeqIdRemote);
                    lastSeq.set(lastSeqIdRemote);
                }
                lastTimeStamp.set(System.currentTimeMillis());
            } else {
                LOGGER.error("Cannot re-sync state. Last Seq ID from Remote Server is null");
            }
        } catch (Exception e) {
            LOGGER.error("Cannot re-sync state. Exception: " + e);
        }
    }
}