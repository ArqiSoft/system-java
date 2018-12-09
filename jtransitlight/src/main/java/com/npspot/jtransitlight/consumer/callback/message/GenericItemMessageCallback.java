package com.npspot.jtransitlight.consumer.callback.message;


public interface GenericItemMessageCallback<T> extends Acknowledged {

    void onMessage(T item);

}
