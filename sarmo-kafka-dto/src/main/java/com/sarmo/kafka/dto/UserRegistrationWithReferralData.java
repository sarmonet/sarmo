package com.sarmo.kafka.dto;

public class UserRegistrationWithReferralData {
    private Long userId;
    private String emailOrPhone;
    private String firstName;
    private String lastName;
    private String referralCode;

    public UserRegistrationWithReferralData(){}

    public UserRegistrationWithReferralData(Long userId, String emailOrPhone, String firstName, String lastName, String referralCode) {
        this.userId = userId;
        this.emailOrPhone = emailOrPhone;
        this.firstName = firstName;
        this.lastName = lastName;
        this.referralCode = referralCode;
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

    public String getReferralCode() {
        return referralCode;
    }

    public void setReferralCode(String referralCode) {
        this.referralCode = referralCode;
    }
}
