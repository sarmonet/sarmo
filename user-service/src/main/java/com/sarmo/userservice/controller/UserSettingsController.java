package com.sarmo.userservice.controller;

import com.sarmo.userservice.entity.UserSettings;
import com.sarmo.userservice.service.interfaces.UserSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Import
import org.springframework.security.core.Authentication; // Import
import org.springframework.security.core.context.SecurityContextHolder; // Import
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user/setting")
// PreAuthorize annotations are on method level for mixed access
public class UserSettingsController { // Kept the original name

    private final UserSettingsService userSettingsService;

    private static final Logger logger = LoggerFactory.getLogger(UserSettingsController.class); // Updated logger name

    public UserSettingsController(UserSettingsService userSettingsService) {
        this.userSettingsService = userSettingsService;
    }

    @PostMapping // Create settings for the current user
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserSettings> createUserSettingsForCurrentUser(@RequestBody UserSettings userSettings) {
        logger.info("Creating user settings for current user");
        Long userId = getCurrentUserId(); // Get user ID from authenticated principal
        // Assuming Settings ID is the stringified User ID
        userSettings.setId(String.valueOf(userId)); // Ensure ID is set based on current user
        // Service should handle logic if settings already exist for this user (e.g., update instead)
        UserSettings createdSettings = userSettingsService.createUserSettings(userSettings);
        return new ResponseEntity<>(createdSettings, HttpStatus.CREATED);
    }

    @GetMapping // Get settings for the current user
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserSettings> getUserSettingsForCurrentUser() {
        logger.info("Getting user settings for current user");
        Long userId = getCurrentUserId(); // Get user ID from authenticated principal
        // Assuming Settings ID is the stringified User ID
        UserSettings settings = userSettingsService.getUserSettingsById(String.valueOf(userId));
        if (settings != null) {
            return new ResponseEntity<>(settings, HttpStatus.OK);
        } else {
            logger.info("User settings not found for current user {}", userId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping // Update settings for the current user
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserSettings> updateUserSettingsForCurrentUser(@RequestBody UserSettings userSettings) {
        logger.info("Updating user settings for current user");
        Long userId = getCurrentUserId(); // Get user ID from authenticated principal
        // Assuming Settings ID is the stringified User ID
        userSettings.setId(String.valueOf(userId)); // Ensure ID is set based on current user for update
        // Service implementation should also verify that the settings object being updated belongs to this user ID
        UserSettings updatedSettings = userSettingsService.updateUserSettings(userSettings);
        if (updatedSettings != null) {
            return new ResponseEntity<>(updatedSettings, HttpStatus.OK);
        } else {
            // Could return HttpStatus.FORBIDDEN if service detects user mismatch, or NOT_FOUND if settings don't exist
            logger.warn("User settings not found or user mismatch for current user {} during update", userId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping // Delete settings for the current user
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteUserSettingsForCurrentUser() {
        logger.info("Deleting user settings for current user");
        Long userId = getCurrentUserId(); // Get user ID from authenticated principal
        // Assuming Settings ID is the stringified User ID
        userSettingsService.deleteUserSettings(String.valueOf(userId));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @GetMapping("/{userIdStr}") // Get settings by User ID (as String) - Clarified path variable
    @PreAuthorize("hasRole('ADMIN')") // Only ADMIN can get settings by arbitrary User ID (as string)
    public ResponseEntity<UserSettings> getUserSettingsByUserIdStr(@PathVariable String userIdStr) {
        logger.info("Getting user settings by user ID string: {} (Admin)", userIdStr);
        // Assuming Settings ID is the stringified User ID
        UserSettings settings = userSettingsService.getUserSettingsById(userIdStr);
        if (settings != null) {
            return new ResponseEntity<>(settings, HttpStatus.OK);
        } else {
            logger.warn("User settings not found for user ID string {} (Admin)", userIdStr);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{userIdStr}") // Delete settings by User ID (as String) - Clarified path variable
    @PreAuthorize("hasRole('ADMIN')") // Only ADMIN can delete settings by arbitrary User ID (as string)
    public ResponseEntity<Void> deleteUserSettingsByUserIdStr(@PathVariable String userIdStr) {
        logger.info("Deleting user settings by user ID string: {} (Admin)", userIdStr);
        // Assuming Settings ID is the stringified User ID
        userSettingsService.deleteUserSettings(userIdStr);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @GetMapping("/by-user-id/{userId}") // Get UserSettingsId (String) by User ID (Long) - New path for clarity
    @PreAuthorize("hasRole('ADMIN')") // Admin only
    public ResponseEntity<String> getUserSettingsIdByUserId(@PathVariable Long userId) {
        logger.info("Getting user settings ID for user ID: {} (Admin)", userId);
        String settingsId = userSettingsService.getUserSettingsId(userId);
        if (settingsId != null) {
            return new ResponseEntity<>(settingsId, HttpStatus.OK);
        } else {
            logger.warn("User settings ID not found for user ID: {} (Admin)", userId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/link/{userId}/{userSettingsId}") // Link User ID (Long) to UserSettingsId (String) - New path for clarity
    @PreAuthorize("hasRole('ADMIN')") // Admin only
    public ResponseEntity<Void> linkUserSettingsIdToUserId(
            @PathVariable Long userId, @PathVariable String userSettingsId) {
        logger.info("Linking settings ID {} to user ID {} (Admin)", userSettingsId, userId);
        userSettingsService.setUserSettingsId(userId, userSettingsId);
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