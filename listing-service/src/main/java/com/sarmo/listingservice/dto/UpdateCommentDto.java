package com.sarmo.listingservice.dto;

public class UpdateCommentDto {
    private String content;

    public UpdateCommentDto() {
    }

    public UpdateCommentDto(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
