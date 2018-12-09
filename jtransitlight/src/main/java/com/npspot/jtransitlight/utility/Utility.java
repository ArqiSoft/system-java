package com.npspot.jtransitlight.utility;

import com.npspot.jtransitlight.JTransitLightException;
import com.npspot.jtransitlight.contract.Contract;
import com.npspot.jtransitlight.contract.ContractType;
import com.npspot.jtransitlight.publisher.sequence.ReferenceSequenceProvider;
import com.npspot.jtransitlight.publisher.sequence.SequenceProvider;
import com.npspot.jtransitlight.publisher.sequence.TransactionSequenceProvider;

import java.util.UUID;

public class Utility {

    public static final String SEQUENCE_NUMBER_HEADER = "sequenceNumber";
    public static final String IS_SNAPSHOT_HEADER = "isSnapshot";

    /**
     * Build the exchange name from the contract class.<br>
     * The name consists of two parts separated by ":".<br> Part one is the C#
     * namespace which should be accessible by a "getNamespace" method on the
     * contract class. Part two is the class name withou class path.
     *
     * @param contract
     * @return
     * @throws JTransitLightException
     */
    public static String getContractExchangeId(Contract contract) throws JTransitLightException {
        String namespace = contract.getNamespace();
        String className = contract.getContractName();
        return namespace + ":" + className;
    }

    public static String getHeartbeatExchangeId(Contract contract) throws JTransitLightException {
        String namespace = contract.getNamespace();
        return namespace + "Heartbeat";
    }

    /**
     * Generate a (hopefully) globally unique id.
     *
     * @return Unique id.
     */
    public static UUID getUniqueId() {
        UUID uuid = UUID.randomUUID();
        return uuid;
    }

    public static SequenceProvider getSequenceProvider(ContractType contractType) {
        SequenceProvider sequenceProvider;
        if (contractType == ContractType.TRANSACTION_TYPE) {
            sequenceProvider = new TransactionSequenceProvider();
        } else {
            sequenceProvider = new ReferenceSequenceProvider();
        }
        return sequenceProvider;
    }
}
