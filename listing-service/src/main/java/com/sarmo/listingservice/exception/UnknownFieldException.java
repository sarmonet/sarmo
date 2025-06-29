package com.sarmo.listingservice.exception;

public class UnknownFieldException extends ValidationException {
    public UnknownFieldException(String fieldName) {
        super("Unknown field '" + fieldName + "' found.");
    }
}