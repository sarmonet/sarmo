package com.sarmo.subscriptionservice.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    private Long id; // Соответствует ID пользователя из сервиса регистрации

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @CreationTimestamp
    private LocalDateTime registrationDate;
//
//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<UserSubscription> userSubscriptions;

    @ManyToOne
    @JoinColumn(name = "active_subscription_id")
    private UserSubscription activeSubscription;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserIndividualFeature> userIndividualFeatures;

    public User() {
    }

    public User(Long id, String name, String lastName) {
        this.id = id;
        this.firstName = name;
        this.lastName = lastName;
    }

    public User(Long id, LocalDateTime registrationDate, UserSubscription activeSubscription, List<UserIndividualFeature> userIndividualFeatures) {
        this.id = id;
        this.registrationDate = registrationDate;
        this.activeSubscription = activeSubscription;
        this.userIndividualFeatures = userIndividualFeatures;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }


    public UserSubscription getActiveSubscription() {
        return activeSubscription;
    }

    public void setActiveSubscription(UserSubscription activeSubscription) {
        this.activeSubscription = activeSubscription;
    }

    public List<UserIndividualFeature> getUserIndividualFeatures() {
        return userIndividualFeatures;
    }

    public void setUserIndividualFeatures(List<UserIndividualFeature> userIndividualFeatures) {
        this.userIndividualFeatures = userIndividualFeatures;
    }
}