package com.sarmo.listingservice.dto;

import com.sarmo.listingservice.enums.ListingStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map; // Import for Map

public class UpdateListingDto {

    @Size(max = 255)
    private String title;

    @Size(max = 2048) // Adjust max size as needed
    private String description;

    private BigDecimal price;

    private String mainImage;

    private List<String> images;

    private String videoUrl;

    private String country;

    private String city;

    private String fullAddress;

    private ListingStatus status;

    private Boolean invest;

    // Add field for dynamic fields
    private Map<String, Object> fields;


    public UpdateListingDto() {
    }

    // Constructor including the fields map
    public UpdateListingDto(String title, String description, BigDecimal price, String mainImage, List<String> images, String videoUrl, @NotNull String country, String city, String fullAddress, ListingStatus status, Boolean invest, Map<String, Object> fields) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.mainImage = mainImage;
        this.images = images;
        this.videoUrl = videoUrl;
        this.country = country;
        this.city = city;
        this.fullAddress = fullAddress;
        this.status = status;
        this.invest = invest;
        this.fields = fields; // Initialize the new field
    }

    // Getters and setters for all fields

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getMainImage() {
        return mainImage;
    }

    public void setMainImage(String mainImage) {
        this.mainImage = mainImage;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public ListingStatus getStatus() {
        return status;
    }

    public void setStatus(ListingStatus status) {
        this.status = status;
    }

    public Boolean getInvest() {
        return invest;
    }

    public void setInvest(Boolean invest) {
        this.invest = invest;
    }

    // Getter and setter for the new fields map
    public Map<String, Object> getFields() {
        return fields;
    }

    public void setFields(Map<String, Object> fields) {
        this.fields = fields;
    }
}