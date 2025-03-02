package com.auth.oidcprovider.dto;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Standardized error response object for API error responses.
 */
public class ErrorResponse {
    
    private String message;
    private String errorCode;
    private int status;
    private String path;
    private LocalDateTime timestamp;
    private Map<String, String> errors;
    
    public ErrorResponse() {
        this.timestamp = LocalDateTime.now();
        this.errors = new HashMap<>();
    }
    
    public ErrorResponse(String message, String errorCode, int status, String path) {
        this.message = message;
        this.errorCode = errorCode;
        this.status = status;
        this.path = path;
        this.timestamp = LocalDateTime.now();
        this.errors = new HashMap<>();
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
    
    public int getStatus() {
        return status;
    }
    
    public void setStatus(int status) {
        this.status = status;
    }
    
    public String getPath() {
        return path;
    }
    
    public void setPath(String path) {
        this.path = path;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public Map<String, String> getErrors() {
        return errors;
    }
    
    public void setErrors(Map<String, String> errors) {
        this.errors = errors;
    }
    
    public void addError(String field, String message) {
        this.errors.put(field, message);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ErrorResponse that = (ErrorResponse) o;
        return status == that.status &&
                Objects.equals(message, that.message) &&
                Objects.equals(errorCode, that.errorCode) &&
                Objects.equals(path, that.path) &&
                Objects.equals(timestamp, that.timestamp) &&
                Objects.equals(errors, that.errors);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(message, errorCode, status, path, timestamp, errors);
    }
    
    @Override
    public String toString() {
        return "ErrorResponse{" +
                "message='" + message + '\'' +
                ", errorCode='" + errorCode + '\'' +
                ", status=" + status +
                ", path='" + path + '\'' +
                ", timestamp=" + timestamp +
                ", errors=" + errors +
                '}';
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private String message;
        private String errorCode;
        private int status;
        private String path;
        private LocalDateTime timestamp = LocalDateTime.now();
        private Map<String, String> errors = new HashMap<>();
        
        public Builder message(String message) {
            this.message = message;
            return this;
        }
        
        public Builder errorCode(String errorCode) {
            this.errorCode = errorCode;
            return this;
        }
        
        public Builder status(int status) {
            this.status = status;
            return this;
        }
        
        public Builder path(String path) {
            this.path = path;
            return this;
        }
        
        public Builder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }
        
        public Builder errors(Map<String, String> errors) {
            this.errors = errors;
            return this;
        }
        
        public Builder addError(String field, String errorMessage) {
            this.errors.put(field, errorMessage);
            return this;
        }
        
        public ErrorResponse build() {
            ErrorResponse response = new ErrorResponse(message, errorCode, status, path);
            response.setTimestamp(timestamp);
            response.setErrors(errors);
            return response;
        }
    }
} 