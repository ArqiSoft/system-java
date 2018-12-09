package com.sds.validation;

public class JsonValidationException extends RuntimeException {

    private static final long serialVersionUID = -2373300480783454680L;
    
    public JsonValidationException() {
        super();
    }

    public JsonValidationException(String message) {
        super(message);
    }

}
