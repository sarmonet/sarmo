package com.sarmo.contentservice.dto;

import com.sarmo.contentservice.entity.ContentItem;

import java.util.List;

public class ArticleCreateDTO {
    private String mainImage;
    private String title;
    private String description;
    private List<ContentItem> content;

    public ArticleCreateDTO() {}

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

    public List<ContentItem> getContent() {
        return content;
    }

    public void setContent(List<ContentItem> content) {
        this.content = content;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}