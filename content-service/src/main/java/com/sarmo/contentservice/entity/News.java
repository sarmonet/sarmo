package com.sarmo.contentservice.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "news")
public class News {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // Many-to-One relationship with the User entity (the author)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false) // Join column in the 'news' table
    private User author; // Navigation field for the User relationship

    @Column(nullable = false)
    private String mainImage;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(name = "content_id", nullable = false)
    private String contentId;

    @CreationTimestamp
    @Column(name = "publication_date", updatable = false)
    private LocalDateTime publicationDate;

    @Column(name = "view_count", nullable = false, columnDefinition = "bigint default 0")
    private Long viewCount;


    public News() {
        this.viewCount = 0L;
    }

    // Constructor accepting User object for the author
    public News(User author, String mainImage, String title, String description, String contentId, LocalDateTime publicationDate, Long viewCount) {
        this.author = author;
        this.mainImage = mainImage;
        this.title = title;
        this.description = description;
        this.contentId = contentId;
        this.publicationDate = publicationDate;
        this.viewCount = viewCount != null ? viewCount : 0L;
    }

    // Constructor accepting userId for the author (less preferred for JPA, but possible)
    public News(String mainImage, String title, String description, String contentId, LocalDateTime publicationDate, Long viewCount) {
        this.mainImage = mainImage;
        this.title = title;
        this.description = description;
        this.contentId = contentId;
        this.publicationDate = publicationDate;
        this.viewCount = viewCount != null ? viewCount : 0L;
    }

    public News(Long id, String mainImage, String title, String description, String contentId, LocalDateTime publicationDate, Long viewCount) {
        this.id = id;
        this.mainImage = mainImage;
        this.title = title;
        this.description = description;
        this.contentId = contentId;
        this.publicationDate = publicationDate;
        this.viewCount = viewCount != null ? viewCount : 0L;
    }


    // --- Getters and Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getAuthor() {
        return author;
    }
    public void setAuthor(User author) {
        this.author = author;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContentId() { return contentId; }
    public void setContentId(String contentId) { this.contentId = contentId; }

    public LocalDateTime getPublicationDate() { return publicationDate; }
    public void setPublicationDate(LocalDateTime publicationDate) { this.publicationDate = publicationDate; }

    public Long getViewCount() { return viewCount; }
    public void setViewCount(Long viewCount) { this.viewCount = viewCount; }

    public String getMainImage() { return mainImage; }
    public void setMainImage(String mainImage) { this.mainImage = mainImage; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}