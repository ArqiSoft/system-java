package com.sds.storage.inmemory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.sds.storage.Blob;
import com.sds.storage.BlobInfo;

public class InMemoryBlob implements Blob {

    private BlobInfo info;
    private byte[] content;
    
    public InMemoryBlob(BlobInfo info, byte[] content) {
        super();
        this.info = info;
        this.content = content;
    }

    public BlobInfo getInfo() {
        return info;
    }

    public InputStream getContentAsStream() {
        return new ByteArrayInputStream(content);
    }

    public byte[] getContent() {
        return content;
    }

    public void close() {
    }

}
