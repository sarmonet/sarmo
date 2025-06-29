package com.sarmo.listingservice.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Document(collection = "listings")
public class ListingMongo {

    @Id
    private String id;
    private Long listingId;
    private Long categoryId;
    private Map<String, Object> fields;

    public ListingMongo(){}

    public ListingMongo(String id, Long listingId, Long categoryId, Map<String, Object> fields) {
        this.id = id;
        this.listingId = listingId;
        this.categoryId = categoryId;
        this.fields = fields;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getListingId() {
        return listingId;
    }

    public void setListingId(Long listingId) {
        this.listingId = listingId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Map<String, Object> getFields() {
        return fields;
    }

    public void setFields(Map<String, Object> fields) {
        this.fields = fields;
    }
}