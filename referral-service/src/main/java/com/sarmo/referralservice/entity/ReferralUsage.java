package com.sarmo.referralservice.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "referral_usage")
public class ReferralUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "referral_code_id", nullable = false)
    private ReferralCode referralCode;

    @ManyToOne
    @JoinColumn(name = "referred_user_id", referencedColumnName = "user_id", nullable = false)
    private User referredUser;

    @Column(nullable = false, updatable = false)
    private LocalDateTime usageDate;

    public ReferralUsage() {
        this.usageDate = LocalDateTime.now();
    }

    public ReferralUsage(ReferralCode referralCode, User referredUser) {
        this.referralCode = referralCode;
        this.referredUser = referredUser;
        this.usageDate = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ReferralCode getReferralCode() {
        return referralCode;
    }

    public void setReferralCode(ReferralCode referralCode) {
        this.referralCode = referralCode;
    }

    public User getReferredUser() {
        return referredUser;
    }

    public void setReferredUser(User referredUser) {
        this.referredUser = referredUser;
    }

    public LocalDateTime getUsageDate() {
        return usageDate;
    }

    public void setUsageDate(LocalDateTime usageDate) {
        this.usageDate = usageDate;
    }
}