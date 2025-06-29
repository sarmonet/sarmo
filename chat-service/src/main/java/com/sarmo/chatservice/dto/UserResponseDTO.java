package com.sarmo.chatservice.dto;

import java.util.Objects;

public class UserResponseDTO {
    private Long id;
    private String email;
    private String phoneNumber;
    private String profilePictureUrl;
    private String firstName;
    private String lastName;

    public UserResponseDTO() {}

    public UserResponseDTO(Long id, String email, String phoneNumber, String profilePictureUrl, String firstName, String lastName) {
        this.id = id;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.profilePictureUrl = profilePictureUrl;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
        UserResponseDTO that = (UserResponseDTO) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "UserResponseDTO{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", profilePictureUrl='" + profilePictureUrl + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}