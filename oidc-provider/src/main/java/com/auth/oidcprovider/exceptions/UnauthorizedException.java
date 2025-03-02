package com.auth.oidcprovider.exceptions;

/**
 * Exception thrown when a user is not authorized to perform an action.
 * Will be translated to a 401 UNAUTHORIZED response.
 */
public class UnauthorizedException extends RuntimeException {

    private final String action;
    private final String resource;

    public UnauthorizedException(String message) {
        super(message);
        this.action = null;
        this.resource = null;
    }

    public UnauthorizedException(String action, String resource) {
        super(String.format("You are not authorized to %s this %s", action, resource));
        this.action = action;
        this.resource = resource;
    }

    public String getAction() {
        return action;
    }

    public String getResource() {
        return resource;
    }
} 