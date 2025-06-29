package com.sarmo.authservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class LoginRequest {

    @NotBlank
    @Pattern(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$|^\\+?[1-9]\\d{1,14}$", message = "Invalid email or phone number")
    private String contact;

    @NotBlank
    private String password;

    public LoginRequest(){}

    public LoginRequest(String contact, String password) {
        this.contact = contact;
        this.password = password;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}