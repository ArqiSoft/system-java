/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.npspot.jtransitlight.consumer;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Headers for error message when forwarded to error exchange
 * @author Rami
 */
public class ErrorHeaders {
    
    static final String ERROR_MESSAGE_HEADER = "NP-Error-Message";
    static final String ERROR_TIME_HEADER = "NP-Error-Time";
    static final String ERROR_STACK_TRACE_HEADER = "NP-Error-StackTrace";
    
    public static Map<String,String> getErrorHeaders(Exception ex) {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
        Map<String,String> errorHeaders = new HashMap<>();
        errorHeaders.put(ERROR_MESSAGE_HEADER,ex.getMessage());
        errorHeaders.put(ERROR_TIME_HEADER, now.toString());
        
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        errorHeaders.put(ERROR_STACK_TRACE_HEADER, sw.toString());
        return errorHeaders;
    }
    
}
