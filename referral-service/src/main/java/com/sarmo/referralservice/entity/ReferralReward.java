package com.sarmo.referralservice.entity;

import com.sarmo.referralservice.enums.RewardCondition;
import com.sarmo.referralservice.enums.RewardType;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "referral_rewards")
public class ReferralReward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "referrer_id", referencedColumnName = "user_id", nullable = false)
    private User referrer; // Связь с сущностью User (кто пригласил)

    @ManyToOne
    @JoinColumn(name = "referred_user_id", referencedColumnName = "user_id", nullable = false)
    private User referred; // Связь с сущностью User (кто был приглашен)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RewardType rewardType;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal rewardAmount;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime rewardDate;

    @Enumerated(EnumType.STRING)
    private RewardCondition rewardCondition;

    public ReferralReward() {
        this.rewardDate = LocalDateTime.now();
    }

    public ReferralReward(Long id, User referrer, User referred, RewardType rewardType, BigDecimal rewardAmount, LocalDateTime rewardDate, RewardCondition rewardCondition) {
        this.id = id;
        this.referrer = referrer;
        this.referred = referred;
        this.rewardType = rewardType;
        this.rewardAmount = rewardAmount;
        this.rewardDate = rewardDate;
        this.rewardCondition = rewardCondition;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getReferrer() {
        return referrer;
    }

    public void setReferrer(User referrer) {
        this.referrer = referrer;
    }

    public User getReferred() {
        return referred;
    }

    public void setReferred(User referred) {
        this.referred = referred;
    }

    public BigDecimal getRewardAmount() {
        return rewardAmount;
    }

    public void setRewardAmount(BigDecimal rewardAmount) {
        this.rewardAmount = rewardAmount;
    }

    public LocalDateTime getRewardDate() {
        return rewardDate;
    }

    public void setRewardDate(LocalDateTime rewardDate) {
        this.rewardDate = rewardDate;
    }

    public RewardType getRewardType() {
        return rewardType;
    }

    public void setRewardType(RewardType rewardType) {
        this.rewardType = rewardType;
    }

    public RewardCondition getRewardCondition() {
        return rewardCondition;
    }

    public void setRewardCondition(RewardCondition rewardCondition) {
        this.rewardCondition = rewardCondition;
    }
}