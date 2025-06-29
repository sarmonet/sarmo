package com.sarmo.authservice.exception;

public class InvalidJwtRolesException extends Exception {

    public InvalidJwtRolesException(String message) {
        super(message);
    }
}