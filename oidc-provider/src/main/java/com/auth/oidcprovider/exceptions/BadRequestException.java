package com.auth.oidcprovider.exceptions;

import java.util.HashMap;
import java.util.Map;

/**
 * Exception thrown when a request is invalid due to bad input.
 * Will be translated to a 400 BAD REQUEST response.
 */
public class BadRequestException extends RuntimeException {

    private final Map<String, String> errors;

    public BadRequestException(String message) {
        super(message);
        this.errors = new HashMap<>();
    }

    public BadRequestException(String message, Map<String, String> errors) {
        super(message);
        this.errors = errors;
    }
    
    public BadRequestException(String field, String errorMessage) {
        super("Invalid request parameters");
        this.errors = new HashMap<>();
        this.errors.put(field, errorMessage);
    }

    public Map<String, String> getErrors() {
        return errors;
    }
    
    public void addError(String field, String errorMessage) {
        this.errors.put(field, errorMessage);
    }
} 