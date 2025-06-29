package com.sarmo.userservice.entity;

import com.sarmo.userservice.enums.PreferredCommunicationChannel;
import jakarta.persistence.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Document(collection = "notification_settings")
public class NotificationSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private boolean isPromotionalSubscribed;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PreferredCommunicationChannel preferredCommunicationChannel;

    private List<CategorySubscription> categorySubscriptions;

    public NotificationSettings() {}

    public NotificationSettings(Long id, Long userId, boolean isPromotionalSubscribed, PreferredCommunicationChannel preferredCommunicationChannel, List<CategorySubscription> categorySubscriptions) {
        this.id = id;
        this.userId = userId;
        this.isPromotionalSubscribed = isPromotionalSubscribed;
        this.preferredCommunicationChannel = preferredCommunicationChannel;
        this.categorySubscriptions = categorySubscriptions;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public boolean isPromotionalSubscribed() {
        return isPromotionalSubscribed;
    }

    public void setPromotionalSubscribed(boolean promotionalSubscribed) {
        isPromotionalSubscribed = promotionalSubscribed;
    }

    public PreferredCommunicationChannel getPreferredCommunicationChannel() {
        return preferredCommunicationChannel;
    }

    public void setPreferredCommunicationChannel(PreferredCommunicationChannel preferredCommunicationChannel) {
        this.preferredCommunicationChannel = preferredCommunicationChannel;
    }

    public List<CategorySubscription> getCategorySubscriptions() {
        return categorySubscriptions;
    }

    public void setCategorySubscriptions(List<CategorySubscription> categorySubscriptions) {
        this.categorySubscriptions = categorySubscriptions;
    }
}