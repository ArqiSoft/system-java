package com.sds.osdr.model;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SampleCommand extends AbstractContract {

    private UUID id;
    private UUID blobId;
    private String timeStamp;
    private String bucket;
    private UUID userId;


    public SampleCommand() {
        namespace = "Sds.Example.Domain.Commands";
        contractName = SampleCommand.class.getSimpleName();
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
    @JsonProperty("TimeStamp")
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
    @JsonProperty("UserId")
    public UUID getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    /**
     * @return the bucket
     */
    @JsonProperty("Bucket")
    public String getBucket() {
        return bucket;
    }

    /**
     * @param bucket the bucket to set
     */
    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    /**
     * @return the blobId
     */
    @JsonProperty("BlobId")
    public UUID getBlobId() {
        return blobId;
    }

    /**
     * @param blobId the blobId to set
     */
    public void setBlobId(UUID blobId) {
        this.blobId = blobId;
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

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format(
                "SampleCommand [id=%s, blobId=%s, timeStamp=%s, bucket=%s, userId=%s, namespace=%s, contractName=%s, correlationId=%s]",
                id, blobId, timeStamp, bucket, userId, namespace, contractName, correlationId);
    }


}
