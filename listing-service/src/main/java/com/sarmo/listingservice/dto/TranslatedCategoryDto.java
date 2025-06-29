package com.sarmo.listingservice.dto;

import com.sarmo.listingservice.entity.Category;

public class TranslatedCategoryDto {
    private Long id;
    private String name;
    private String imageUrl;

    public TranslatedCategoryDto() {
    }

    public TranslatedCategoryDto(Long id, String name, String imageUrl) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public TranslatedCategoryDto(Category category, String translatedName) {
        this.id = category.getId();
        this.name = translatedName;
        this.imageUrl = category.getImageUrl();
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}