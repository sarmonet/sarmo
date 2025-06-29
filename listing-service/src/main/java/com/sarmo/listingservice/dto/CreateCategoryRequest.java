package com.sarmo.listingservice.dto;

import com.sarmo.listingservice.entity.Field;

import java.util.List;

public class CreateCategoryRequest {

    private String name; // Название категории
    private List<Field> fields; // Список полей категории

    public CreateCategoryRequest() {
    }

    public CreateCategoryRequest(String name, List<Field> fields) {
        this.name = name;
        this.fields = fields;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }
}
