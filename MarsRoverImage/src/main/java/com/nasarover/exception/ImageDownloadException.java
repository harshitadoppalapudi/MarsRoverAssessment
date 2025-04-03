package com.nasarover.exception;

/**
 * Exception thrown when there is an error downloading an image.
 */
public class ImageDownloadException extends Exception {
    
    public ImageDownloadException(String message) {
        super(message);
    }
    
    public ImageDownloadException(String message, Throwable cause) {
        super(message, cause);
    }
}
