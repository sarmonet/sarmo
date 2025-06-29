package com.sarmo.referralservice.dto;

import java.time.LocalDateTime;

public class ReferralCodeDto {

    private Long id;
    private String code;
    private Long userId; // ID пользователя, которому принадлежит код
    private LocalDateTime creationDate;

    public ReferralCodeDto() {
    }

    public ReferralCodeDto(Long id, String code, Long userId, LocalDateTime creationDate) {
        this.id = id;
        this.code = code;
        this.userId = userId;
        this.creationDate = creationDate;
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }
}