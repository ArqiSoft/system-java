package com.npspot.jtransitlight.publisher;

import com.npspot.jtransitlight.JTransitLightException;
import com.npspot.jtransitlight.MessageContext;
import com.npspot.jtransitlight.contract.Contract;
import com.npspot.jtransitlight.contract.ContractMapping;
import com.npspot.jtransitlight.publisher.model.BusMessage;
import com.npspot.jtransitlight.publisher.model.RabbitMQMessageContext;
import com.npspot.jtransitlight.publisher.sequence.SequenceProvider;
import com.npspot.jtransitlight.serializer.Serializer;
import com.npspot.jtransitlight.transport.Connection;
import com.npspot.jtransitlight.utility.Utility;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Implementation of publishing functionality.
 */
public abstract class PublishEndpoint implements IBusControl {

    private static final Logger LOGGER = LogManager.getLogger(PublishEndpoint.class);
    private SequenceProvider sequenceProvider;
    private String encodingName = "UTF-8";

    public PublishEndpoint(SequenceProvider sequenceProvider) {
        this.sequenceProvider = sequenceProvider;
    }

    @Override
    public void publish(Contract contract) {
        try {
            LOGGER.info("[Publish]  Message {}.{} with Id: {} is about to be published. Message: {}", 
                contract.getNamespace(), contract.getContractName(), contract.getId(), contract);
            MessageContext<Contract> context = new MessageContext<>();
            context.setConversationId(Utility.getUniqueId());
            publish(contract, context);
            LOGGER.info("[Publish]  Message {}.{} with Id: {}  successdully published.", 
                contract.getNamespace(), contract.getContractName(), contract.getId());
        }
        catch(JTransitLightException e) {
            LOGGER.error("[Publish] Cannot publish message {}.{} with Id: {}. Message: {}. Exception: {}", 
                    contract.getNamespace(), contract.getContractName(), contract.getId(), contract, e.toString());
        }

    }

    @Override
    public void publish(Contract contract, boolean createExchange) throws IOException, JTransitLightException {
        if (createExchange) {
            getConnection().getTransport().declareFanoutAndErrorExchanges(Utility.getContractExchangeId(contract));
        }
        publish(contract);
    }

    @Override
    public void publishHeartbeat(Contract contract) {
        try {
            MessageContext<Contract> context = new MessageContext<>();
            context.setConversationId(Utility.getUniqueId());
            publishHeartbeat(contract, context);
        } catch (JTransitLightException ex) {
            LOGGER.error("Error publishing heartbeat {}", ex);
        }
    }

    @Override
    public void publish(Contract contract, MessageContext<Contract> messageContext) throws JTransitLightException {
        String exchangeName = Utility.getContractExchangeId(contract);
        sequenceProvider.processSequence(exchangeName, contract);
        ContractMapping.updateActiveContract(exchangeName, contract.getMessageSequence());
        internalPublish(contract, messageContext, exchangeName, contract.getQueueName());
    }

    private void internalPublish(Contract contract, MessageContext<Contract> messageContext, String exchangeName, String queueName) throws JTransitLightException {
        try {

            BusMessage<Contract> busMessage = new BusMessage<>();
            busMessage.setConversationId(messageContext.getConversationId());
            busMessage.setMessageId(Utility.getUniqueId());
            busMessage.setMessageType("urn:message:" + exchangeName);
            busMessage.setMessage(contract);

            String targetAddress = getConnection().getDestinationString() + exchangeName;
            busMessage.setTargetAddress(targetAddress);

            busMessage.setSourceAddress(getConnection().getSourceAddressString(exchangeName, queueName));

            String serializedBusMessage = getSerializer().serialize(busMessage);

            RabbitMQMessageContext rabbitMQMessageContext = new RabbitMQMessageContext();
            rabbitMQMessageContext.setExchange(exchangeName);
            rabbitMQMessageContext.setMessage(serializedBusMessage.getBytes(encodingName));
            rabbitMQMessageContext.setMessageId(busMessage.getMessageId().toString());
            rabbitMQMessageContext.addHeaders(messageContext.getHeaders());
            rabbitMQMessageContext.addHeader(Utility.SEQUENCE_NUMBER_HEADER, contract.getMessageSequence());
            rabbitMQMessageContext.addHeader(Utility.IS_SNAPSHOT_HEADER, contract.isSnapshot());
            rabbitMQMessageContext.setSupplierSettings(contract.getSupplierSettings());

            getConnection().publish(rabbitMQMessageContext);
        } catch (UnsupportedEncodingException exception) {
            throw new JTransitLightException("Unable to convert contract to UTF-8 charset.", exception);
        }

    }

    /**
     * For test use only when the connection is created into "internal" memory
     *
     * @param contractId
     * @return
     */
    public List<byte[]> fetchInternalMemoryMessages(String contractId) {
        return getConnection().fetchInternalMessages(contractId);
    }

    public void cleanInternalMemoryMessages() {
        this.getConnection().cleanInternalMessages();
    }

    @Override
    public void publishHeartbeat(Contract contract, MessageContext<Contract> messageContext) throws JTransitLightException {
        String exchangeName = Utility.getHeartbeatExchangeId(contract);
        internalPublish(contract, messageContext, exchangeName, contract.getQueueName());
    }

    @Override
    public void setMessageEncoding(String encodingName) {
        this.encodingName = encodingName;
    }

    abstract Connection getConnection();

    abstract Serializer getSerializer();
}
