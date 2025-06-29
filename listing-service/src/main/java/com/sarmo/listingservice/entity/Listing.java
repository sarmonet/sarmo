package com.sarmo.listingservice.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sarmo.listingservice.enums.ListingStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "listings")
public class Listing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false, unique = true)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @Size(max = 255)
    @Column(nullable = false)
    private String title;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne
    @JoinColumn(name = "sub_category_id")
    private SubCategory subCategory;

    @NotNull
    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    private BigDecimal price;

    private String mainImage;

    @ElementCollection
    @CollectionTable(name = "listing_images", joinColumns = @JoinColumn(name = "listing_id"))
    @Column(name = "image_url")
    private List<String> images;

    private String videoUrl;

    @NotNull
    @Column(nullable = false)
    private String country;

    private String city;

    private String fullAddress;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING) // Указываем, как хранить Enum в базе данных (как строку)
    private ListingStatus status = ListingStatus.INACTIVE;

    @OneToOne(mappedBy = "listing", cascade = CascadeType.ALL, orphanRemoval = true)
    private PremiumSubscription premiumSubscription;

    @OneToMany(mappedBy = "listing", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "listing", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Rating> ratings = new ArrayList<>();

    @Column(nullable = false)
    private Long viewCount;

    @Column(nullable = false)
    private Boolean invest = false;

    public Listing() {
    }

    public Listing(User user, String title, Category category, SubCategory subCategory, String description, BigDecimal price, String mainImage, List<String> images, String videoUrl, String country, String city, String fullAddress, LocalDateTime createdAt, LocalDateTime updatedAt, ListingStatus status, PremiumSubscription premiumSubscription, List<Comment> comments, List<Rating> ratings, Long viewCount, Boolean invest) {
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
        this.premiumSubscription = premiumSubscription;
        this.comments = comments;
        this.ratings = ratings;
        this.viewCount = viewCount;
        this.invest = invest;
    }

    @PrePersist
    public void prePersist() {
        if (invest == null) {
            invest = true;
        }
        if (status == null) {
            status = ListingStatus.INACTIVE;
        }
        if (viewCount == null) {
            viewCount = 0L;
        }
    }

    public Double getAverageRating() {
        if (ratings == null || ratings.isEmpty()) {
            return 0.0;
        }

        return ratings.stream()
                .mapToInt(Rating::getValue)
                .average()
                .orElse(0.0);
    }

    public Long getTotalRatings() {
        if (ratings == null) {
            return 0L;
        }
        return (long) ratings.size();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
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

    public PremiumSubscription getPremiumSubscription() {
        return premiumSubscription;
    }

    public void setPremiumSubscription(PremiumSubscription premiumSubscription) {
        this.premiumSubscription = premiumSubscription;
    }

    public List<Rating> getRatings() {
        return ratings;
    }

    public void setRatings(List<Rating> ratings) {
        this.ratings = ratings;
    }

    public String getMainImage() {
        return mainImage;
    }

    public void setMainImage(String mainImage) {
        this.mainImage = mainImage;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public Long getViewCount() {
        return viewCount;
    }

    public void setViewCount(Long viewCount) {
        this.viewCount = viewCount;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public Boolean getInvest() {
        return invest;
    }

    public void setInvest(Boolean invest) {
        this.invest = invest;
    }
}