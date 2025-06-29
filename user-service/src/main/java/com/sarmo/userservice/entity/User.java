package com.sarmo.userservice.entity;

import com.sarmo.userservice.enums.AccountStatus;
import com.sarmo.userservice.enums.UserStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @NotNull
    @Column(nullable = false, unique = true)
    private Long id;

    private String profilePictureUrl;

    @NotNull
    @Size(min = 2, max = 50)
    @Column(nullable = false, length = 50)
    private String firstName;

    @NotNull
    @Size(min = 2, max = 50)
    @Column(nullable = false, length = 50)
    private String lastName;

    @Email
    @Column(unique = true)
    private String email;

    @Column(unique = true)
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Phone number must contain between 10 and 15 digits and can start with '+'")
    private String phoneNumber;

    private LocalDateTime birthDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserStatus userStatus = UserStatus.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AccountStatus accountStatus = AccountStatus.DEFAULT;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

    private String country;

    private String city;

    private String fullAddress;

    private String notificationSettingsId; // Добавлено поле

    private String userSettingsId;

    @ElementCollection
    @CollectionTable(name = "user_documents", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "document_url")
    private List<String> documents;

    @OneToMany(mappedBy = "ratedUser")
    private List<UserRating> ratings;

    @OneToMany(mappedBy = "userId")
    private List<TransactionSupport> transactionSupports;

    @OneToMany(mappedBy = "userId")
    private List<UserFavoriteListing> favoriteListings;

    public User() {
    }

    public User(Long id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public User(Long id, String profilePictureUrl, String firstName, String lastName, String email, String phoneNumber, LocalDateTime birthDate, UserStatus userStatus, AccountStatus accountStatus, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt, String country, String city, String fullAddress, String notificationSettingsId, String userSettingsId, List<String> documents, List<UserRating> ratings, List<TransactionSupport> transactionSupports, List<UserFavoriteListing> favoriteListings) {
        this.id = id;
        this.profilePictureUrl = profilePictureUrl;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.birthDate = birthDate;
        this.userStatus = userStatus;
        this.accountStatus = accountStatus;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
        this.country = country;
        this.city = city;
        this.fullAddress = fullAddress;
        this.notificationSettingsId = notificationSettingsId;
        this.userSettingsId = userSettingsId;
        this.documents = documents;
        this.ratings = ratings;
        this.transactionSupports = transactionSupports;
        this.favoriteListings = favoriteListings;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LocalDateTime getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDateTime birthDate) {
        this.birthDate = birthDate;
    }

    public UserStatus getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(UserStatus userStatus) {
        this.userStatus = userStatus;
    }

    public AccountStatus getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(AccountStatus accountStatus) {
        this.accountStatus = accountStatus;
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

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
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

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public String getNotificationSettingsId() {
        return notificationSettingsId;
    }

    public void setNotificationSettingsId(String notificationSettingsId) {
        this.notificationSettingsId = notificationSettingsId;
    }

    public String getUserSettingsId() {
        return userSettingsId;
    }

    public void setUserSettingsId(String userSettingsId) {
        this.userSettingsId = userSettingsId;
    }

    public List<String> getDocuments() {
        return documents;
    }

    public void setDocuments(List<String> documents) {
        this.documents = documents;
    }

    public List<TransactionSupport> getTransactionSupports() {
        return transactionSupports;
    }

    public void setTransactionSupports(List<TransactionSupport> transactionSupports) {
        this.transactionSupports = transactionSupports;
    }

    public List<UserFavoriteListing> getFavoriteListings() {
        return favoriteListings;
    }

    public void setFavoriteListings(List<UserFavoriteListing> favoriteListings) {
        this.favoriteListings = favoriteListings;
    }

    public List<UserRating> getRatings() {
        return ratings;
    }

    public void setRatings(List<UserRating> ratings) {
        this.ratings = ratings;
    }
}