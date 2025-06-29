package com.sarmo.noticeservice.dto;

import com.sarmo.noticeservice.enums.PreferredCommunicationChannel;

public class NotificationSettingsDto {
    private Long id;
    private Long userId;
    private PreferredCommunicationChannel preferredCommunicationChannel;
    private CategorySubscriptionDto categorySubscription;


    public NotificationSettingsDto() {
    }

    public NotificationSettingsDto(Long id, Long userId, PreferredCommunicationChannel preferredCommunicationChannel, CategorySubscriptionDto categorySubscription) {
        this.id = id;
        this.userId = userId;
        this.preferredCommunicationChannel = preferredCommunicationChannel;
        this.categorySubscription = categorySubscription;
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

    public PreferredCommunicationChannel getPreferredCommunicationChannel() {
        return preferredCommunicationChannel;
    }

    public void setPreferredCommunicationChannel(PreferredCommunicationChannel preferredCommunicationChannel) {
        this.preferredCommunicationChannel = preferredCommunicationChannel;
    }

    public CategorySubscriptionDto getCategorySubscription() {
        return categorySubscription;
    }

    public void setCategorySubscription(CategorySubscriptionDto categorySubscription) {
        this.categorySubscription = categorySubscription;
    }
}