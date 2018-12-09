package com.sds.storage.gridfs;

import java.util.HashSet;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.sds.storage.BlobStorage;
import com.sds.storage.Guid;

public class GridFSPerformanceTest {

    private final static Logger LOGGER = LoggerFactory.getLogger(GridFSPerformanceTest.class);
    
    private final static int TIMEOUT = 1000; // msec
    private final static int DOCUMENT_SIZE = 100000; // bytes
    
    private byte[] DOCUMENT;
    private HashSet<Guid> ids;
    private MongoClient client;
    private GridFSBlobStorage storage;

    @Before
    public void setUp() throws Exception {
        Random rnd = new Random();
        DOCUMENT = new byte[DOCUMENT_SIZE];
        rnd.nextBytes(DOCUMENT);
        client = new MongoClient(new MongoClientURI(System.getenv(GridFSBlobStorageTest.OSDR_MONGO_DB_ENV_PARAMETER_NAME)));
        storage = new GridFSBlobStorage(client.getDatabase("test2"));
        ids = new HashSet<>();
    }

    @After
    public void tearDown() throws Exception {
        client.close();
    }

    @Test
    public void testPerformace() {
        String fileName = "perftestfile";
        String contentType = BlobStorage.DEFAULT_CONTENT_TYPE;
        String bucketName = Guid.newGuid().toString();
        
        long start = System.currentTimeMillis();
        
        
        // file upload
        while ((System.currentTimeMillis() - start) < TIMEOUT) {
            ids.add(storage.addFile(fileName, DOCUMENT, contentType, bucketName, null));
        }
        LOGGER.info("{} files uploaded in {} msec", ids.size(), (System.currentTimeMillis() - start));
        
        // file download
        start = System.currentTimeMillis();
        for (Guid id : ids) {
            storage.downloadFile(id, bucketName);
        }
        LOGGER.info("{} files downloaded in {} msec", ids.size(), (System.currentTimeMillis() - start));
        
        // file delete
        start = System.currentTimeMillis();
        for (Guid id : ids) {
            storage.deleteFile(id, bucketName);
        }
        LOGGER.info("{} files deleted in {} msec", ids.size(), (System.currentTimeMillis() - start));
        
    }

}
