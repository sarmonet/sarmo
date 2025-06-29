package com.sarmo.contentservice.exception;

// Custom exception for when a user is not authorized to perform an action
public class UnauthorizedActionException extends RuntimeException {

    public UnauthorizedActionException(String message) {
        super(message);
    }

    public UnauthorizedActionException(String message, Throwable cause) {
        super(message, cause);
    }
}