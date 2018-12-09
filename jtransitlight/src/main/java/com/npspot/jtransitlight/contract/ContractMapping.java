/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.npspot.jtransitlight.contract;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Rami
 */
public class ContractMapping {
    
    private static final Map<String,Long> contractSequenceMap = new ConcurrentHashMap<>();
    
    public static Long getCurrentSequence(String contractId) {
        Long sequence = contractSequenceMap.get(contractId);
        if(sequence == null) {
            return -1l;
        }
        return contractSequenceMap.get(contractId);
    }
    
    public static Set<String> getActiveContracts() {
        return contractSequenceMap.keySet();
    }

    public static void updateActiveContract(String contractId, Long messageSequence) {
        contractSequenceMap.put(contractId, messageSequence);
    }
    
}
