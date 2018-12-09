package com.npspot.jtransitlight.consumer.processor;

import com.npspot.jtransitlight.MessageProcessingException;
import com.npspot.jtransitlight.consumer.TrxDataConsumerWatcher;
import com.npspot.jtransitlight.consumer.callback.SnapshotDeltaCallback;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TrxDataRestExecutor implements RestExecutor {

    private static final Logger LOGGER = LogManager.getLogger(TrxDataConsumerWatcher.class);

    private String uri;
    private SnapshotDeltaCallback messageCallback;

    private long startTimeUTC;
    private long endTimeUTC;

    public TrxDataRestExecutor(String uri, long startTimeUTC, long endTimeUTC, SnapshotDeltaCallback messageCallback) {
        this.uri = uri;
        this.messageCallback = messageCallback;
        this.startTimeUTC = startTimeUTC;
        this.endTimeUTC = endTimeUTC;
    }

    public void executeSnapshotRequest() throws IOException, MessageProcessingException {
        messageCallback.onSnapshot(getSnapshot());
    }

    public void executeSequenceRequest(long fromSeqNo, long toSeqNo) throws Exception {
        String sequenceUrl = uri + "?"
                + String.format("fromSeqNo=%s", URLEncoder.encode(fromSeqNo + "", "UTF-8"))
                + String.format("toSeqNo=%s", URLEncoder.encode(toSeqNo + "", "UTF-8"));
        try {
            final String restResult = getRestResult(sequenceUrl);
            messageCallback.onSnapshot(restResult.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("Error executing sequence request: " + sequenceUrl + " " + e.getMessage());
        } catch (MessageProcessingException e) {
            e.printStackTrace();
            LOGGER.error("Error parsing received sequence response as snapshot. " + e.getMessage());
        }
    }

    public byte[] getSnapshot() throws IOException {
        final String startTimeUTCStr = String.format("startTimeUTC=%s", URLEncoder.encode(startTimeUTC + "", "UTF-8"));
        final String endTimeUTCStr = String.format("endTimeUTC=%s", URLEncoder.encode(endTimeUTC + "", "UTF-8"));

        return getRestResult(uri + "?" + startTimeUTCStr + "&" + endTimeUTCStr).getBytes();
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

        if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new IOException("Failed : HTTP error code : "
                    + conn.getResponseCode() + ", " + conn.getResponseMessage());
        }
        String result = IOUtils.toString(conn.getInputStream());
        conn.disconnect();

        return result;
    }
}