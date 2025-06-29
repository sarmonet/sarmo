package com.sarmo.userservice.service.interfaces;

import com.sarmo.userservice.entity.UserSettings;

public interface UserSettingsService {

    UserSettings createUserSettings(UserSettings userSettings);

    UserSettings getUserSettingsById(String id);

    UserSettings updateUserSettings(UserSettings userSettings);

    void deleteUserSettings(String id);

    void setUserSettingsId(Long userId, String userSettingsId);

    String getUserSettingsId(Long userId);
}