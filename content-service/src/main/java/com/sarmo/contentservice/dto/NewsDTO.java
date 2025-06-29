package com.sarmo.contentservice.dto; // Укажите актуальный пакет для ваших DTO

import java.time.LocalDateTime;

public class NewsDTO { // Или NewsSummaryDTO

    private Long id; // Поле id из сущности News

    private String mainImage; // Поле mainImage из сущности News

    private String title; // Поле title из сущности News

    private String description; // Поле description из сущности News

    private LocalDateTime publicationDate; // Поле publicationDate из сущности News

    private Long viewCount; // Поле viewCount из сущности News

    // --- Геттеры и сеттеры ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDateTime getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(LocalDateTime publicationDate) {
        this.publicationDate = publicationDate;
    }

    public Long getViewCount() {
        return viewCount;
    }

    public void setViewCount(Long viewCount) {
        this.viewCount = viewCount;
    }

}