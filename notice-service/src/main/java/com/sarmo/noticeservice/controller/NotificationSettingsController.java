package com.sarmo.noticeservice.controller;

import com.sarmo.noticeservice.dto.NotificationSettingsCreateDto;
import com.sarmo.noticeservice.dto.NotificationSettingsDto;
import com.sarmo.noticeservice.entity.NotificationSettings;
import com.sarmo.noticeservice.service.NotificationSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Important import for security
import org.springframework.security.core.Authentication; // Important import for security
import org.springframework.security.core.context.SecurityContextHolder; // Important import for security
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/notice/settings")
public class NotificationSettingsController {

    private static final Logger logger = LoggerFactory.getLogger(NotificationSettingsController.class);

    private final NotificationSettingsService notificationSettingsService;

    public NotificationSettingsController(NotificationSettingsService notificationSettingsService) {
        this.notificationSettingsService = notificationSettingsService;
    }

    /**
     * Helper method to extract the current authenticated user's ID from the SecurityContext.
     * Assumes that the principal's name (authentication.getName()) is the user ID in Long format.
     *
     * @return The ID of the currently authenticated user.
     * @throws IllegalStateException if the user is not authenticated or the principal is in an unexpected format.
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            logger.error("getCurrentUserId called but no authenticated user found.");
            throw new IllegalStateException("User is not authenticated.");
        }
        try {
            // Assuming the principal name (username) is the user ID as a String,
            // and we parse it to Long.
            return Long.parseLong(authentication.getName());
        } catch (NumberFormatException e) {
            logger.error("Principal name '{}' is not a valid user ID (Long) format.", authentication.getName(), e);
            throw new IllegalStateException("Authenticated principal's name is not a valid user ID format.");
        } catch (Exception e) {
            logger.error("Unexpected error getting principal from SecurityContext: {}", e.getMessage(), e);
            throw new IllegalStateException("Could not retrieve user ID from authentication context.");
        }
    }

    // --- Endpoints for the current authenticated user ---
    // These endpoints use the user ID from SecurityContextHolder.
    // They replace functionality from NotificationSettingsTokenController.

    @PostMapping // Create notification settings for the currently authenticated user
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<NotificationSettingsDto> createNotificationSettingsForCurrentUser(
            @RequestBody NotificationSettingsCreateDto createDto) {
        Long userId = getCurrentUserId();
        logger.info("Received request to create notification settings for current user ID: {}", userId);
        try {
            NotificationSettingsDto createdSettings = notificationSettingsService.createNotificationSettingsWithSubscription(userId, createDto);
            return new ResponseEntity<>(createdSettings, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            logger.error("Error creating notification settings for current user ID {}: {}", userId, e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Unexpected error creating notification settings for current user ID {}: {}", userId, e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping // Get all notification settings for the currently authenticated user
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<NotificationSettingsDto>> getAllNotificationSettingsForCurrentUser() {
        Long userId = getCurrentUserId();
        logger.info("Received request to get all notification settings for current user ID: {}", userId);
        List<NotificationSettingsDto> settingsList = notificationSettingsService.getAllNotificationSettingsByUserId(userId);
        return new ResponseEntity<>(settingsList, HttpStatus.OK);
    }

    @GetMapping("/{id}") // Get a specific notification setting by its ID, ensuring it belongs to the current user
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<NotificationSettings> getNotificationSettingsByIdAndCurrentUser(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        logger.info("Received request to get notification settings by ID: {} for current user ID: {}", id, userId);
        Optional<NotificationSettings> settings = notificationSettingsService.getNotificationSettingsById(id);
        return settings.filter(s -> s.getUser() != null && s.getUser().getId().equals(userId)) // Ownership check
                .map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}") // Update a specific notification setting by its ID, ensuring it belongs to the current user
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<NotificationSettings> updateNotificationSettingsByIdAndCurrentUser(
            @PathVariable Long id,
            @RequestBody NotificationSettingsDto updateDto) {
        Long userId = getCurrentUserId();
        logger.info("Received request to update notification settings with ID: {} for current user ID: {}", id, userId);
        try {
            Optional<NotificationSettings> existingSettingsOpt = notificationSettingsService.getNotificationSettingsById(id);

            if (existingSettingsOpt.isEmpty()) {
                logger.warn("Notification settings not found for ID: {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            NotificationSettings existingSettings = existingSettingsOpt.get();
            if (existingSettings.getUser() == null || !existingSettings.getUser().getId().equals(userId)) {
                logger.warn("User ID {} is not authorized to update settings with ID: {}", userId, id);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403 Forbidden
            }

            NotificationSettings updatedSettings = notificationSettingsService.updateNotificationSettings(id, updateDto);
            return new ResponseEntity<>(updatedSettings, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            logger.error("Error updating notification settings with ID {} for user ID {}: {}", id, userId, e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Unexpected error updating notification settings with ID {} for user ID {}: {}", id, userId, e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}") // Delete a specific notification setting by its ID, ensuring it belongs to the current user
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteNotificationSettingsByIdAndCurrentUser(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        logger.info("Received request to delete notification settings with ID: {} for current user ID: {}", id, userId);
        Optional<NotificationSettings> existingSettingsOpt = notificationSettingsService.getNotificationSettingsById(id);

        if (existingSettingsOpt.isEmpty()) {
            logger.warn("Notification settings not found for ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        NotificationSettings existingSettings = existingSettingsOpt.get();
        if (existingSettings.getUser() == null || !existingSettings.getUser().getId().equals(userId)) {
            logger.warn("User ID {} is not authorized to delete settings with ID: {}", userId, id);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403 Forbidden
        }

        notificationSettingsService.deleteNotificationSettings(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // --- Endpoints for administrative purposes or internal service calls (access by user ID) ---
    // These endpoints originated from the previous NotificationSettingsController and handle arbitrary user IDs.
    // It is crucial to apply appropriate authorization, such as @PreAuthorize("hasRole('ADMIN')"), here.

    @PostMapping("/for-user/{userId}") // Create notification settings for a specific user ID (admin/internal)
    @PreAuthorize("hasRole('ADMIN')") // Assumed to be for admin or internal service use
    public ResponseEntity<NotificationSettingsDto> createNotificationSettingsForArbitraryUserId(
            @PathVariable Long userId,
            @RequestBody NotificationSettingsCreateDto createDto) {
        logger.info("Received request to create notification settings for user ID (admin/internal): {}", userId);
        try {
            NotificationSettingsDto createdSettings = notificationSettingsService.createNotificationSettingsWithSubscription(userId, createDto);
            return new ResponseEntity<>(createdSettings, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            logger.error("Error creating notification settings for user ID {} (admin/internal): {}", userId, e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Unexpected error creating notification settings for user ID {} (admin/internal): {}", userId, e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/for-user/{userId}") // Get all notification settings for a specific user ID (admin/internal)
    @PreAuthorize("hasRole('ADMIN')") // Assumed to be for admin or internal service use
    public ResponseEntity<List<NotificationSettingsDto>> getAllNotificationSettingsForArbitraryUserId(
            @PathVariable Long userId) {
        logger.info("Received request to get all notification settings for user ID (admin/internal): {}", userId);
        List<NotificationSettingsDto> settingsList = notificationSettingsService.getAllNotificationSettingsByUserId(userId);
        return new ResponseEntity<>(settingsList, HttpStatus.OK);
    }

    // --- Admin-only endpoint to get all notification settings across all users ---

    @GetMapping("/all") // Get all notification settings across all users (admin only)
    @PreAuthorize("hasRole('ADMIN')") // Requires ADMIN role
    public ResponseEntity<List<NotificationSettingsDto>> getAllNotificationSettingsAdmin() {
        logger.info("Received request to get all notification settings (admin access)");
        List<NotificationSettingsDto> allSettings = notificationSettingsService.getAllNotificationSettings();
        return new ResponseEntity<>(allSettings, HttpStatus.OK);
    }
}