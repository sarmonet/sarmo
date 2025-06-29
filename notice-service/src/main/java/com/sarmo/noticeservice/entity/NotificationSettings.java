package com.sarmo.noticeservice.entity;

import com.sarmo.noticeservice.enums.PreferredCommunicationChannel;
import jakarta.persistence.*;

@Entity
@Table(name = "notification_settings")
public class NotificationSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PreferredCommunicationChannel preferredCommunicationChannel;

    @OneToOne(mappedBy = "notificationSettings", cascade = CascadeType.ALL, orphanRemoval = true)
    private CategorySubscription categorySubscription; // Изменяем тип на одну CategorySubscription

    public NotificationSettings() {
    }

    public NotificationSettings(Long id, User user, PreferredCommunicationChannel preferredCommunicationChannel, CategorySubscription categorySubscription) {
        this.id = id;
        this.user = user;
        this.preferredCommunicationChannel = preferredCommunicationChannel;
        this.categorySubscription = categorySubscription;
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public PreferredCommunicationChannel getPreferredCommunicationChannel() {
        return preferredCommunicationChannel;
    }

    public void setPreferredCommunicationChannel(PreferredCommunicationChannel preferredCommunicationChannel) {
        this.preferredCommunicationChannel = preferredCommunicationChannel;
    }

    public CategorySubscription getCategorySubscription() {
        return categorySubscription;
    }

    public void setCategorySubscription(CategorySubscription categorySubscription) {
        this.categorySubscription = categorySubscription;
    }
}