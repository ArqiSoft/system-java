package com.sds.storage.inmemory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.sds.storage.Blob;
import com.sds.storage.BlobInfo;
import com.sds.storage.BlobStorage;
import com.sds.storage.Guid;
import java.io.OutputStream;

public class InMemoryBlobStorage implements BlobStorage {
    private ConcurrentHashMap<String, ConcurrentHashMap<Guid, Blob>> storage = 
            new ConcurrentHashMap<String, ConcurrentHashMap<Guid, Blob>>();
    
    /*
     * (non-Javadoc)
     * @see org.sds.storage.BlobStorage#addFile(java.lang.String, java.io.InputStream, java.lang.String, java.lang.String, java.util.Map)
     */
    public Guid addFile(String fileName, InputStream source, String contentType, 
            String bucketName, Map<String, Object> metadata) {
        Guid id = Guid.newGuid();
        addFile(id, fileName, source, contentType, bucketName, metadata);
        return id;
    }

    /*
     * (non-Javadoc)
     * @see org.sds.storage.BlobStorage#addFile(java.lang.String, byte[], java.lang.String, java.lang.String, java.util.Map)
     */
    public Guid addFile(String fileName, byte[] source, String contentType, 
            String bucketName, Map<String, Object> metadata) {
        Guid id = Guid.newGuid();
        addFile(id, fileName, source, contentType, bucketName, metadata);
        return id;
    }

    /*
     * (non-Javadoc)
     * @see org.sds.storage.BlobStorage#updateFile(java.util.Guid, java.lang.String, java.io.InputStream, java.lang.String, java.lang.String, java.util.Map)
     */
    public void addFile(Guid id, String fileName, InputStream source, 
            String contentType, String bucketName, Map<String, Object> metadata) {
        byte[] content = readStream(source);
        addFile(id, fileName, content, contentType, bucketName, metadata);
    }

    /*
     * (non-Javadoc)
     * @see org.sds.storage.BlobStorage#updateFile(java.util.Guid, java.lang.String, byte[], java.lang.String, java.lang.String, java.util.Map)
     */
    public void addFile(Guid id, String fileName, byte[] source, String contentType, String bucketName,
            Map<String, Object> metadata) {
        Blob blob = new InMemoryBlob(new InMemoryBlobInfo(id, fileName, 
                contentType, source.length, DEFAULT_CONTENT_TYPE, new Date(), metadata), 
                source);
        
        String bucket = bucketName == null ? "" : bucketName;
        storage.getOrDefault(bucket, new ConcurrentHashMap<Guid, Blob>()).put(id, blob);
    }

    /*
     * (non-Javadoc)
     * @see org.sds.storage.BlobStorage#deleteFile(java.util.Guid, java.lang.String)
     */
    public void deleteFile(Guid id, String bucketName) {
        storage.get(bucketName == null ? "" : bucketName).remove(id);
    }

    /*
     * (non-Javadoc)
     * @see org.sds.storage.BlobStorage#getFileStream(java.util.Guid, java.lang.String)
     */
    public InputStream getFileStream(Guid id, String bucketName) {
        return storage.get(bucketName == null ? "" : bucketName).get(id).getContentAsStream();
    }

    /*
     * (non-Javadoc)
     * @see org.sds.storage.BlobStorage#downloadFile(java.util.Guid, java.lang.String)
     */
    public byte[] downloadFile(Guid id, String bucketName) {
        return storage.get(bucketName == null ? "" : bucketName).get(id).getContent();
    }

    /*
     * (non-Javadoc)
     * @see org.sds.storage.BlobStorage#getFileInfo(java.util.Guid, java.lang.String)
     */
    public BlobInfo getFileInfo(Guid id, String bucketName) {
        return storage.get(bucketName == null ? "" : bucketName).get(id).getInfo();
    }
    
    private byte[] readStream(InputStream stream) {
        byte[] content = null;
        try {
            content = new byte[stream.available()];
            stream.read(content);
            stream.close();
        } catch (IOException e) {
            throw new IllegalArgumentException("stream");
        }
        
        return content;
    }

    @Override
    public OutputStream OpenUploadStream(Guid id, String fileName, String contentType, String bucketName, Map<String, Object> metadata) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
