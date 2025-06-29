package com.sarmo.authservice.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "two_factor_codes")
public class TwoFactorCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "verification_id", unique = true, nullable = false)
    private UUID verificationId;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "email_or_phone_number", nullable = false)
    private String emailOrPhoneNumber;

    @Column(name = "creation_time", nullable = false)
    private LocalDateTime creationTime;

    @Column(name = "expiration_time", nullable = false)
    private LocalDateTime expirationTime;


    public TwoFactorCode(){}

    public TwoFactorCode(UUID verificationId, String code, String emailOrPhoneNumber, LocalDateTime creationTime, LocalDateTime expirationTime) {
        this.verificationId = verificationId;
        this.code = code;
        this.emailOrPhoneNumber = emailOrPhoneNumber;
        this.creationTime = creationTime;
        this.expirationTime = expirationTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getVerificationId() {
        return verificationId;
    }

    public void setVerificationId(UUID verificationId) {
        this.verificationId = verificationId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getEmailOrPhoneNumber() {
        return emailOrPhoneNumber;
    }

    public void setEmailOrPhoneNumber(String emailOrPhoneNumber) {
        this.emailOrPhoneNumber = emailOrPhoneNumber;
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