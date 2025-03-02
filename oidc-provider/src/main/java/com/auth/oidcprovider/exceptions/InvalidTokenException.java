package com.auth.oidcprovider.exceptions;

/**
 * Exception thrown when a JWT token is invalid or cannot be processed.
 */
public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException(String message) {
        super(message);
    }

    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause);
    }
} 