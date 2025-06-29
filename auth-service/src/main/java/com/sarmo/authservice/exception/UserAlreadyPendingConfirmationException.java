package com.sarmo.authservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT) // HTTP статус 409 Conflict
public class UserAlreadyPendingConfirmationException extends RuntimeException {
    public UserAlreadyPendingConfirmationException(String message) {
        super(message);
    }
}