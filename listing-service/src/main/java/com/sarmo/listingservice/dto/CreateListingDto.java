package com.sarmo.listingservice.dto;

import com.sarmo.listingservice.enums.ListingStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class CreateListingDto {
    private String title;
    private Long categoryId;
    private Long subCategoryId;
    private String description;
    private BigDecimal price;
    private String mainImage;
    private List<String> images;
    private String videoUrl;
    private String country;
    private String city;
    private String fullAddress;
    private ListingStatus status;
    private LocalDateTime premiumStartDate;
    private LocalDateTime premiumEndDate;
    private Boolean invest;
    private Map<String, Object> fields;

    public CreateListingDto(){}

    public CreateListingDto(String title, Long categoryId, Long subCategoryId, String description, BigDecimal price, String mainImage, List<String> images, String videoUrl, String country, String city, String fullAddress, ListingStatus status, LocalDateTime premiumStartDate, LocalDateTime premiumEndDate, Boolean invest, Map<String, Object> fields) {
        this.title = title;
        this.categoryId = categoryId;
        this.subCategoryId = subCategoryId;
        this.description = description;
        this.price = price;
        this.mainImage = mainImage;
        this.images = images;
        this.videoUrl = videoUrl;
        this.country = country;
        this.city = city;
        this.fullAddress = fullAddress;
        this.status = status;
        this.premiumStartDate = premiumStartDate;
        this.premiumEndDate = premiumEndDate;
        this.invest = invest;
        this.fields = fields;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getSubCategoryId() {
        return subCategoryId;
    }

    public void setSubCategoryId(Long subCategoryId) {
        this.subCategoryId = subCategoryId;
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

    public LocalDateTime getPremiumStartDate() {
        return premiumStartDate;
    }

    public void setPremiumStartDate(LocalDateTime premiumStartDate) {
        this.premiumStartDate = premiumStartDate;
    }

    public LocalDateTime getPremiumEndDate() {
        return premiumEndDate;
    }

    public void setPremiumEndDate(LocalDateTime premiumEndDate) {
        this.premiumEndDate = premiumEndDate;
    }

    public Map<String, Object> getFields() {
        return fields;
    }

    public void setFields(Map<String, Object> fields) {
        this.fields = fields;
    }

    public Boolean getInvest() {
        return invest;
    }

    public void setInvest(Boolean invest) {
        this.invest = invest;
    }
}