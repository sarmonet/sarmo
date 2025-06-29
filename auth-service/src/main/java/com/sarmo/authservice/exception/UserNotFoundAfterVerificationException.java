package com.sarmo.authservice.exception;

public class UserNotFoundAfterVerificationException extends RuntimeException {
    public UserNotFoundAfterVerificationException(String message) {
        super(message);
    }
}