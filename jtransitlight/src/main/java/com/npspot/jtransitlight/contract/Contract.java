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

    Long getMessageSequence();

    void setMessageSequence(Long sequence);

    boolean isSnapshot();

    void setSnapshot(boolean isSnapshot);

    default SupplierSettings getSupplierSettings() {
        return new SupplierSettings();
    }
}