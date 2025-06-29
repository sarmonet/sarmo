package com.sarmo.referralservice.dto;

import com.sarmo.referralservice.enums.RewardCondition;
import com.sarmo.referralservice.enums.RewardType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class UpdateReferralRewardDto {

    private RewardType rewardType;

    @Positive(message = "Reward amount must be positive")
    private BigDecimal rewardAmount;

    private RewardCondition rewardCondition;

    public UpdateReferralRewardDto() {
    }

    public UpdateReferralRewardDto(RewardType rewardType, BigDecimal rewardAmount, RewardCondition rewardCondition) {
        this.rewardType = rewardType;
        this.rewardAmount = rewardAmount;
        this.rewardCondition = rewardCondition;
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