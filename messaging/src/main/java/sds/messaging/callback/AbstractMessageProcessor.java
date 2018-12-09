package sds.messaging.callback;

import com.npspot.jtransitlight.consumer.callback.message.MessageCallback;
import com.npspot.jtransitlight.contract.Contract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractMessageProcessor<T extends Contract> implements MessageProcessor<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageCallback.class);

    @Override
    public abstract void process(T message);
    
    public void doProcess(T message) {
        LOGGER.info("[Consume] Message {}.{} with Id: {} is about to be consumed. Message: {}}", ((Contract)message).getNamespace(), ((Contract)message).getContractName(), ((Contract)message).getId(), (Contract)message);
        try {
            process(message);
        } catch (Exception e) {
            
            LOGGER.error("[Consume] Message {}.{} with Id: {} consumed with error. Message: {}. Exception: {}", ((Contract)message).getNamespace(), ((Contract)message).getContractName(), ((Contract)message).getId(), message, e);
        }
        LOGGER.info("[Consume] Message {}.{} with Id: {} successfully consumed", ((Contract)message).getNamespace(), ((Contract)message).getContractName(), ((Contract)message).getId());
    }
}
