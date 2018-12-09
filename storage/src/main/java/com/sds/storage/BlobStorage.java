package com.sds.storage;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public interface BlobStorage {
    public final String DEFAULT_CONTENT_TYPE = "application/octet-stream";
    
    /**
     * Add file into storage.
     * @param fileName File name
     * @param source Source stream
     * @param contentType MIME type
     * @param bucketName Bucket name where file stored
     * @param metadata Metadata assigned to saved file
     * @return GUID assigned to file in storage
     */
    Guid addFile(String fileName, InputStream source, String contentType, 
            String bucketName, Map<String, Object> metadata);

    /**
     * Add file into storage.
     * @param fileName File name
     * @param source File content
     * @param contentType MIME type
     * @param bucketName Bucket name where file is stored
     * @param metadata Metadata assigned to saved file
     * @return GUID assigned to file in storage
     */
    Guid addFile(String fileName, byte[] source, String contentType, 
            String bucketName, Map<String, Object> metadata);

    /**
     * Replaces existing file in storage
     * @param fileName File name
     * @param source Source stream
     * @param contentType MIME type
     * @param bucketName Bucket name where file stored
     * @param metadata Metadata assigned to saved file
     * @return GUID assigned to file in storage
     */
    void addFile(Guid id, String fileName, InputStream source, String contentType, 
            String bucketName, Map<String, Object> metadata);

    /**
     * Replaces existing file in storage
     * @param fileName File name
     * @param source File content
     * @param contentType MIME type
     * @param bucketName Bucket name where file is stored
     * @param metadata Metadata assigned to saved file
     * @return GUID assigned to file in storage
     */
    void addFile(Guid id, String fileName, byte[] source, String contentType, 
            String bucketName, Map<String, Object> metadata);
    
    /**
     * Delete file from storage
     * @param id File Id
     * @param bucketName Bucket name where file is stored
     */
    void deleteFile(Guid id, String bucketName);
    
    /**
     * Get file from storage
     * @param id File Id
     * @param bucketName Bucket name where file is stored
     * @return a stream containing blob data
     */
    InputStream getFileStream(Guid id, String bucketName);

    /**
     * Get file from storage
     * @param id File Id
     * @param bucketName Bucket name where file is stored
     * @return file data
     */
    byte[] downloadFile(Guid id, String bucketName);
    
    /**
     * Get file info if exist or null
     * @param id File Id
     * @param bucketName Bucket name where file is stored
     * @return
     */
    BlobInfo getFileInfo(Guid id, String bucketName);
    
    /**
    * Opens a Stream that can be used by the application to write data to a BlobStorage file.
    * @param id Blob id
    * @param fileName name for result file
    * @param contentType MIME type
    * @param bucketName Bucket name where file
    * @param metadata Metadata assigned to saved file
    * @return Stream for direct storaging data
    */
    OutputStream OpenUploadStream (Guid id, String fileName, String contentType, String bucketName, Map<String, Object> metadata);
}
