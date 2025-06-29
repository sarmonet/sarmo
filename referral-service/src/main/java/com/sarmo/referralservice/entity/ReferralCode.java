package com.sarmo.referralservice.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "referral_codes")
public class ReferralCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", unique = true, nullable = false)
    private User user; // Связь с сущностью User (владелец кода)

    @Column(nullable = false, updatable = false)
    private LocalDateTime creationDate;

    public ReferralCode() {
        this.creationDate = LocalDateTime.now();
    }

    public ReferralCode(String code, User user) {
        this.code = code;
        this.user = user;
        this.creationDate = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }
}