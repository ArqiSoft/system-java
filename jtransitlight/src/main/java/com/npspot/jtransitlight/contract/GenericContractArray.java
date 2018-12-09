package com.npspot.jtransitlight.contract;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.npspot.jtransitlight.publisher.model.SupplierSettings;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GenericContractArray<T> extends ArrayList<T> implements Contract {

    @JsonIgnore
    private boolean snapshot;
    @JsonIgnore
    private Long sequence;
    @JsonIgnore
    private String namespace;
    @JsonIgnore
    private String contractName;
    @JsonIgnore
    private String queueName;
    @JsonIgnore
    private SupplierSettings supplierSettings;
    
    protected UUID Id;

    public GenericContractArray() {
    }

    private GenericContractArray(Builder<T> builder) {
        addAll(builder.items);
        setSnapshot(builder.snapshot);
        sequence = builder.sequence;
        namespace = builder.namespace;
        contractName = builder.contractName;
        queueName = builder.queueName;
        supplierSettings = builder.supplierSettings;
    }

    public static <T> Builder<T> newBuilder() {
        return new Builder<>();
    }

    @Override @JsonIgnore
    public String getNamespace() {
        return namespace;
    }

    @Override @JsonIgnore
    public String getContractName() {
        return contractName;
    }

    @Override
    public String getQueueName() {
        return queueName;
    }

    @JsonIgnore
    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    @Override
    @JsonIgnore
    public Long getMessageSequence() {
        return sequence;
    }

    @Override
    @JsonIgnore
    public void setMessageSequence(Long sequence) {
        this.sequence = sequence;
    }

    @Override @JsonIgnore
    public boolean isSnapshot() {
        return snapshot;
    }

    @Override @JsonIgnore
    public void setSnapshot(boolean isSnapshot) {
        this.snapshot = isSnapshot;
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
    public UUID getId()
    {
        return Id;
    }

    public static final class Builder<T> {
        private List<T> items;
        private boolean snapshot;
        private Long sequence;
        private String namespace;
        private String contractName;
        private String queueName;
        private SupplierSettings supplierSettings;

        private Builder() {
        }

        public Builder<T> withItems(List<T> val) {
            items = val;
            return this;
        }

        public Builder<T> withSnapshot(boolean val) {
            snapshot = val;
            return this;
        }

        public Builder<T> withSequence(Long val) {
            sequence = val;
            return this;
        }

        public Builder<T> withNamespace(String val) {
            namespace = val;
            return this;
        }

        public Builder<T> withContractName(String val) {
            contractName = val;
            return this;
        }

        public Builder<T> withQueueName(String val) {
            queueName= val;
            return this;
        }

        public Builder<T> withSupplierSettings(SupplierSettings val) {
            supplierSettings = val;
            return this;
        }

        public GenericContractArray<T> build() {
            return new GenericContractArray<>(this);
        }
    }

    @Override
    public String toString() {
        return "GenericContractArray{" +
                "snapshot=" + snapshot +
                ", sequence=" + sequence +
                ", namespace='" + namespace + '\'' +
                ", contractName='" + contractName + '\'' +
                ", queueName='" + queueName + '\'' +
                ", supplierSettings=" + supplierSettings +
                '}';
    }
}
