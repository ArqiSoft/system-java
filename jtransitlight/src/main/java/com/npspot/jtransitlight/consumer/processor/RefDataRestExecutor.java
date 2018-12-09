package com.npspot.jtransitlight.consumer.processor;

import com.npspot.jtransitlight.MessageProcessingException;
import com.npspot.jtransitlight.consumer.callback.SnapshotDeltaCallback;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class RefDataRestExecutor implements RestExecutor {

    private String uri;

    private SnapshotDeltaCallback messageCallback;

    public RefDataRestExecutor(String uri, SnapshotDeltaCallback messageCallback) {
        this.uri = uri;
        this.messageCallback = messageCallback;
    }

    public void executeSnapshotRequest() throws IOException, MessageProcessingException {
        messageCallback.onSnapshot(getSnapshot());
    }

    public byte[] getSnapshot() throws IOException {
        return getRestResult(uri).getBytes();
    }

    @Override
    public Long executeGetLastSeqId() throws IOException {
        return Long.parseLong(getRestResult(uri + "/lastseq"));
    }

    private String getRestResult(String uri) throws IOException {
        URL url = new URL(uri);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");

        // On 204 send empty
        if (conn.getResponseCode() == HttpURLConnection.HTTP_NO_CONTENT) {
            return "[]";
        }

        if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new RuntimeException("Failed : HTTP error code : "
                    + conn.getResponseCode() + ", " + conn.getResponseMessage());
        }

        String result = IOUtils.toString(conn.getInputStream());
        conn.disconnect();
        return result;
    }

}