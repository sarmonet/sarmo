package com.sarmo.chatservice.dto;

import java.util.Objects;

public class UserUpdateRequestDTO {
    private String email;
    private String phoneNumber;
    private String profilePictureUrl;
    private String firstName;
    private String lastName;

    public UserUpdateRequestDTO() {}

    public UserUpdateRequestDTO(String email, String phoneNumber, String profilePictureUrl, String firstName, String lastName) {
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.profilePictureUrl = profilePictureUrl;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserUpdateRequestDTO that = (UserUpdateRequestDTO) o;
        return Objects.equals(email, that.email) && Objects.equals(phoneNumber, that.phoneNumber) && Objects.equals(profilePictureUrl, that.profilePictureUrl) && Objects.equals(firstName, that.firstName) && Objects.equals(lastName, that.lastName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, phoneNumber, profilePictureUrl, firstName, lastName);
    }

    @Override
    public String toString() {
        return "UserUpdateRequestDTO{" +
                "email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", profilePictureUrl='" + profilePictureUrl + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}