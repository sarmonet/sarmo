package com.sarmo.noticeservice.entity;

import com.google.type.DateTime;
import com.sarmo.noticeservice.enums.FrequencyType;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
public class CategorySubscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FrequencyType frequency = FrequencyType.DAILY;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "is_active")
    private boolean active;

    @Column(columnDefinition = "TEXT")
    private String filters;

    @OneToOne
    @JoinColumn(name = "notification_settings_id", unique = true)
    private NotificationSettings notificationSettings;

    public CategorySubscription() {
    }

    public CategorySubscription(FrequencyType frequency, LocalDateTime createdAt, boolean active, String filters, NotificationSettings notificationSettings) {
        this.frequency = frequency;
        this.createdAt = createdAt;
        this.active = active;
        this.filters = filters;
        this.notificationSettings = notificationSettings;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public FrequencyType getFrequency() {
        return frequency;
    }

    public void setFrequency(FrequencyType frequency) {
        this.frequency = frequency;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getFilters() {
        return filters;
    }

    public void setFilters(String filters) {
        this.filters = filters;
    }

    public NotificationSettings getNotificationSettings() {
        return notificationSettings;
    }

    public void setNotificationSettings(NotificationSettings notificationSettings) {
        this.notificationSettings = notificationSettings;
    }
}