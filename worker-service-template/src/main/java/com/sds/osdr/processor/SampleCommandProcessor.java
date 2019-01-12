package com.sds.osdr.processor;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.npspot.jtransitlight.JTransitLightException;
import com.npspot.jtransitlight.consumer.ReceiverBusControl;
import com.npspot.jtransitlight.publisher.IBusControl;
import com.sds.osdr.model.FailureEvent;
import com.sds.osdr.model.SampleCommand;
import com.sds.osdr.model.SuccessEvent;
import com.sds.storage.BlobStorage;

@Component
public class SampleCommandProcessor implements MessageProcessor<SampleCommand> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SampleCommandProcessor.class);

    ReceiverBusControl receiver;
    IBusControl bus;
    BlobStorage storage;

    
    @Autowired
    public SampleCommandProcessor(ReceiverBusControl receiver, IBusControl bus,
            BlobStorage storage) throws JTransitLightException, IOException {
        this.bus = bus;
        this.receiver = receiver;
        this.storage = storage;
    }

    @Override
    public void process(SampleCommand message) {
            // dowwnload file
            // byte[] data = storage.downloadFile(new Guid(message.getBlobId()), message.getBucket());

            // get file info 
            // BlobInfo fileInfo = storage.getFileInfo(new Guid(message.getBlobId()), message.getBucket());

            // processing logic here

            // save file
            // Map<String, Object> metadata = new HashMap<>();
            // metadata.put("SourceId", message.getBlobId());
            // storage.addFile(new Guid(file UUId), <file UUId>.toString(), data, mimetype,
            //        message.getBucket(), metadata);
	

        if (message.getBucket() != null /* everything is OK*/) {
            publishSuccessEvent(message);
        } else {
            publishFailureEvent(message, "Error message");
        }
    }

    private void publishSuccessEvent(SampleCommand message) {
        SuccessEvent event = new SuccessEvent();
        event.setId(UUID.randomUUID());
        event.setUserId(message.getUserId());
        event.setTimeStamp(getTimestamp());
        event.setBucket(message.getBucket());
        event.setCorrelationId(message.getCorrelationId());
        
        LOGGER.debug("Publishing event {}", event);
        
        try {
            bus.publish(event);
        } catch (JTransitLightException e) {
            LOGGER.error("failed to publish event: {}", e.getMessage());
        }
    }

    private void publishFailureEvent(SampleCommand message, 
            String exception) {
        FailureEvent event = new FailureEvent();
        event.setId(UUID.randomUUID());
        event.setUserId(message.getUserId());
        event.setTimeStamp(getTimestamp());
        event.setCorrelationId(message.getCorrelationId());
        
        LOGGER.debug("Publishing event {}", event);
        
        try {
            bus.publish(event);
        } catch (JTransitLightException e) {
            LOGGER.error("failed to publish event: {}", e.getMessage());
        }
    }

    private String getTimestamp() {
        //("yyyy-MM-dd'T'HH:mm:ss'Z'")
        return LocalDateTime.now().toString();
    }
    

}
