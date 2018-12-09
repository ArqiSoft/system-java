package com.npspot.jtransitlight.consumer.callback.message;

import java.util.List;

public interface GenericArrayMessageCallback<T> extends Acknowledged {

    void onMessage(List<T> list);

}
