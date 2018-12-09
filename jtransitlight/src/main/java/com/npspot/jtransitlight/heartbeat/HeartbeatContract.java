/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.npspot.jtransitlight.heartbeat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.npspot.jtransitlight.contract.Contract;
import java.util.UUID;

/**
 * @author Rami
 */
class HeartbeatContract implements Contract {

    private Long lastEventNo;

    private final Long timestamp;


    private final String namespace;

    public HeartbeatContract(Long messageSequence, String contractId, Long timestamp) {
        this.lastEventNo = messageSequence;
        this.namespace = contractId;
        this.timestamp = timestamp;
    }

    @Override
    public Long getMessageSequence() {
        return lastEventNo;
    }

    @Override
    public void setMessageSequence(Long sequence) {
        this.lastEventNo = sequence;
    }

    @Override
    public boolean isSnapshot() {
        return false;
    }

    @Override
    public void setSnapshot(boolean isSnapshot) {
    }

    @Override
    @JsonIgnore
    public String getNamespace() {
        return namespace;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    @Override
    @JsonIgnore
    public String getContractName() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        return "";
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
