package com.sarmo.authservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "pending_user_registrations")
public class PendingUserRegistration {

    @Id
    private UUID verificationId;

    private String firstName;
    private String lastName;
    private String contact;
    private String hashedPassword;
    private String referralCode;
    private LocalDateTime creationTime;
    private LocalDateTime expirationTime;

    public PendingUserRegistration() {
    }

    public PendingUserRegistration(UUID verificationId, String firstName, String lastName, String contact, String hashedPassword, String referralCode, LocalDateTime creationTime, LocalDateTime expirationTime) {
        this.verificationId = verificationId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.contact = contact;
        this.hashedPassword = hashedPassword;
        this.referralCode = referralCode;
        this.creationTime = creationTime;
        this.expirationTime = expirationTime;
    }

    // --- Геттеры и Сеттеры ---
    public UUID getVerificationId() {
        return verificationId;
    }

    public void setVerificationId(UUID verificationId) {
        this.verificationId = verificationId;
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

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public String getReferralCode() {
        return referralCode;
    }

    public void setReferralCode(String referralCode) {
        this.referralCode = referralCode;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }

    public LocalDateTime getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(LocalDateTime expirationTime) {
        this.expirationTime = expirationTime;
    }
}