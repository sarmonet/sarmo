package com.sarmo.referralservice.dto;

import com.sarmo.referralservice.enums.RewardCondition;
import com.sarmo.referralservice.enums.RewardType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class CreateReferralRewardDto {

    @NotNull(message = "Referrer ID cannot be null")
    private Long referrerId;

    @NotNull(message = "Referred ID cannot be null")
    private Long referredId;

    @NotNull(message = "Reward type cannot be null")
    private RewardType rewardType;

    @NotNull(message = "Reward amount cannot be null")
    @Positive(message = "Reward amount must be positive")
    private BigDecimal rewardAmount;

    private RewardCondition rewardCondition;

    public CreateReferralRewardDto() {
    }

    public CreateReferralRewardDto(Long referrerId, Long referredId, RewardType rewardType, BigDecimal rewardAmount, RewardCondition rewardCondition) {
        this.referrerId = referrerId;
        this.referredId = referredId;
        this.rewardType = rewardType;
        this.rewardAmount = rewardAmount;
        this.rewardCondition = rewardCondition;
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

    public RewardCondition getRewardCondition() {
        return rewardCondition;
    }

    public void setRewardCondition(RewardCondition rewardCondition) {
        this.rewardCondition = rewardCondition;
    }
}