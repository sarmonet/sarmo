package com.sarmo.listingservice.entity;

import jakarta.persistence.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "investment_category_fields") // Указываем новую коллекцию
public class InvestmentCategoryField {

    @Id
    private Long categoryId;
    private List<Field> fields;

    public InvestmentCategoryField() {
    }

    public InvestmentCategoryField(Long categoryId, List<Field> fields) {
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