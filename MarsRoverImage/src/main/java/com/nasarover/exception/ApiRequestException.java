package com.nasarover.exception;

/**
 * Exception thrown when there is an error with the NASA API request.
 */
public class ApiRequestException extends Exception {
    
    public ApiRequestException(String message) {
        super(message);
    }
    
    public ApiRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
