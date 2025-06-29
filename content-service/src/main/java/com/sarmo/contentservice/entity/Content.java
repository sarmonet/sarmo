package com.sarmo.contentservice.entity;

import jakarta.persistence.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "content")
public class Content {
    @Id
    private String id;
    private List<ContentItem> content;

    public Content(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<ContentItem> getContent() {
        return content;
    }

    public void setContent(List<ContentItem> content) {
        if (content != null && content.size() > 8) {
            throw new IllegalArgumentException("The content list cannot contain more than 8 items.");
        }
        this.content = content;
    }
}