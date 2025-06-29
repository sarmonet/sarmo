package com.sarmo.contentservice.dto;

import java.time.LocalDateTime;

public class ArticleDTO {

    private Long id;

    private String title;

    private String description; // Используйте имя поля из вашей сущности Article для основного текста

    private String mainImage;

    private LocalDateTime publicationDate; // Используйте имя поля даты из вашей сущности Article

    private Long viewCount; // Используйте имя поля счетчика просмотров из вашей сущности Article

    private String authorName; // Имя автора статьи (если нужно включать информацию об авторе)

    // --- Геттеры и сеттеры ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getMainImage() {
        return mainImage;
    }

    public void setMainImage(String mainImage) {
        this.mainImage = mainImage;
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

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }
}