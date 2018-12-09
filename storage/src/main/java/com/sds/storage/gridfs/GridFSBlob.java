package com.sds.storage.gridfs;

import java.io.InputStream;

import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.sds.storage.Blob;
import com.sds.storage.BlobInfo;

public class GridFSBlob implements Blob {
    
    private GridFSBlobInfo info;
    private GridFSDownloadStream stream;

    
    public GridFSBlob(GridFSDownloadStream stream) {
        super();
        this.info = new GridFSBlobInfo(stream.getGridFSFile());
        this.stream = stream;
    }

    public BlobInfo getInfo() {
        return info;
    }

    public InputStream getContentAsStream() {
        return stream;
    }

    public byte[] getContent() {
        byte[] content = new byte[stream.available()];
        stream.read(content);
        stream.reset();
        return content;
    }
    
    public void close() {
        stream.close();
    }

    @Override
    public void finalize() {
        close();
    }

}
