package com.sarmo.noticeservice.dto;

import com.sarmo.noticeservice.enums.PreferredCommunicationChannel;

public class NotificationSettingsCreateDto {
    private boolean isPromotionalSubscribed;
    private PreferredCommunicationChannel preferredCommunicationChannel;
    private CategorySubscriptionCreateDto categorySubscription;

    public NotificationSettingsCreateDto() {}

    public NotificationSettingsCreateDto(boolean isPromotionalSubscribed, PreferredCommunicationChannel preferredCommunicationChannel, CategorySubscriptionCreateDto categorySubscription) {
        this.isPromotionalSubscribed = isPromotionalSubscribed;
        this.preferredCommunicationChannel = preferredCommunicationChannel;
        this.categorySubscription = categorySubscription;
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

    public CategorySubscriptionCreateDto getCategorySubscription() {
        return categorySubscription;
    }

    public void setCategorySubscription(CategorySubscriptionCreateDto categorySubscription) {
        this.categorySubscription = categorySubscription;
    }
}
