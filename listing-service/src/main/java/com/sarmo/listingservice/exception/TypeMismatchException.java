package com.sarmo.listingservice.exception;

public class TypeMismatchException extends ValidationException {
    public TypeMismatchException(String fieldName, String expectedType, Object actualValue) {
        super("Type mismatch for field '" + fieldName + "'. Expected: " + expectedType + ", Actual: " + (actualValue != null ? actualValue.getClass().getSimpleName() : "null"));
    }
}