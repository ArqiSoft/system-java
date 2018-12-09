package com.sds.storage;

import java.util.Date;
import java.util.Map;

public interface BlobInfo {
    /**
     * @return the type of the content
     */
    String getContentType();
    
    /**
     * @return the file name
     */
    String getFileName();
    
    /**
     * @return the identifier
     */
    Guid getId();
    
    /**
     * @return the blob length
     */
    long getLength();
    
    /**
     * @return the metadata
     */
    Map<String, Object> getMetadata();
    
    /**
     * @return the MD5 checksum
     */
    String getMD5();
    
    /**
     * @return the upload date time
     */
    Date getUploadDateTime();
}
