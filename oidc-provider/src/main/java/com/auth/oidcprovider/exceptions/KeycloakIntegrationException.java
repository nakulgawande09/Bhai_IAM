package com.auth.oidcprovider.exceptions;

/**
 * Exception thrown when there are issues with Keycloak integration.
 * Will be translated to a 502 BAD GATEWAY or 500 INTERNAL SERVER ERROR response.
 */
public class KeycloakIntegrationException extends RuntimeException {

    private final int statusCode;
    private final String errorCode;

    public KeycloakIntegrationException(String message) {
        super(message);
        this.statusCode = 500;
        this.errorCode = "KEYCLOAK_ERROR";
    }

    public KeycloakIntegrationException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = 500;
        this.errorCode = "KEYCLOAK_ERROR";
    }

    public KeycloakIntegrationException(String message, int statusCode, String errorCode) {
        super(message);
        this.statusCode = statusCode;
        this.errorCode = errorCode;
    }

    public KeycloakIntegrationException(String message, Throwable cause, int statusCode, String errorCode) {
        super(message, cause);
        this.statusCode = statusCode;
        this.errorCode = errorCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
} 