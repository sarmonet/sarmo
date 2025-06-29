package com.sarmo.chatservice.entity;

import com.sarmo.chatservice.enums.AttachmentType;
import java.util.Objects;

public class Attachment {
    private String url;
    private AttachmentType type;

    public Attachment() {}

    public Attachment(String url, AttachmentType type) {
        this.url = url;
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public AttachmentType getType() {
        return type;
    }

    public void setType(AttachmentType type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Attachment that = (Attachment) o;
        return Objects.equals(url, that.url) && Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, type);
    }

    @Override
    public String toString() {
        return "Attachment{" +
                "url='" + url + '\'' +
                ", type=" + type +
                '}';
    }
}