package com.sarmo.contentservice.dto;

import com.sarmo.contentservice.entity.ContentItem;

import java.util.List;

public class ArticleUpdateFullDTO {
    private String mainImage;
    private String title;
    private String description;
    private List<ContentItem> content; // To update the content in MongoDB

    public ArticleUpdateFullDTO() {}

    // Getters and Setters

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

    public List<ContentItem> getContent() {
        return content;
    }

    public void setContent(List<ContentItem> content) {
        this.content = content;
    }
}