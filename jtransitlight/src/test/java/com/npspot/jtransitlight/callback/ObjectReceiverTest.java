package com.npspot.jtransitlight.callback;

import com.npspot.jtransitlight.MessageProcessingException;
import com.npspot.jtransitlight.consumer.callback.ObjectSnapshotDeltaCallback;
import com.npspot.jtransitlight.consumer.callback.GenericArraySnapshotDeltaCallback;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ObjectReceiverTest {

    private String snapshot = "[{\"Id\":1,\"Deleted\":false},{\"Id\":2,\"Deleted\":true}]";

    @Test
    public void parseSnapshot() throws MessageProcessingException {
        TestArraySnapshotDeltaCallback testCallback = new TestArraySnapshotDeltaCallback();
        ObjectSnapshotDeltaCallback<TestDTO> objectReceiverCallback = new ObjectSnapshotDeltaCallback<>(TestDTO.class, testCallback);
        objectReceiverCallback.onSnapshot(snapshot.getBytes());
        checkReceivedData(testCallback);
    }

    private void checkReceivedData(TestArraySnapshotDeltaCallback testCallback) {
        List<TestDTO> snapshot = testCallback.getSnapshot();
        Assert.assertEquals("Size", 2, snapshot.size());
        Assert.assertEquals(1, snapshot.get(0).getId());
        Assert.assertEquals(2, snapshot.get(1).getId());
        Assert.assertEquals(false, snapshot.get(0).isDeleted());
        Assert.assertEquals(true, snapshot.get(1).isDeleted());
    }

    private class TestArraySnapshotDeltaCallback implements GenericArraySnapshotDeltaCallback<TestDTO> {

        private List<TestDTO> snapshot = new ArrayList<>();

        @Override
        public void deltaReceived(List<TestDTO> delta) {
            snapshot.addAll(delta);
        }

        @Override
        public void snapshotReceived(List<TestDTO> snapshot) {
            this.snapshot.addAll(snapshot);
        }

        public List<TestDTO> getSnapshot() {
            return snapshot;
        }
    }
}