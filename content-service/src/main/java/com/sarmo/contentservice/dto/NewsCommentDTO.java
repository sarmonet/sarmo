package com.sarmo.contentservice.dto;

import com.sarmo.contentservice.entity.User;

import java.time.LocalDateTime;

public class NewsCommentDTO {
    private Long id;

    private String text;

    private LocalDateTime creationDate;

    private Long parentCommentId;

    private User author;

    public NewsCommentDTO() {
    }

    public NewsCommentDTO(Long id, String text, LocalDateTime creationDate, Long parentCommentId, User author) {
        this.id = id;
        this.text = text;
        this.creationDate = creationDate;
        this.parentCommentId = parentCommentId;
        this.author = author;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public Long getParentCommentId() {
        return parentCommentId;
    }

    public void setParentCommentId(Long parentCommentId) {
        this.parentCommentId = parentCommentId;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }
}
