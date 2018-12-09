package com.sds.osdr.model;

import java.util.Date;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.npspot.jtransitlight.contract.Contract;

public abstract class AbstractContract implements Contract {
    String namespace;
    String contractName;
    
    private long messageSequence = new Date().getTime();
    private boolean isSnapshot;
    UUID correlationId;

    
    @Override
    @JsonIgnore
    public String getNamespace() {
        return namespace;
    }

    @Override
    @JsonIgnore
    public String getContractName() {
        return contractName;
    }

    @Override
    @JsonIgnore
    public String getQueueName() {
        return String.format("%s:%s", namespace, contractName);
    }

    @Override
    @JsonIgnore
    public Long getMessageSequence() {
        return messageSequence;
    }

    @Override
    public void setMessageSequence(Long sequence) {
        this.messageSequence = messageSequence;
    }

    @Override
    @JsonIgnore
    public boolean isSnapshot() {
        return isSnapshot;
    }

    @Override
    public void setSnapshot(boolean isSnapshot) {
        this.isSnapshot = isSnapshot;
    }
    
    /**
     * @return the correlationId
     */
    @JsonProperty("CorrelationId")
    public UUID getCorrelationId() {
        return correlationId;
    }

    /**
     * @param correlationId the correlationId to set
     */
    public void setCorrelationId(UUID correlationId) {
        this.correlationId = correlationId;
    }
}
