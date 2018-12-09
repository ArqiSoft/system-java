package com.npspot.jtransitlight.consumer.callback;

import java.util.List;

public interface GenericArraySnapshotDeltaCallback<T> {

    void deltaReceived(List<T> delta);

    void snapshotReceived(List<T> snapshot);
}
