//package com.sarmo.userservice.dto;
//
//import com.sarmo.listingservice.entity.Category;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//
//public class ListingDto {
//    private Long id;
//    private String title;
//    private Category category;
//    private BigDecimal price;
//    private String country;
//    private String city;
//    private LocalDateTime createdAt;
//    private Boolean isActive;
//    private Boolean premiumSubscription;
//    private Double averageRating;
//    private String mainImage;
//    private Long viewCount;
//
//    // Конструктор без аргументов
//    public ListingDto() {}
//
//    public ListingDto(Long id, String title, Category category, BigDecimal price, String country, String city, LocalDateTime createdAt, Boolean isActive, Boolean premiumSubscription, Double averageRating, String mainImage, Long viewCount) {
//        this.id = id;
//        this.title = title;
//        this.category = category;
//        this.price = price;
//        this.country = country;
//        this.city = city;
//        this.createdAt = createdAt;
//        this.isActive = isActive;
//        this.premiumSubscription = premiumSubscription;
//        this.averageRating = averageRating;
//        this.mainImage = mainImage;
//        this.viewCount = viewCount;
//    }
//
//    // Геттеры и сеттеры
//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public String getTitle() {
//        return title;
//    }
//
//    public void setTitle(String title) {
//        this.title = title;
//    }
//
//    public Category getCategory() {
//        return category;
//    }
//
//    public void setCategory(Category category) {
//        this.category = category;
//    }
//
//    public BigDecimal getPrice() {
//        return price;
//    }
//
//    public void setPrice(BigDecimal price) {
//        this.price = price;
//    }
//
//    public String getCountry() {
//        return country;
//    }
//
//    public void setCountry(String country) {
//        this.country = country;
//    }
//
//    public LocalDateTime getCreatedAt() {
//        return createdAt;
//    }
//
//    public void setCreatedAt(LocalDateTime createdAt) {
//        this.createdAt = createdAt;
//    }
//
//    public Boolean getActive() {
//        return isActive;
//    }
//
//    public void setActive(Boolean active) {
//        isActive = active;
//    }
//
//    public Boolean getPremiumSubscription() {
//        return premiumSubscription;
//    }
//
//    public void setPremiumSubscription(Boolean premiumSubscription) {
//        this.premiumSubscription = premiumSubscription;
//    }
//
//    public Double getAverageRating() {
//        return averageRating;
//    }
//
//    public void setAverageRating(Double averageRating) {
//        this.averageRating = averageRating;
//    }
//
//    public String getMainImage() {
//        return mainImage;
//    }
//
//    public void setMainImage(String mainImage) {
//        this.mainImage = mainImage;
//    }
//
//    public String getCity() {
//        return city;
//    }
//
//    public void setCity(String city) {
//        this.city = city;
//    }
//
//    public Long getViewCount() {
//        return viewCount;
//    }
//
//    public void setViewCount(Long viewCount) {
//        this.viewCount = viewCount;
//    }
//}