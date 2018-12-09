package com.npspot.jtransitlight.contract;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.npspot.jtransitlight.publisher.model.SupplierSettings;

public abstract class GenericContractItem implements Contract {

    @JsonIgnore
    private boolean snapshot;

    @JsonIgnore
    private Long messageSequence;

    @JsonIgnore
    private String namespace;

    @JsonIgnore
    private String contractName;

    @JsonIgnore
    private String queueName;

    @JsonIgnore
    private SupplierSettings supplierSettings;

    public GenericContractItem() {
    }

    @Override
    @JsonIgnore
    public boolean isSnapshot() {
        return snapshot;
    }

    @Override
    @JsonIgnore
    public void setSnapshot(boolean snapshot) {
        this.snapshot = snapshot;
    }

    @Override
    @JsonIgnore
    public Long getMessageSequence() {
        return messageSequence;
    }

    @Override
    @JsonIgnore
    public void setMessageSequence(Long messageSequence) {
        this.messageSequence = messageSequence;
    }

    @Override
    @JsonIgnore
    public String getNamespace() {
        return namespace;
    }

    @JsonIgnore
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    @Override
    @JsonIgnore
    public String getContractName() {
        return contractName;
    }

    @JsonIgnore
    public void setContractName(String contractName) {
        this.contractName = contractName;
    }

    @Override
    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    @Override
    @JsonIgnore
    public SupplierSettings getSupplierSettings() {
        return supplierSettings;
    }

    @JsonIgnore
    public void setSupplierSettings(SupplierSettings supplierSettings) {
        this.supplierSettings = supplierSettings;
    }

    @Override
    public String toString() {
        return "GenericContractItem{" +
                "snapshot=" + snapshot +
                ", messageSequence=" + messageSequence +
                ", namespace='" + namespace + '\'' +
                ", contractName='" + contractName + '\'' +
                ", queueName='" + queueName + '\'' +
                ", supplierSettings=" + supplierSettings +
                '}';
    }
}
