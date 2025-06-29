package com.sarmo.listingservice.dto;

public class CreateCommentDto {
    private Long listingId;
    private String content;
    private Long parentCommentId;

    public CreateCommentDto() {
    }

    public CreateCommentDto(Long listingId, String content, Long parentCommentId) {
        this.listingId = listingId;
        this.content = content;
        this.parentCommentId = parentCommentId;
    }

    public Long getListingId() {
        return listingId;
    }

    public void setListingId(Long listingId) {
        this.listingId = listingId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getParentCommentId() {
        return parentCommentId;
    }

    public void setParentCommentId(Long parentCommentId) {
        this.parentCommentId = parentCommentId;
    }
}
