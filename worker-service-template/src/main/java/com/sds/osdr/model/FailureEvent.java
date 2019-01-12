package com.sds.osdr.model;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FailureEvent extends AbstractContract {

    private UUID id;
    private String timeStamp;
    private UUID userId;

    public FailureEvent() {
        namespace = "Sds.Example.Domain.Events";
        contractName = FailureEvent.class.getSimpleName();
    }

    /**
     * @return the id
     */
    @JsonProperty("Id")
    public UUID getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(UUID id) {
        this.id = id;
    }

    /**
     * @return the timeStamp
     */
    public String getTimeStamp() {
        return timeStamp;
    }

    /**
     * @param timeStamp the timeStamp to set
     */
    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    /**
     * @return the userId
     */
    public UUID getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format(
                "FailureEvent [id=%s, timeStamp=%s, userId=%s, namespace=%s, contractName=%s, correlationId=%s]",
                id, timeStamp, userId, namespace, contractName, correlationId);
    }
    
    

}
