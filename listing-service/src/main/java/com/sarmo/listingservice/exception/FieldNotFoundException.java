package com.sarmo.listingservice.exception;

public class FieldNotFoundException extends ValidationException {
    public FieldNotFoundException(String fieldName) {
        super("Required field '" + fieldName + "' is missing.");
    }
}