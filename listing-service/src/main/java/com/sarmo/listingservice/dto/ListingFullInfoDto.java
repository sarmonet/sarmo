package com.sarmo.listingservice.dto;

import com.sarmo.listingservice.entity.*;
import com.sarmo.listingservice.enums.ListingStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class ListingFullInfoDto {
    private Long id;
    private UserInfoDto user;
    private String title;
    private Category category;
    private SubCategory subCategory;
    private String description;
    private BigDecimal price;
    private String mainImage;
    private List<String> images;
    private String videoUrl;
    private String country;
    private String city;
    private String fullAddress;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private ListingStatus status;
    private Boolean invest;
    private PremiumSubscription premiumSubscription;
    private Double averageRating;
    private Long totalRatings;
    private Long viewCount;
    private Map<String, Object> fields;
    private  List<CommentDTO> comments;
    private List<ListingDto> similarListings;

    public ListingFullInfoDto(){}

    public ListingFullInfoDto(Long id, UserInfoDto user, String title, Category category, SubCategory subCategory, String description, BigDecimal price, String mainImage, List<String> images, String videoUrl, String country, String city, String fullAddress, LocalDateTime createdAt, LocalDateTime updatedAt, ListingStatus status, Boolean invest, PremiumSubscription premiumSubscription, Double averageRating, Long totalRatings, Long viewCount, Map<String, Object> fields, List<CommentDTO> comments, List<ListingDto> similarListings) {
        this.id = id;
        this.user = user;
        this.title = title;
        this.category = category;
        this.subCategory = subCategory;
        this.description = description;
        this.price = price;
        this.mainImage = mainImage;
        this.images = images;
        this.videoUrl = videoUrl;
        this.country = country;
        this.city = city;
        this.fullAddress = fullAddress;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.status = status;
        this.invest = invest;
        this.premiumSubscription = premiumSubscription;
        this.averageRating = averageRating;
        this.totalRatings = totalRatings;
        this.viewCount = viewCount;
        this.fields = fields;
        this.comments = comments;
        this.similarListings = similarListings;
    }

    public ListingFullInfoDto(Listing listing) {
        mapFromListing(listing);
    }

    public ListingFullInfoDto(Listing listing, Map<String, Object> fields, List<ListingDto> similarListings) {
        mapFromListing(listing);
        this.fields = fields;
        this.similarListings = similarListings;
    }

    public ListingFullInfoDto(Listing listing, Map<String, Object> fields) {
        mapFromListing(listing);
        this.fields = fields;
    }

    private UserInfoDto mapToUserInfoDto(User user) {
        if (user == null) {
            return null;
        }
        return new UserInfoDto(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getPhoneNumber(), user.getProfileImageUrl());
    }

    private void mapFromListing(Listing listing) {
        this.id = listing.getId();
        this.user = mapToUserInfoDto(listing.getUser());
        this.title = listing.getTitle();
        this.category = listing.getCategory();
        this.subCategory = listing.getSubCategory();
        this.description = listing.getDescription();
        this.price = listing.getPrice();
        this.mainImage = listing.getMainImage();
        this.images = listing.getImages();
        this.videoUrl = listing.getVideoUrl();
        this.country = listing.getCountry();
        this.city = listing.getCity();
        this.fullAddress = listing.getFullAddress();
        this.createdAt = listing.getCreatedAt();
        this.updatedAt = listing.getUpdatedAt();
        this.status = listing.getStatus();
        this.premiumSubscription = listing.getPremiumSubscription();
        this.averageRating = listing.getAverageRating();
        this.totalRatings = listing.getTotalRatings();
        this.viewCount = listing.getViewCount();
        this.invest = listing.getInvest();
    }

    public Boolean getInvest() {
        return invest;
    }

    public void setInvest(Boolean invest) {
        this.invest = invest;
    }

    public List<CommentDTO> getComments() {
        return comments;
    }

    public void setComments(List<CommentDTO> comments) {
        this.comments = comments;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public SubCategory getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(SubCategory subCategory) {
        this.subCategory = subCategory;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public ListingStatus getStatus() {
        return status;
    }

    public void setStatus(ListingStatus status) {
        this.status = status;
    }

    public PremiumSubscription getPremiumSubscription() {
        return premiumSubscription;
    }

    public void setPremiumSubscription(PremiumSubscription premiumSubscription) {
        this.premiumSubscription = premiumSubscription;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public UserInfoDto getUser() {
        return user;
    }

    public void setUser(UserInfoDto user) {
        this.user = user;
    }

    public Long getTotalRatings() {
        return totalRatings;
    }

    public void setTotalRatings(Long totalRatings) {
        this.totalRatings = totalRatings;
    }

    public Map<String, Object> getFields() {
        return fields;
    }

    public void setFields(Map<String, Object> fields) {
        this.fields = fields;
    }

    public Long getViewCount() {
        return viewCount;
    }

    public void setViewCount(Long viewCount) {
        this.viewCount = viewCount;
    }

    public List<ListingDto> getSimilarListings() {
        return similarListings;
    }

    public void setSimilarListings(List<ListingDto> similarListings) {
        this.similarListings = similarListings;
    }

}