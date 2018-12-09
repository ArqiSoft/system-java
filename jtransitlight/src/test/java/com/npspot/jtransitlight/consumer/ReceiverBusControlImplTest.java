package com.npspot.jtransitlight.consumer;

import com.npspot.jtransitlight.JTransitLightException;
import com.npspot.jtransitlight.MessageProcessingException;
import com.npspot.jtransitlight.consumer.callback.SnapshotDeltaCallback;
import com.npspot.jtransitlight.consumer.callback.message.MessageCallback;
import com.npspot.jtransitlight.consumer.setting.ConsumerSettings;
import com.npspot.jtransitlight.transport.Connection;
import com.npspot.jtransitlight.transport.JTransitLightTransportException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.verify;


/**
 * Test class for {@link ReceiverBusControlImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ReceiverBusControlImplTest {

    private static final String EXCHANGE_NAME = "EXCHANGE_NAME";
    private static final String QUEUE_NAME = "QUEUE_NAME";
    private static final String REST_URL = "REST_URL";
    private static final Boolean IS_HANDLE_RE_SYNC = false;
    private static final Long START_TIME_UTC = 100L;
    private static final Long END_TIME_UTC = 101L;
    private static final List<String> EXCHANGE_NAMES = Arrays.asList(EXCHANGE_NAME, EXCHANGE_NAME);
    private static final ConsumerSettings CONSUMER_SETTINGS = new ConsumerSettings();

    @Mock
    private Connection connection;
    @InjectMocks
    private ReceiverBusControlImpl receiverBusControl;

    @Test
    public void subscribe() throws IOException, JTransitLightException {

        receiverBusControl.subscribe(EXCHANGE_NAMES, QUEUE_NAME, CONSUMER_SETTINGS, CALLBACK);
        verify(connection).subscribe(EXCHANGE_NAMES, QUEUE_NAME, CONSUMER_SETTINGS, CALLBACK);

        receiverBusControl.subscribe(EXCHANGE_NAME, QUEUE_NAME, CONSUMER_SETTINGS, CALLBACK);
        verify(connection).subscribe(Collections.singletonList(EXCHANGE_NAME), QUEUE_NAME, CONSUMER_SETTINGS, CALLBACK);
    }

    @Test
    public void subscribeRefData() throws MessageProcessingException, JTransitLightException, IOException {

        receiverBusControl.subscribeRefData(EXCHANGE_NAME, QUEUE_NAME, CONSUMER_SETTINGS, REST_URL, IS_HANDLE_RE_SYNC, SNAPSHOT_DELTA_CALLBACK);
        verify(connection).subscribeRefData(EXCHANGE_NAME, QUEUE_NAME, CONSUMER_SETTINGS, REST_URL, IS_HANDLE_RE_SYNC, SNAPSHOT_DELTA_CALLBACK);
    }

    @Test
    public void subscribeTrxData() throws MessageProcessingException, JTransitLightException, IOException {

        receiverBusControl.subscribeTrxData(EXCHANGE_NAME, QUEUE_NAME, CONSUMER_SETTINGS, REST_URL, START_TIME_UTC, END_TIME_UTC, IS_HANDLE_RE_SYNC, SNAPSHOT_DELTA_CALLBACK);
        verify(connection).subscribeTrxData(EXCHANGE_NAME, QUEUE_NAME, CONSUMER_SETTINGS, REST_URL, START_TIME_UTC, END_TIME_UTC, IS_HANDLE_RE_SYNC, SNAPSHOT_DELTA_CALLBACK);
    }

    @Test
    public void unSubscribe() throws IOException {

        receiverBusControl.unSubscribe(EXCHANGE_NAME, QUEUE_NAME);
        verify(connection).unsubscribe(Collections.singletonList(EXCHANGE_NAME), QUEUE_NAME);

        receiverBusControl.unSubscribe(EXCHANGE_NAMES, QUEUE_NAME);
        verify(connection).unsubscribe(EXCHANGE_NAMES, QUEUE_NAME);
    }

    @Test
    public void dispose() throws JTransitLightTransportException {

        receiverBusControl.dispose();
        verify(connection).dispose();
    }

    private static final MessageCallback CALLBACK = bytes -> {};
    private static final SnapshotDeltaCallback SNAPSHOT_DELTA_CALLBACK = new SnapshotDeltaCallback() {
        @Override
        public void onSnapshot(byte[] message) throws MessageProcessingException {
        }
        @Override
        public void onDelta(byte[] message) throws MessageProcessingException {
        }
    };
}