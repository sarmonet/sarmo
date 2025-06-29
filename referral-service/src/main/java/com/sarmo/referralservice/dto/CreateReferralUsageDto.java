package com.sarmo.referralservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateReferralUsageDto {

    @NotBlank(message = "Referral code cannot be blank")
    private String referralCodeValue;

    @NotNull(message = "Referred user ID cannot be null")
    private Long referredUserId;

    public CreateReferralUsageDto() {
    }

    public CreateReferralUsageDto(String referralCodeValue, Long referredUserId) {
        this.referralCodeValue = referralCodeValue;
        this.referredUserId = referredUserId;
    }

    public String getReferralCodeValue() {
        return referralCodeValue;
    }

    public void setReferralCodeValue(String referralCodeValue) {
        this.referralCodeValue = referralCodeValue;
    }

    public Long getReferredUserId() {
        return referredUserId;
    }

    public void setReferredUserId(Long referredUserId) {
        this.referredUserId = referredUserId;
    }
}