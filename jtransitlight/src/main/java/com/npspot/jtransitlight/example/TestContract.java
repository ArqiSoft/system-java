/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.npspot.jtransitlight.example;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.npspot.jtransitlight.contract.Contract;
import java.util.UUID;

/**
 *
 * @author Rami
 */
public class TestContract implements Contract{
    
    private Long messageSequence;
    
    @JsonProperty
    private String testProperty;

    @JsonProperty
    private boolean snapshot;

    @Override
    public String getNamespace() {
        return "com.nps.test.connection.errors";
    }

    @Override
    public String getContractName() {
        return "TestContract";
    }

    @Override
    public String getQueueName() {
        return "";
    }

    @Override
    public Long getMessageSequence() {
        return messageSequence;
    }

    public void setMessageSequence(Long messageSequence) {
        this.messageSequence = messageSequence;
    }

    public String getTestProperty() {
        return testProperty;
    }

    public void setTestProperty(String testProperty) {
        this.testProperty = testProperty;
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
    public UUID getId() {
        return UUID.fromString("00000000-0000-0000-0000-000000000000");
    }
}
