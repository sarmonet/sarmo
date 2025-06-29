package com.sarmo.contentservice.entity;

public class ContentItem {
    private String type;
    private String title;
    private String value;

    public ContentItem() {}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ContentItem(String type, String value) {}

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}