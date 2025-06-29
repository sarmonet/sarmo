package com.sarmo.noticeservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    private Long id;

    @Email
    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String phoneNumber;

    private String profilePictureUrl;

    private String firstName;

    private String lastName;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NotificationSettings> notificationSettingsList;

    public User() {
    }

    public User(Long id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public User(Long id, String email, String phoneNumber, String profilePictureUrl, String firstName, String lastName, List<NotificationSettings> notificationSettingsList) {
        this.id = id;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.profilePictureUrl = profilePictureUrl;
        this.firstName = firstName;
        this.lastName = lastName;
        this.notificationSettingsList = notificationSettingsList;
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

    public List<NotificationSettings> getNotificationSettingsList() {
        return notificationSettingsList;
    }

    public void setNotificationSettingsList(List<NotificationSettings> notificationSettingsList) {
        this.notificationSettingsList = notificationSettingsList;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }
}