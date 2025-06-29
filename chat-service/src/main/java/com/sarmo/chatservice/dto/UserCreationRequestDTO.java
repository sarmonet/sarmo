package com.sarmo.chatservice.dto;

import java.util.Objects;

public class UserCreationRequestDTO {
    private String email;
    private String phoneNumber;
    private String password;
    private String firstName;
    private String lastName;

    public UserCreationRequestDTO() {}

    public UserCreationRequestDTO(String email, String phoneNumber, String password, String firstName, String lastName) {
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
        UserCreationRequestDTO that = (UserCreationRequestDTO) o;
        return Objects.equals(email, that.email) && Objects.equals(phoneNumber, that.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, phoneNumber);
    }

    @Override
    public String toString() {
        return "UserCreationRequestDTO{" +
                "email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", password='[PROTECTED]'" +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}