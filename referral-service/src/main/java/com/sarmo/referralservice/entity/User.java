package com.sarmo.referralservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Email(message = "Email should be valid")
    @Column(unique = true)
    private String email;

    @Column(unique = true)
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Phone number must contain between 10 and 15 digits and can start with '+'")
    private String phoneNumber;

    @NotBlank(message = "First name cannot be blank")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank")
    private String lastName;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, optional = true)
    private ReferralCode referralCode;

    public User() {
    }

    public User(Long userId, String firstName, String lastName) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public User(Long userId, String email, String phoneNumber, String firstName, String lastName) {
        this.userId = userId;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public ReferralCode getReferralCode() {
        return referralCode;
    }

    public void setReferralCode(ReferralCode referralCode) {
        this.referralCode = referralCode;
    }
}