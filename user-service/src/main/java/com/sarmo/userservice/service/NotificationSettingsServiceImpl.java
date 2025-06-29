package com.sarmo.userservice.service;

import com.sarmo.userservice.entity.NotificationSettings;
import com.sarmo.userservice.entity.User;
import com.sarmo.userservice.repository.NotificationSettingsRepository;
import com.sarmo.userservice.service.interfaces.NotificationSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class NotificationSettingsServiceImpl implements NotificationSettingsService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationSettingsServiceImpl.class);

    private final NotificationSettingsRepository notificationSettingsRepository;
    private final UserServiceImpl userService;

    public NotificationSettingsServiceImpl(NotificationSettingsRepository notificationSettingsRepository, UserServiceImpl userService) {
        this.notificationSettingsRepository = notificationSettingsRepository;
        this.userService = userService;
    }

    @Override
    public NotificationSettings createNotificationSettings(NotificationSettings notificationSettings) {
        try {
            logger.info("Creating notification settings: {}", notificationSettings);
            return notificationSettingsRepository.save(notificationSettings);
        } catch (Exception e) {
            logger.error("Error creating notification settings: {}", e.getMessage());
            throw new RuntimeException("Failed to create notification settings", e);
        }
    }

    @Override
    public NotificationSettings getNotificationSettingsById(String id) {
        try {
            logger.debug("Getting notification settings by id: {}", id);
            Optional<NotificationSettings> notificationSettings = notificationSettingsRepository.findById(id);
            return notificationSettings.orElse(null);
        } catch (Exception e) {
            logger.error("Error getting notification settings by id {}: {}", id, e.getMessage());
            throw new RuntimeException("Failed to get notification settings by id", e);
        }
    }

    @Override
    public NotificationSettings updateNotificationSettings(NotificationSettings notificationSettings) {
        try {
            logger.info("Updating notification settings: {}", notificationSettings);
            return notificationSettingsRepository.save(notificationSettings);
        } catch (Exception e) {
            logger.error("Error updating notification settings: {}", e.getMessage());
            throw new RuntimeException("Failed to update notification settings", e);
        }
    }

    @Override
    public void deleteNotificationSettings(String id) {
        try {
            logger.info("Deleting notification settings by id: {}", id);
            notificationSettingsRepository.deleteById(id);
        } catch (Exception e) {
            logger.error("Error deleting notification settings by id {}: {}", id, e.getMessage());
            throw new RuntimeException("Failed to delete notification settings by id", e);
        }
    }


    @Override
    public void setNotificationSettingsId(Long userId, String notificationSettingsId) {
        try {
            logger.debug("Setting notification settings id {} for user {}", notificationSettingsId, userId);
            User user = userService.getUserById(userId);
            if (user != null) {
                user.setNotificationSettingsId(notificationSettingsId);
                userService.updateUser(user);
            } else {
                logger.warn("User with id {} not found, notification settings id not set", userId);
                throw new RuntimeException("User not found");
            }
        } catch (Exception e) {
            logger.error("Error setting notification settings id: {}", e.getMessage());
            throw new RuntimeException("Failed to set notification settings id", e);
        }
    }

    @Override
    public String getNotificationSettingsId(Long userId) {
        try {
            logger.debug("Getting notification settings id for user {}", userId);
            User user = userService.getUserById(userId);
            if (user != null) {
                return user.getNotificationSettingsId();
            } else {
                logger.warn("User with id {} not found, notification settings id not retrieved", userId);
                return null;
            }
        } catch (Exception e) {
            logger.error("Error getting notification settings id: {}", e.getMessage());
            throw new RuntimeException("Failed to get notification settings id", e);
        }
    }

}