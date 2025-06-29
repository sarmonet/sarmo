package com.sarmo.listingservice.exception;

public class JwtParsingException extends RuntimeException {
    public JwtParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}