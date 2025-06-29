package com.sarmo.listingservice.dto;

import com.sarmo.listingservice.entity.Category;
import com.sarmo.listingservice.entity.SubCategory;
import com.sarmo.listingservice.enums.ListingStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ListingDto {
    private Long id;
    private String title;
    private Category category;
    private SubCategory subCategory;
    private BigDecimal price;
    private String country;
    private String city;
    private LocalDateTime createdAt;
    private ListingStatus status;
    private Boolean premiumSubscription;
    private Double averageRating;
    private String mainImage;
    private Long viewCount;
    private Boolean invest; // Добавляем поле invest

    // Конструктор без аргументов
    public ListingDto() {}

    public ListingDto(Long id, String title, Category category, SubCategory subCategory, BigDecimal price, String country, String city, LocalDateTime createdAt, ListingStatus status, Boolean premiumSubscription, Double averageRating, String mainImage, Long viewCount, Boolean invest) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.subCategory = subCategory;
        this.price = price;
        this.country = country;
        this.city = city;
        this.createdAt = createdAt;
        this.status = status;
        this.premiumSubscription = premiumSubscription;
        this.averageRating = averageRating;
        this.mainImage = mainImage;
        this.viewCount = viewCount;
        this.invest = invest;
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SubCategory getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(SubCategory subCategory) {
        this.subCategory = subCategory;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ListingStatus getStatus() {
        return status;
    }

    public void setStatus(ListingStatus status) {
        this.status = status;
    }

    public Boolean getPremiumSubscription() {
        return premiumSubscription;
    }

    public void setPremiumSubscription(Boolean premiumSubscription) {
        this.premiumSubscription = premiumSubscription;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public String getMainImage() {
        return mainImage;
    }

    public void setMainImage(String mainImage) {
        this.mainImage = mainImage;
    }

    public Long getViewCount() {
        return viewCount;
    }

    public void setViewCount(Long viewCount) {
        this.viewCount = viewCount;
    }

    public Boolean getInvest() { // Добавляем геттер для invest
        return invest;
    }

    public void setInvest(Boolean invest) { // Добавляем сеттер для invest
        this.invest = invest;
    }
}