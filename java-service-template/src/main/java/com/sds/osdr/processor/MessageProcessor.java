package com.sds.osdr.processor;

public interface MessageProcessor<T> {
    void process(T message);
}
