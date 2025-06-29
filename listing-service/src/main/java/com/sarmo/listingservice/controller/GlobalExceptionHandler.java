package com.sarmo.listingservice.controller;

import com.nimbusds.jose.JOSEException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.text.ParseException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException e) {
        // Обработка RuntimeException
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Resource not found: " + e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        // Обработка IllegalArgumentException
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Invalid request: " + e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        // Обработка всех других исключений
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An unexpected error occurred: " + e.getMessage());
    }

    @ExceptionHandler(JOSEException.class)
    public ResponseEntity<String> handleJOSEException(JOSEException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid JWT token");
    }

    @ExceptionHandler(ParseException.class)
    public ResponseEntity<String> handleParseException(ParseException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid date format");
    }

}
