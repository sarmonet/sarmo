package com.sarmo.contentservice.exception;

// Custom exception for when a resource (e.g., Article, User) is not found
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}