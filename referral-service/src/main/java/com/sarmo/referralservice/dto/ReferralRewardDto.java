package com.sarmo.referralservice.dto;

import com.sarmo.referralservice.enums.RewardCondition;
import com.sarmo.referralservice.enums.RewardType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ReferralRewardDto {

    private Long id;
    private Long referrerId;
    private Long referredId;
    private RewardType rewardType;
    private BigDecimal rewardAmount;
    private LocalDateTime rewardDate;
    private RewardCondition rewardCondition;

    public ReferralRewardDto() {
    }

    public ReferralRewardDto(Long id, Long referrerId, Long referredId, RewardType rewardType, BigDecimal rewardAmount, LocalDateTime rewardDate, RewardCondition rewardCondition) {
        this.id = id;
        this.referrerId = referrerId;
        this.referredId = referredId;
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

    public Long getReferrerId() {
        return referrerId;
    }

    public void setReferrerId(Long referrerId) {
        this.referrerId = referrerId;
    }

    public Long getReferredId() {
        return referredId;
    }

    public void setReferredId(Long referredId) {
        this.referredId = referredId;
    }

    public RewardType getRewardType() {
        return rewardType;
    }

    public void setRewardType(RewardType rewardType) {
        this.rewardType = rewardType;
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

    public RewardCondition getRewardCondition() {
        return rewardCondition;
    }

    public void setRewardCondition(RewardCondition rewardCondition) {
        this.rewardCondition = rewardCondition;
    }
}