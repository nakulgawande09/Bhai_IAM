package com.auth.oidcprovider.exceptions;

import com.auth.oidcprovider.dto.ApiResponse;
import com.auth.oidcprovider.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the application.
 * Provides consistent error responses for all exceptions.
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    /**
     * Handles ResourceNotFoundException.
     * 
     * @param ex The exception
     * @param request The current request
     * @return A consistent error response with 404 status
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleResourceNotFoundException(
            ResourceNotFoundException ex, HttpServletRequest request) {
        
        logger.error("Resource not found: {}", ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(ex.getMessage())
                .errorCode("RESOURCE_NOT_FOUND")
                .status(HttpStatus.NOT_FOUND.value())
                .path(request.getRequestURI())
                .build();
        
        return new ResponseEntity<>(ApiResponse.error("Resource not found", errorResponse), HttpStatus.NOT_FOUND);
    }
    
    /**
     * Handles UnauthorizedException.
     * 
     * @param ex The exception
     * @param request The current request
     * @return A consistent error response with 401 status
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleUnauthorizedException(
            UnauthorizedException ex, HttpServletRequest request) {
        
        logger.error("Unauthorized access: {}", ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(ex.getMessage())
                .errorCode("UNAUTHORIZED")
                .status(HttpStatus.UNAUTHORIZED.value())
                .path(request.getRequestURI())
                .build();
        
        return new ResponseEntity<>(ApiResponse.error("Unauthorized", errorResponse), HttpStatus.UNAUTHORIZED);
    }
    
    /**
     * Handles BadRequestException.
     * 
     * @param ex The exception
     * @param request The current request
     * @return A consistent error response with 400 status
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleBadRequestException(
            BadRequestException ex, HttpServletRequest request) {
        
        logger.error("Bad request: {}", ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(ex.getMessage())
                .errorCode("BAD_REQUEST")
                .status(HttpStatus.BAD_REQUEST.value())
                .path(request.getRequestURI())
                .errors(ex.getErrors())
                .build();
        
        return new ResponseEntity<>(ApiResponse.error("Bad request", errorResponse), HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Handles KeycloakIntegrationException.
     * 
     * @param ex The exception
     * @param request The current request
     * @return A consistent error response with appropriate status
     */
    @ExceptionHandler(KeycloakIntegrationException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleKeycloakIntegrationException(
            KeycloakIntegrationException ex, HttpServletRequest request) {
        
        logger.error("Keycloak integration error: {}", ex.getMessage());
        
        HttpStatus status = ex.getStatusCode() == 502 ? HttpStatus.BAD_GATEWAY : HttpStatus.INTERNAL_SERVER_ERROR;
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(ex.getMessage())
                .errorCode(ex.getErrorCode())
                .status(ex.getStatusCode())
                .path(request.getRequestURI())
                .build();
        
        return new ResponseEntity<>(ApiResponse.error("Keycloak integration error", errorResponse), status);
    }
    
    /**
     * Handles InvalidTokenException.
     * 
     * @param ex The exception
     * @param request The current request
     * @return A consistent error response with 401 status
     */
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleInvalidTokenException(
            InvalidTokenException ex, HttpServletRequest request) {
        
        logger.error("Invalid token: {}", ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(ex.getMessage())
                .errorCode("INVALID_TOKEN")
                .status(HttpStatus.UNAUTHORIZED.value())
                .path(request.getRequestURI())
                .build();
        
        return new ResponseEntity<>(ApiResponse.error("Invalid token", errorResponse), HttpStatus.UNAUTHORIZED);
    }
    
    /**
     * Handles validation exceptions thrown by @Valid annotations.
     * 
     * @param ex The exception
     * @param request The current request
     * @return A consistent error response with 400 status and detailed validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleValidationExceptions(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        
        logger.error("Validation error: {}", ex.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message("Validation failed")
                .errorCode("VALIDATION_ERROR")
                .status(HttpStatus.BAD_REQUEST.value())
                .path(request.getRequestURI())
                .errors(errors)
                .build();
        
        return new ResponseEntity<>(ApiResponse.error("Validation failed", errorResponse), HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Handles AccessDeniedException from Spring Security.
     * 
     * @param ex The exception
     * @param request The current request
     * @return A consistent error response with 403 status
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleAccessDeniedException(
            AccessDeniedException ex, HttpServletRequest request) {
        
        logger.error("Access denied: {}", ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message("Access denied: " + ex.getMessage())
                .errorCode("ACCESS_DENIED")
                .status(HttpStatus.FORBIDDEN.value())
                .path(request.getRequestURI())
                .build();
        
        return new ResponseEntity<>(ApiResponse.error("Access denied", errorResponse), HttpStatus.FORBIDDEN);
    }
    
    /**
     * Handles constraint violation exceptions.
     * 
     * @param ex The exception
     * @param request The current request
     * @return A consistent error response with 400 status
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleConstraintViolationException(
            ConstraintViolationException ex, HttpServletRequest request) {
        
        logger.error("Constraint violation: {}", ex.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            String propertyPath = violation.getPropertyPath().toString();
            String field = propertyPath.substring(propertyPath.lastIndexOf('.') + 1);
            errors.put(field, violation.getMessage());
        }
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message("Constraint violation")
                .errorCode("CONSTRAINT_VIOLATION")
                .status(HttpStatus.BAD_REQUEST.value())
                .path(request.getRequestURI())
                .errors(errors)
                .build();
        
        return new ResponseEntity<>(ApiResponse.error("Constraint violation", errorResponse), HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Handles method argument type mismatch exceptions.
     * 
     * @param ex The exception
     * @param request The current request
     * @return A consistent error response with 400 status
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        
        logger.error("Type mismatch: {}", ex.getMessage());
        
        String message = String.format("Parameter '%s' should be of type '%s'", 
                ex.getName(), ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown");
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(message)
                .errorCode("TYPE_MISMATCH")
                .status(HttpStatus.BAD_REQUEST.value())
                .path(request.getRequestURI())
                .addError(ex.getName(), message)
                .build();
        
        return new ResponseEntity<>(ApiResponse.error("Type mismatch", errorResponse), HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Handles missing servlet request parameter exceptions.
     * 
     * @param ex The exception
     * @param request The current request
     * @return A consistent error response with 400 status
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex, HttpServletRequest request) {
        
        logger.error("Missing parameter: {}", ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message("Missing required parameter: " + ex.getParameterName())
                .errorCode("MISSING_PARAMETER")
                .status(HttpStatus.BAD_REQUEST.value())
                .path(request.getRequestURI())
                .addError(ex.getParameterName(), "Parameter is required")
                .build();
        
        return new ResponseEntity<>(ApiResponse.error("Missing parameter", errorResponse), HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Handles method not supported exceptions.
     * 
     * @param ex The exception
     * @param request The current request
     * @return A consistent error response with 405 status
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        
        logger.error("Method not supported: {}", ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message("Method " + ex.getMethod() + " not supported for this endpoint")
                .errorCode("METHOD_NOT_SUPPORTED")
                .status(HttpStatus.METHOD_NOT_ALLOWED.value())
                .path(request.getRequestURI())
                .build();
        
        return new ResponseEntity<>(ApiResponse.error("Method not supported", errorResponse), HttpStatus.METHOD_NOT_ALLOWED);
    }
    
    /**
     * Handles no handler found exceptions.
     * 
     * @param ex The exception
     * @param request The current request
     * @return A consistent error response with 404 status
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleNoHandlerFoundException(
            NoHandlerFoundException ex, HttpServletRequest request) {
        
        logger.error("No handler found: {}", ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message("No handler found for " + ex.getHttpMethod() + " " + ex.getRequestURL())
                .errorCode("NOT_FOUND")
                .status(HttpStatus.NOT_FOUND.value())
                .path(request.getRequestURI())
                .build();
        
        return new ResponseEntity<>(ApiResponse.error("Endpoint not found", errorResponse), HttpStatus.NOT_FOUND);
    }
    
    /**
     * Handles bind exceptions.
     * 
     * @param ex The exception
     * @param request The current request
     * @return A consistent error response with 400 status
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleBindException(
            BindException ex, HttpServletRequest request) {
        
        logger.error("Bind exception: {}", ex.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage())
        );
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message("Binding error")
                .errorCode("BINDING_ERROR")
                .status(HttpStatus.BAD_REQUEST.value())
                .path(request.getRequestURI())
                .errors(errors)
                .build();
        
        return new ResponseEntity<>(ApiResponse.error("Binding error", errorResponse), HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Fallback handler for all other exceptions.
     * 
     * @param ex The exception
     * @param request The current request
     * @return A consistent error response with 500 status
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleGlobalException(
            Exception ex, HttpServletRequest request) {
        
        logger.error("Unhandled exception occurred", ex);
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message("An unexpected error occurred")
                .errorCode("INTERNAL_SERVER_ERROR")
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .path(request.getRequestURI())
                .build();
        
        return new ResponseEntity<>(ApiResponse.error("Internal server error", errorResponse), HttpStatus.INTERNAL_SERVER_ERROR);
    }
} 