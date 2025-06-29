package com.sarmo.kafka.dto;


public class UserRegistrationData {

    private Long userId;
    private String emailOrPhone;
    private String firstName;
    private String lastName;

    // Геттеры, сеттеры, конструкторы
    public UserRegistrationData() {}

    public UserRegistrationData(Long userId, String emailOrPhone, String firstName, String lastName) {
        this.userId = userId;
        this.emailOrPhone = emailOrPhone;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEmailOrPhone() {
        return emailOrPhone;
    }

    public void setEmailOrPhone(String emailOrPhone) {
        this.emailOrPhone = emailOrPhone;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}