package com.sarmo.chatservice.exception;

import java.util.NoSuchElementException;

public class ChatNotFoundException extends NoSuchElementException {
    public ChatNotFoundException(String message) {
        super(message);
    }
}