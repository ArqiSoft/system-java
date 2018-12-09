package com.npspot.jtransitlight;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.npspot.jtransitlight.contract.Contract;
import java.util.UUID;

public class TestContract implements Contract {

    @JsonProperty
    private Long messageSequence;

    @JsonProperty
    private boolean snapshot;

    @JsonIgnore
    final private static String namespace = "NPS.Contracts.Test";

    @JsonIgnore
    @Override
    public String getNamespace() {
        return namespace;
    }

    @JsonProperty
    private String name;

    @JsonIgnore
    public String getName() {
        return name;
    }

    @JsonIgnore
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty
    private int yearOfBirth;

    @JsonIgnore
    public int getYearOfBirth() {
        return yearOfBirth;
    }

    @JsonIgnore
    public void setYearOfBirth(int yearOfBirth) {
        this.yearOfBirth = yearOfBirth;
    }

    @Override
    public Long getMessageSequence() {
        return messageSequence;
    }

    public void setMessageSequence(Long messageSequence) {
        this.messageSequence = messageSequence;
    }

    @Override
    public boolean isSnapshot() {
        return snapshot;
    }

    @Override
    public void setSnapshot(boolean snapshot) {
        this.snapshot = snapshot;
    }

    @Override
    @JsonIgnore
    public String getContractName() {
        return "TestContract_2";
    }

    @Override
    public String getQueueName() {
        return "";
    }

    @Override
    public UUID getId() {
        return UUID.fromString("00000000-0000-0000-0000-000000000000");
    }
}
