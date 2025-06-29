package com.sarmo.userservice.service;

import com.sarmo.userservice.entity.User;
import com.sarmo.userservice.entity.UserSettings;
import com.sarmo.userservice.repository.UserSettingsRepository;
import com.sarmo.userservice.service.interfaces.UserService;
import com.sarmo.userservice.service.interfaces.UserSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserSettingsServiceImpl implements UserSettingsService {

    private static final Logger logger = LoggerFactory.getLogger(UserSettingsServiceImpl.class);

    private final UserSettingsRepository userSettingsRepository;
    private final UserServiceImpl userService;

    public UserSettingsServiceImpl(UserSettingsRepository userSettingsRepository, UserServiceImpl userService) {
        this.userSettingsRepository = userSettingsRepository;
        this.userService = userService;
    }

    @Override
    public UserSettings createUserSettings(UserSettings userSettings) {
        try {
            logger.info("Creating user settings: {}", userSettings);
            return userSettingsRepository.save(userSettings);
        } catch (Exception e) {
            logger.error("Error creating user settings: {}", e.getMessage());
            throw new RuntimeException("Failed to create user settings", e);
        }
    }

    @Override
    public UserSettings getUserSettingsById(String id) {
        try {
            logger.debug("Getting user settings by id: {}", id);
            Optional<UserSettings> userSettings = userSettingsRepository.findById(id);
            return userSettings.orElse(null);
        } catch (Exception e) {
            logger.error("Error getting user settings by id {}: {}", id, e.getMessage());
            throw new RuntimeException("Failed to get user settings by id", e);
        }
    }

    @Override
    public UserSettings updateUserSettings(UserSettings userSettings) {
        try {
            logger.info("Updating user settings: {}", userSettings);
            return userSettingsRepository.save(userSettings);
        } catch (Exception e) {
            logger.error("Error updating user settings: {}", e.getMessage());
            throw new RuntimeException("Failed to update user settings", e);
        }
    }

    @Override
    public void deleteUserSettings(String id) {
        try {
            logger.info("Deleting user settings by id: {}", id);
            userSettingsRepository.deleteById(id);
        } catch (Exception e) {
            logger.error("Error deleting user settings by id {}: {}", id, e.getMessage());
            throw new RuntimeException("Failed to delete user settings by id", e);
        }
    }

    @Override
    public void setUserSettingsId(Long userId, String userSettingsId) {
        try {
            logger.debug("Setting user settings id {} for user {}", userSettingsId, userId);
            User user = userService.getUserById(userId);
            if (user != null) {
                user.setUserSettingsId(userSettingsId);
                userService.updateUser(user);
            } else {
                logger.warn("User with id {} not found, user settings id not set", userId);
                throw new RuntimeException("User not found");
            }
        } catch (Exception e) {
            logger.error("Error setting user settings id: {}", e.getMessage());
            throw new RuntimeException("Failed to set user settings id", e);
        }
    }

    @Override
    public String getUserSettingsId(Long userId) {
        try {
            logger.debug("Getting user settings id for user {}", userId);
            User user = userService.getUserById(userId);
            if (user != null) {
                return user.getUserSettingsId();
            } else {
                logger.warn("User with id {} not found, user settings id not retrieved", userId);
                return null;
            }
        } catch (Exception e) {
            logger.error("Error getting user settings id: {}", e.getMessage());
            throw new RuntimeException("Failed to get user settings id", e);
        }
    }
}