package com.sarmo.listingservice.dto;

import java.time.LocalDateTime;

public class CommentDTO {
    private Long id;
    private String content;
    private Long parentId;
    private LocalDateTime createdAt;
    private boolean edited;
    private UserInfoDto author;

    public CommentDTO() {
    }

    public CommentDTO(Long id, String content, Long parentId, LocalDateTime createdAt, boolean edited, UserInfoDto author) {
        this.id = id;
        this.content = content;
        this.parentId = parentId;
        this.createdAt = createdAt;
        this.edited = edited;
        this.author = author;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isEdited() {
        return edited;
    }

    public void setEdited(boolean edited) {
        this.edited = edited;
    }

    public UserInfoDto getAuthor() {
        return author;
    }

    public void setAuthor(UserInfoDto author) {
        this.author = author;
    }
}