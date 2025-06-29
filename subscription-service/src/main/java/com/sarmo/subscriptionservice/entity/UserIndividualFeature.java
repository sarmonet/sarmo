package com.sarmo.subscriptionservice.entity;

import com.sarmo.subscriptionservice.enums.UserIndividualFeatureStatus;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "user_individual_features")
public class UserIndividualFeature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "feature_id", nullable = false)
    private IndividualFeature individualFeature;

    @Column(nullable = false)
    private LocalDate purchaseDate;

    private LocalDate expirationDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserIndividualFeatureStatus status;

    private String additionalInfo;

    public UserIndividualFeature() {
    }

    public UserIndividualFeature(User user, IndividualFeature individualFeature, LocalDate purchaseDate, LocalDate expirationDate, UserIndividualFeatureStatus status, String additionalInfo) {
        this.user = user;
        this.individualFeature = individualFeature;
        this.purchaseDate = purchaseDate;
        this.expirationDate = expirationDate;
        this.status = status;
        this.additionalInfo = additionalInfo;
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

    public IndividualFeature getIndividualFeature() {
        return individualFeature;
    }

    public void setIndividualFeature(IndividualFeature individualFeature) {
        this.individualFeature = individualFeature;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public UserIndividualFeatureStatus getStatus() {
        return status;
    }

    public void setStatus(UserIndividualFeatureStatus status) {
        this.status = status;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }
}