package com.sarmo.referralservice.dto;

import jakarta.validation.constraints.NotBlank;

public class UpdateReferralCodeDto {

    @NotBlank(message = "Code cannot be blank")
    private String code;

    // Конструкторы
    public UpdateReferralCodeDto() {
    }

    public UpdateReferralCodeDto(String code) {
        this.code = code;
    }

    // Геттеры и сеттеры
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}