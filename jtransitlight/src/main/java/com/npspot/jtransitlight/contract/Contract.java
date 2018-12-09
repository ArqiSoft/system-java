package com.npspot.jtransitlight.contract;

import com.npspot.jtransitlight.publisher.model.SupplierSettings;
import java.util.UUID;

/**
 * Stuff that all objects (Contracts) that are published should contain.
 *
 * @author Rami
 */
public interface Contract {

    UUID getId();
    
    String getNamespace();

    String getContractName();

    String getQueueName();

    /**
     * All messages must have sequence numbers, per contract, receiving end may use this to handle out-of-order messages.
     * You can use ContractMapping class to keep your sequences in memory.
     * If you don't care about order of messages and send small amounts then you could
     * hardcode MessageSequence to 1 - it will work though
     */
    Long getMessageSequence();

    void setMessageSequence(Long sequence);

    boolean isSnapshot();

    void setSnapshot(boolean isSnapshot);

    default SupplierSettings getSupplierSettings() {
        return new SupplierSettings();
    }
}