package com.sarmo.userservice.controller;

import com.sarmo.userservice.entity.NotificationSettings;
import com.sarmo.userservice.service.interfaces.NotificationSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user/settings/notification")
public class NotificationSettingsController {

    private final NotificationSettingsService notificationSettingsService;

    private static final Logger logger = LoggerFactory.getLogger(NotificationSettingsController.class);

    // Removed injection of JwtTokenDataExtractor from the constructor if not directly needed here
    public NotificationSettingsController(NotificationSettingsService notificationSettingsService) {
        this.notificationSettingsService = notificationSettingsService;
    }

    // --- Methods for the current authenticated user (using principal ID) ---
    // Replaces functionality of NotificationSettingsTokenController

    @PostMapping // Create settings for the current user
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<NotificationSettings> createNotificationSettingsForCurrentUser(
            @RequestBody NotificationSettings notificationSettings) {
        logger.info("Creating notification settings for current user");
        Long userId = getCurrentUserId(); // Get user ID from principal
        notificationSettings.setUserId(userId); // Link settings to the current user
        NotificationSettings createdSettings = notificationSettingsService.createNotificationSettings(notificationSettings);
        return new ResponseEntity<>(createdSettings, HttpStatus.CREATED);
    }

    @GetMapping // Get settings for the current user
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<NotificationSettings> getNotificationSettingsForCurrentUser() {
        logger.info("Getting notification settings for current user");
        Long userId = getCurrentUserId();
        // Assuming notification settings ID matches the stringified user ID
        NotificationSettings settings = notificationSettingsService.getNotificationSettingsById(String.valueOf(userId));
        if (settings != null) {
            return new ResponseEntity<>(settings, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping // Update settings for the current user
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<NotificationSettings> updateNotificationSettingsForCurrentUser(
            @RequestBody NotificationSettings notificationSettings) {
        logger.info("Updating notification settings for current user");
        Long userId = getCurrentUserId();
        notificationSettings.setUserId(userId); // Ensure the settings object is linked to the current user
        // Service implementation should also verify that the settings object being updated belongs to this user
        NotificationSettings updatedSettings = notificationSettingsService.updateNotificationSettings(notificationSettings);
        if (updatedSettings != null) {
            return new ResponseEntity<>(updatedSettings, HttpStatus.OK);
        } else {
            // Can return HttpStatus.FORBIDDEN if the service detects an attempt to update others' settings
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping // Delete settings for the current user
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteNotificationSettingsForCurrentUser() {
        logger.info("Deleting notification settings for current user");
        Long userId = getCurrentUserId();
        // Assuming notification settings ID matches the stringified user ID
        notificationSettingsService.deleteNotificationSettings(String.valueOf(userId));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // --- Methods by User ID (as String - likely for Admin) ---
    // Replaces getNotificationSettingsById and deleteNotificationSettings from the first controller,
    // assuming the String ID in the path is the stringified user ID

    @GetMapping("/{userIdStr}") // Get settings by User ID (as String) - Path variable renamed for clarity
    @PreAuthorize("hasRole('ADMIN')") // Only ADMIN can get settings by arbitrary User ID
    public ResponseEntity<NotificationSettings> getNotificationSettingsByUserIdStr(@PathVariable String userIdStr) {
        logger.info("Getting notification settings by user ID string: {}", userIdStr);
        // Assuming notification settings ID matches the stringified user ID
        NotificationSettings settings = notificationSettingsService.getNotificationSettingsById(userIdStr);
        if (settings != null) {
            return new ResponseEntity<>(settings, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{userIdStr}") // Delete settings by User ID (as String) - Path variable renamed for clarity
    @PreAuthorize("hasRole('ADMIN')") // Only ADMIN can delete settings by arbitrary User ID
    public ResponseEntity<Void> deleteNotificationSettingsByUserIdStr(@PathVariable String userIdStr) {
        logger.info("Deleting notification settings by user ID string: {}", userIdStr);
        // Assuming notification settings ID matches the stringified user ID
        notificationSettingsService.deleteNotificationSettings(userIdStr);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    // --- Methods by User ID (as Long) and Linking (Admin) ---
    // Replaces getNotificationSettingsId and setNotificationSettingsId from the first controller
    // Paths changed to avoid conflict with /{userIdStr}

    @GetMapping("/by-user-id/{userId}") // Get NotificationSettingsId by User ID (as Long) - New path for clarity
    @PreAuthorize("hasRole('ADMIN')") // Admin only
    public ResponseEntity<String> getNotificationSettingsIdByUserId(@PathVariable Long userId) {
        logger.info("Getting notification settings ID for user ID: {}", userId);
        String settingsId = notificationSettingsService.getNotificationSettingsId(userId);
        if (settingsId != null) {
            return new ResponseEntity<>(settingsId, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/link/{userId}/{notificationSettingsId}") // Link User ID to Settings ID - New path for clarity
    @PreAuthorize("hasRole('ADMIN')") // Admin only
    public ResponseEntity<Void> linkNotificationSettingsIdToUserId(
            @PathVariable Long userId, @PathVariable String notificationSettingsId) {
        logger.info("Linking settings ID {} to user ID {}", notificationSettingsId, userId);
        notificationSettingsService.setNotificationSettingsId(userId, notificationSettingsId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // --- Helper method to get the current authenticated user's ID ---
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication.getPrincipal() instanceof String && authentication.getPrincipal().equals("anonymousUser"))) {
            // Assuming the principal name (username) is the user ID as a String
            // This depends on how your JwtTokenAuthenticationFilter sets the principal
            try {
                return Long.parseLong(authentication.getName());
            } catch (NumberFormatException e) {
                // Log an error if the principal name is not a number (unexpected)
                logger.error("Principal name is not a valid user ID (Long): {}", authentication.getName(), e);
                // Depending on your application's error handling, you might throw a custom exception
                throw new IllegalStateException("Authenticated principal's name is not a valid user ID format");
            }
        }
        // If isAuthenticated() is true, this branch indicates an unexpected principal type or anonymousUser
        // If isAuthenticated() false, this method should not be called due to @PreAuthorize
        logger.error("getCurrentUserId called but user is not authenticated or principal is anonymous");
        throw new IllegalStateException("User is not authenticated or principal is not as expected");
    }
}