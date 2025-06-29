package com.sarmo.contentservice.dto;

public class NewsCommentCreateDTO {
    private Long newsId;
    private String text;
    private Long parentCommentId;

    public NewsCommentCreateDTO() {
    }

    public NewsCommentCreateDTO(Long newsId, String text, Long parentCommentId) {
        this.newsId = newsId;
        this.text = text;
        this.parentCommentId = parentCommentId;
    }

    public Long getNewsId() {
        return newsId;
    }

    public void setNewsId(Long newsId) {
        this.newsId = newsId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getParentCommentId() {
        return parentCommentId;
    }

    public void setParentCommentId(Long parentCommentId) {
        this.parentCommentId = parentCommentId;
    }
}
