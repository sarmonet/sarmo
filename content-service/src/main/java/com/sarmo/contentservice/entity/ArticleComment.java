package com.sarmo.contentservice.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
public class ArticleComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // Many-to-One relationship with the Article entity
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false) // Join column in the 'comments' table
    private Article article; // Navigation field for the Article relationship

    // Many-to-One relationship with the User entity (the author)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false) // Join column in the 'comments' table
    private User author; // Navigation field for the User relationship

    @Column(name = "text", nullable = false)
    private String text;

    @CreationTimestamp
    @Column(name = "creation_date", updatable = false)
    private LocalDateTime creationDate;

    @Column(name = "parent_comment_id")
    private Long parentCommentId;


    public ArticleComment() {}

    // --- Constructors updated to accept relationship objects or IDs ---

    // Constructor accepting relationship objects
    public ArticleComment(Article article, User author, String text, LocalDateTime creationDate, Long parentCommentId) {
        this.article = article;
        this.author = author;
        this.text = text;
        this.creationDate = creationDate;
        this.parentCommentId = parentCommentId;
    }

    // Constructor accepting IDs (less preferred for JPA)
    public ArticleComment(String text, LocalDateTime creationDate, Long parentCommentId) {
        this.text = text;
        this.creationDate = creationDate;
        this.parentCommentId = parentCommentId;
        // JPA will use these IDs to establish relationships, but the 'article' and 'author' objects will be null until loaded.
    }

    // Simplified constructor
    public ArticleComment( String text) {
        this.text = text;
    }

    // Full constructor including ID
    public ArticleComment(Long id, String text, LocalDateTime creationDate, Long parentCommentId) {
        this.id = id;
        this.text = text;
        this.creationDate = creationDate;
        this.parentCommentId = parentCommentId;
    }


    // --- Getters and Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;

    }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public LocalDateTime getCreationDate() { return creationDate; }
    public void setCreationDate(LocalDateTime creationDate) { this.creationDate = creationDate; }

    public Long getParentCommentId() { return parentCommentId; }
    public void setParentCommentId(Long parentCommentId) { this.parentCommentId = parentCommentId; }

}