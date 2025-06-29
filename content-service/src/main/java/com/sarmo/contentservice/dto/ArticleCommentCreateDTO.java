package com.sarmo.contentservice.dto;

public class ArticleCommentCreateDTO {
    private Long articleId;
    private String text;
    private Long parentCommentId;

    public ArticleCommentCreateDTO() {}

    public ArticleCommentCreateDTO(Long articleId, String text, Long parentCommentId) {
        this.articleId = articleId;
        this.text = text;
        this.parentCommentId = parentCommentId;
    }

    public Long getArticleId() {
        return articleId;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
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
