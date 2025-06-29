package com.sarmo.chatservice.dto;

import com.sarmo.chatservice.enums.AttachmentType;
import java.util.Objects;

public class AttachmentDTO {
    private String url;
    private AttachmentType type;

    public AttachmentDTO() {
    }

    public AttachmentDTO(String url, AttachmentType type) {
        this.url = url;
        this.type = type;
    }

    // Getters
    public String getUrl() {
        return url;
    }

    public AttachmentType getType() {
        return type;
    }

    // Setters
    public void setUrl(String url) {
        this.url = url;
    }

    public void setType(AttachmentType type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AttachmentDTO that = (AttachmentDTO) o;
        return Objects.equals(url, that.url) && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, type);
    }

    @Override
    public String toString() {
        return "AttachmentDTO{" +
                "url='" + url + '\'' +
                ", type=" + type +
                '}';
    }

    // Builder pattern (optional)
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String url;
        private AttachmentType type;

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder type(AttachmentType type) {
            this.type = type;
            return this;
        }

        public AttachmentDTO build() {
            return new AttachmentDTO(url, type);
        }
    }
}