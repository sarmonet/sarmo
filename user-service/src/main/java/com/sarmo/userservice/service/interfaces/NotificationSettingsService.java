package com.sarmo.userservice.service.interfaces;

import com.sarmo.userservice.entity.NotificationSettings;

public interface NotificationSettingsService {

    NotificationSettings createNotificationSettings(NotificationSettings notificationSettings);

    NotificationSettings getNotificationSettingsById(String id);

    NotificationSettings updateNotificationSettings(NotificationSettings notificationSettings);

    void deleteNotificationSettings(String id);

    void setNotificationSettingsId(Long userId, String notificationSettingsId);

    String getNotificationSettingsId(Long userId);

}