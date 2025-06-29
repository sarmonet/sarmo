package com.sarmo.authservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyTakenException.class)
    public ResponseEntity<String> handleEmailAlreadyTakenException(EmailAlreadyTakenException ex) {
        String errorMessage = ex.getMessage();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }

    // Опционально: обработка других RuntimeException, если нужно
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        String errorMessage = ex.getMessage();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage); // Или другой код ошибки
    }
}