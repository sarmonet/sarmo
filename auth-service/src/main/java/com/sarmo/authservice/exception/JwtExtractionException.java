package com.sarmo.authservice.exception;

public class JwtExtractionException extends RuntimeException {
    public JwtExtractionException(String message, Throwable cause) {
        super(message, cause);
    }
}