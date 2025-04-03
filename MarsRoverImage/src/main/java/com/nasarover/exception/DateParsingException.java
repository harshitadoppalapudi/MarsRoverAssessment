package com.nasarover.exception;

/**
 * Exception thrown when there is an error parsing a date string.
 */
public class DateParsingException extends Exception {
    
    public DateParsingException(String message) {
        super(message);
    }
    
    public DateParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}
