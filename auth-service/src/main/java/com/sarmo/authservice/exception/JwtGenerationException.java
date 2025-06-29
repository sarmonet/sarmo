package com.sarmo.authservice.exception;

public class JwtGenerationException extends RuntimeException {
    public JwtGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
