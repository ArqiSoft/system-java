package com.sds.storage;

import java.io.InputStream;

public interface Blob {
    /**
     * Provide information about stored file @see BlobInfo
     * @return information about stored file
     */
    BlobInfo getInfo();
    
    /**
     * @return Stream with the file's data
     */
    InputStream getContentAsStream();

    /**
     * @return file's data
     */
    byte[] getContent();
    
    /**
     * Closes associated with the blob resources
     */
    void close();
}
