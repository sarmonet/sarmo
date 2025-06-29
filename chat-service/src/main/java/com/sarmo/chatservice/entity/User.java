// src/main/java/com/sarmo/chatservice/entity/User.java
package com.sarmo.chatservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import java.util.HashSet; // Используем HashSet для Set
import java.util.Objects; // Не забудьте импортировать Objects
import java.util.Set;

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

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_blocked_users", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "blocked_user_id")
    private Set<Long> blockedUserIds = new HashSet<>(); // Инициализация!

    public User() {
    }

    public User(Long id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public User(Long id, String email, String phoneNumber, String profilePictureUrl, String firstName, String lastName) {
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

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public Set<Long> getBlockedUserIds() {
        return blockedUserIds;
    }

    public void setBlockedUserIds(Set<Long> blockedUserIds) {
        this.blockedUserIds = blockedUserIds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id != null && Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return id != null ? Objects.hash(id) : 0;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", profilePictureUrl='" + profilePictureUrl + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", blockedUserIds=" + blockedUserIds +
                '}';
    }
}