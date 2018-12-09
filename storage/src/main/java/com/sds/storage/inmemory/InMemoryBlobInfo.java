package com.sds.storage.inmemory;

import java.util.Date;
import java.util.Map;

import com.sds.storage.BlobInfo;
import com.sds.storage.Guid;

public class InMemoryBlobInfo implements BlobInfo {
    
    private Guid id;
    private String contentType;
    private String fileName;
    private String md5;
    private long length;
    private Date uploadDateTime;
    private Map<String, Object> metadata;
    
    public InMemoryBlobInfo(Guid id, String fileName, String contentType, long length, String md5,
            Date uploadDateTime, Map<String, Object> metadata) {
        super();
        this.id = id;
        this.fileName = fileName;
        this.contentType = contentType;
        this.length = length;
        this.md5 = md5;
        this.uploadDateTime = uploadDateTime;
        this.metadata = metadata;
    }

    public String getContentType() {
        return contentType;
    }

    public String getFileName() {
        return fileName;
    }

    public Guid getId() {
        return id;
    }

    public long getLength() {
        return length;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public String getMD5() {
        return md5;
    }

    public Date getUploadDateTime() {
        return uploadDateTime;
    }

}
