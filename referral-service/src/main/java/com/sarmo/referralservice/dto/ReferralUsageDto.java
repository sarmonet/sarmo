package com.sarmo.referralservice.dto;

import java.time.LocalDateTime;

public class ReferralUsageDto {

    private Long id;
    private String referralCodeValue; // Значение использованного реферального кода
    private Long referredUserId;     // ID пользователя, который использовал код
    private LocalDateTime usageDate;

    public ReferralUsageDto() {
    }

    public ReferralUsageDto(Long id, String referralCodeValue, Long referredUserId, LocalDateTime usageDate) {
        this.id = id;
        this.referralCodeValue = referralCodeValue;
        this.referredUserId = referredUserId;
        this.usageDate = usageDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDateTime getUsageDate() {
        return usageDate;
    }

    public void setUsageDate(LocalDateTime usageDate) {
        this.usageDate = usageDate;
    }
}