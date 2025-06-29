package com.sarmo.contentservice.dto;

public class ArticleUpdateDTO {

    private String mainImage;
    private String title;
    private String description;

    public ArticleUpdateDTO() {
    }

    public String getMainImage() {
        return mainImage;
    }

    public void setMainImage(String mainImage) {
        this.mainImage = mainImage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // TODO: Add getters/setters for any other updateable fields included above
}