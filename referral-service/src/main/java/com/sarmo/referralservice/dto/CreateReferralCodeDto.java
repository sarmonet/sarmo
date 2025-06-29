package com.sarmo.referralservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateReferralCodeDto {

    @NotBlank(message = "Code cannot be blank")
    private String code;

    @NotNull(message = "User ID cannot be null")
    private Long userId;

    // Конструкторы
    public CreateReferralCodeDto() {
    }

    public CreateReferralCodeDto(String code, Long userId) {
        this.code = code;
        this.userId = userId;
    }

    // Геттеры и сеттеры
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
}