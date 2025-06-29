package com.sarmo.listingservice.dto;

import com.sarmo.listingservice.entity.SubCategory;

public class TranslatedSubCategoryDto {
    private Long id;
    private String name;
    private Long categoryId;
    private String categoryName;

    public TranslatedSubCategoryDto() {
    }

    public TranslatedSubCategoryDto(Long id, String name, Long categoryId, String categoryName) {
        this.id = id;
        this.name = name;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    public TranslatedSubCategoryDto(SubCategory subCategory, String translatedName, String translatedCategoryName) {
        this.id = subCategory.getId();
        this.name = translatedName;
        this.categoryId = subCategory.getCategory() != null ? subCategory.getCategory().getId() : null;
        this.categoryName = translatedCategoryName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}