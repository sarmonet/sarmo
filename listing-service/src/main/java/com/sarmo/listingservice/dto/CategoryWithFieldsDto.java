package com.sarmo.listingservice.dto;

import com.sarmo.listingservice.entity.Category;
import com.sarmo.listingservice.entity.CategoryField;
import com.sarmo.listingservice.entity.Field;

import java.util.List;

public class CategoryWithFieldsDto {
    private Long categoryId;
    private String categoryName;
    private List<Field>  fields;

    public CategoryWithFieldsDto() {}

    public CategoryWithFieldsDto(Long categoryId, String categoryName, List<Field> fields) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.fields = fields;
    }

    public CategoryWithFieldsDto(Category category, CategoryField categoryField) {
        this.categoryId = category.getId();
        this.categoryName = category.getName();
        this.fields = categoryField.getFields();
    }

    public CategoryWithFieldsDto(Category category, List<Field> fields) {
        this.categoryId = category.getId();
        this.categoryName = category.getName();
        this.fields = fields;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }
}
