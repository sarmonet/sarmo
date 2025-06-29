package com.sarmo.listingservice.exception;

public class CategoryFieldsNotFoundException extends ValidationException {
    public CategoryFieldsNotFoundException(Long categoryId) {
        super("Category fields not found for categoryId: " + categoryId);
    }
}