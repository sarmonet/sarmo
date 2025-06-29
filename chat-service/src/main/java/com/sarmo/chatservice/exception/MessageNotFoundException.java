package com.sarmo.chatservice.exception;

import java.util.NoSuchElementException;

public class MessageNotFoundException extends NoSuchElementException {
    public MessageNotFoundException(String message) {
        super(message);
    }
}