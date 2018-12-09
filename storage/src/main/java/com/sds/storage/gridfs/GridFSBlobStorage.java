package com.sds.storage.gridfs;

import static com.mongodb.client.model.Filters.eq;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.bson.BsonBinary;
import org.bson.BsonBinarySubType;
import org.bson.BsonValue;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import com.sds.storage.BlobInfo;
import com.sds.storage.BlobStorage;
import com.sds.storage.Guid;
import java.io.OutputStream;

public class GridFSBlobStorage implements BlobStorage {

    private final static Logger LOGGER = LoggerFactory.getLogger(GridFSBlobStorage.class);

    private final static BsonBinarySubType BSON_BINARY_SUBTYPE = BsonBinarySubType.UUID_LEGACY;

    private final MongoDatabase db;

    public GridFSBlobStorage(MongoDatabase db) {
        super();
        this.db = db;
    }

    /*
     * (non-Javadoc)
     * @see org.sds.storage.BlobStorage#addFile(java.lang.String, java.io.InputStream, java.lang.String, java.lang.String, java.util.Map)
     */
    public Guid addFile(String fileName, InputStream source, String contentType, String bucketName,
            Map<String, Object> metadata) {

        Guid uid = Guid.newGuid();
        addFile(uid, fileName, source, contentType, bucketName, metadata);
        return uid;
    }

    /*
     * (non-Javadoc)
     * @see org.sds.storage.BlobStorage#addFile(java.lang.String, byte[], java.lang.String, java.lang.String, java.util.Map)
     */
    public Guid addFile(String fileName, byte[] source, String contentType, String bucketName,
            Map<String, Object> metadata) {
        Guid id = Guid.newGuid();
        addFile(id, fileName, source, contentType, bucketName, metadata);
        return id;
    }

    /*
     * (non-Javadoc)
     * @see org.sds.storage.BlobStorage#updateFile(java.util.Guid, java.lang.String, java.io.InputStream, java.lang.String, java.lang.String, java.util.Map)
     */
    public void addFile(Guid id, String fileName, InputStream source, String contentType, String bucketName,
            Map<String, Object> metadata) {
        LOGGER.debug("#addFile: [id={}, fileName={}, contentType={}, bucketName={}]", id, fileName, contentType, bucketName);
        Document meta;
        if (metadata != null) {
            meta = new Document(metadata);
        } else {
            meta = new Document();
        }
        meta.append(GridFSBlobInfo.CONTENT_TYPE_KEY, contentType == null ? DEFAULT_CONTENT_TYPE : contentType);

        GridFSUploadOptions options = new GridFSUploadOptions();
        options = options.metadata(meta);
        getBucket(bucketName).uploadFromStream(toObjectId(id),
                fileName, source, options);
    }

    /*
     * (non-Javadoc)
     * @see org.sds.storage.BlobStorage#updateFile(java.util.Guid, java.lang.String, byte[], java.lang.String, java.lang.String, java.util.Map)
     */
    public void addFile(Guid id, String fileName, byte[] source, String contentType, String bucketName,
            Map<String, Object> metadata) {
        try (ByteArrayInputStream stream = new ByteArrayInputStream(source)) {
            addFile(id, fileName, stream, contentType, bucketName, metadata);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.sds.storage.BlobStorage#deleteFile(java.util.Guid, java.lang.String)
     */
    public void deleteFile(Guid id, String bucketName) {
        LOGGER.debug("#deleteFile: [id={}, bucketName={}]", id, bucketName);
        getBucket(bucketName).delete(toObjectId(id));
    }

    /*
     * (non-Javadoc)
     * @see org.sds.storage.BlobStorage#downloadFile(java.util.Guid, java.lang.String)
     */
    public byte[] downloadFile(Guid id, String bucketName) {
        return readStream(getFileStream(id, bucketName));
    }

    /*
     * (non-Javadoc)
     * @see org.sds.storage.BlobStorage#getFileStream(java.util.Guid, java.lang.String)
     */
    public GridFSDownloadStream getFileStream(Guid id, String bucketName) {
        LOGGER.debug("#getFileStream: [id={}, bucketName={}]", id, bucketName);
        return getBucket(bucketName).openDownloadStream(toObjectId(id));
    }

    /*
     * (non-Javadoc)
     * @see org.sds.storage.BlobStorage#getFileInfo(java.util.Guid, java.lang.String)
     */
    public BlobInfo getFileInfo(Guid id, String bucketName) {
        LOGGER.debug("#getFileInfo: [id={}, bucketName={}]", id, bucketName);
        return new GridFSBlobInfo(getBucket(bucketName).find(eq("_id", toObjectId(id))).first());
    }

    private GridFSBucket getBucket(String bucketName) {
        if (bucketName == null || bucketName.isEmpty()) {
            return GridFSBuckets.create(db);
        } else {
            return GridFSBuckets.create(db, bucketName);
        }
    }

    private byte[] readStream(GridFSDownloadStream stream) {
        byte[] content = new byte[(int) stream.getGridFSFile().getLength()];
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        int data = stream.read();
        while (data >= 0) {
            outputStream.write((char)data);
            data = stream.read();
        }
        content = outputStream.toByteArray();
        try {
            outputStream.close();
        } catch (IOException e) {
            LOGGER.warn("Error closing output stream {}", e);
        }
        stream.close();

        return content;
    }

    private BsonValue toObjectId(Guid id) {
        return new BsonBinary(BSON_BINARY_SUBTYPE, id.toByteArray());
    }

    private GridFSBucket GetBucket(String bucketName) {
        
        if(bucketName != null && bucketName.trim().length() != 0)
        {
            return GridFSBuckets.create(db, bucketName);
        }
        else
        {
            return GridFSBuckets.create(db);
        }
    };
        
    @Override
    public OutputStream OpenUploadStream(Guid id, String fileName, String contentType, String bucketName, Map<String, Object> metadata) {

        GridFSBucket bucket = GetBucket(bucketName);
        
        OutputStream stream = bucket.openUploadStream(toObjectId(id), fileName, new GridFSUploadOptions().metadata(new Document().append("ContentType", contentType)));
        
        return stream;

    }

}
