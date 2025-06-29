package com.sarmo.listingservice.entity;

import jakarta.persistence.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "category_fields")
public class CategoryField {

    @Id
    private Long categoryId; // ID категории
    private List<Field> fields; // Список специфичных полей для категории

    public CategoryField() {
    }

    public CategoryField(Long categoryId, List<Field> fields) {
        this.categoryId = categoryId;
        this.fields = fields;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }
}
