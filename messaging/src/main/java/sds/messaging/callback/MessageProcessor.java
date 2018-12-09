package sds.messaging.callback;

public interface MessageProcessor<T> {
    void process(T message);
}
