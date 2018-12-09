package com.sds.storage.gridfs;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoGridFSException;
import com.sds.storage.BlobInfo;
import com.sds.storage.Guid;

import junit.framework.TestCase;

public class GridFSBlobStorageTest extends TestCase {
    final static String OSDR_MONGO_DB_ENV_PARAMETER_NAME = "OSDR_MONGO_DB";

    private GridFSBlobStorage s = new GridFSBlobStorage(
            new MongoClient(new MongoClientURI(System.getenv(OSDR_MONGO_DB_ENV_PARAMETER_NAME))).getDatabase("test2"));

    @Test
    public void testGridFSAccess() {
        final String testString = "test content 1";
        final String fileName = "test";
        final String contentType = "text/text";
        final String bucketName = Guid.newGuid().toString();

        // updaload a file
        Guid id = s.addFile(fileName, testString.getBytes(), contentType, bucketName, null);

        // get updaloaded file info
        final BlobInfo fileInfo = s.getFileInfo(id, bucketName);
        // download file content
        String content = new String(s.downloadFile(id, bucketName));

        assertEquals(testString, content);
        assertEquals(fileName, fileInfo.getFileName());
        assertEquals(contentType, fileInfo.getContentType());
        assertEquals(testString.length(), fileInfo.getLength());
        assertEquals(id, fileInfo.getId());

        // delete file
        s.deleteFile(id, bucketName);

        // check the file does not exist
        try {
            s.downloadFile(id, bucketName);
            throw new RuntimeException("FIle not found exception was expected");
        } catch (MongoGridFSException e) {
            assertTrue(e.getMessage().startsWith("No file found"));
        }
    }

}
