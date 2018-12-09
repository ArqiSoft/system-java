package com.sds.storage.gridfs;

import java.util.Date;
import java.util.Map;

import com.mongodb.client.gridfs.model.GridFSFile;
import com.sds.storage.BlobInfo;
import com.sds.storage.Guid;

public class GridFSBlobInfo implements BlobInfo {
    public final static String CONTENT_TYPE_KEY = "ContentType";

    
    private GridFSFile fileInfo;

    public GridFSBlobInfo(GridFSFile fileInfo) {
        this.fileInfo = fileInfo;
    }

    public String getContentType() {
        if (fileInfo != null && fileInfo.getMetadata() != null && 
                fileInfo.getMetadata().containsKey(CONTENT_TYPE_KEY)) {
            return fileInfo.getMetadata().getString(CONTENT_TYPE_KEY);
        }
        
        return null;
    }

    public String getFileName() {
        return fileInfo.getFilename();
    }

    public Guid getId() {
        return Guid.fromByteArray(fileInfo.getId().asBinary().getData());
    }

    public long getLength() {
        return fileInfo.getLength();
    }

    public Map<String, Object> getMetadata() {
        return fileInfo.getMetadata();
    }

    public String getMD5() {
        return fileInfo.getMD5();
    }

    public Date getUploadDateTime() {
        return fileInfo.getUploadDate();
    }

}
