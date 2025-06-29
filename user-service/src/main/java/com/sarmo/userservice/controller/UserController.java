package com.sarmo.userservice.controller;

import com.sarmo.userservice.entity.User;
import com.sarmo.userservice.service.interfaces.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Import
import org.springframework.security.core.Authentication; // Import
import org.springframework.security.core.context.SecurityContextHolder; // Import
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
// No class-level PreAuthorize here, mixed access requires method-level
public class UserController { // Kept the original name

    private final UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class); // Updated logger name

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me") // Get the current authenticated user's profile
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<User> getCurrentUser() {
        logger.info("Getting current authenticated user's profile");
        Long userId = getCurrentUserId(); // Get user ID from authenticated principal
        User user = userService.getUserById(userId); // Service method should handle not found if user doesn't exist (unlikely for authenticated)
        if (user != null) {
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            // Should ideally not happen if user is authenticated, but handle defensively
            logger.error("Authenticated user with ID {} not found in service", userId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/me") // Update the current authenticated user's profile (partial or full)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<User> updateCurrentUser(@RequestBody User userDetails) {
        logger.info("Updating current authenticated user's profile");
        Long userId = getCurrentUserId(); // Get user ID from authenticated principal
        // Ensure the userDetails object is linked to the current user ID for the update
        userDetails.setId(userId); // Set the ID from the principal onto the object from the body
        // Service implementation should also verify that the update applies only to the user matching this ID
        try {
            User updatedUser = userService.partialUpdateUser(userId, userDetails); // Assuming partialUpdateUser handles update logic
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (RuntimeException e) {
            if (e.getMessage().equals("User not found")) { // Example error handling from original code
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            logger.error("Error updating profile for user {}: {}", userId, e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/me/profile-picture") // Update profile picture for the current authenticated user
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> updateCurrentUserProfilePicture(@RequestBody String profilePictureUrl) {
        logger.info("Updating profile picture for current authenticated user");
        Long userId = getCurrentUserId(); // Get user ID from authenticated principal
        try {
            userService.updateProfilePicture(userId, profilePictureUrl);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RuntimeException e) { // Example error handling from original code
            if (e.getMessage().equals("User not found")) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            logger.error("Error updating profile picture for user {}: {}", userId, e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/me/documents") // Add a document URL for the current authenticated user
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> addDocumentForCurrentUser(@RequestBody String documentUrl) {
        logger.info("Adding document for current authenticated user");
        Long userId = getCurrentUserId(); // Get user ID from authenticated principal
        userService.addDocument(userId, documentUrl);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/me/documents") // Remove a document URL for the current authenticated user
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> removeDocumentForCurrentUser(@RequestBody String documentUrl) {
        logger.info("Removing document for current authenticated user");
        Long userId = getCurrentUserId(); // Get user ID from authenticated principal
        userService.removeDocument(userId, documentUrl);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/me/documents") // Get document URLs for the current authenticated user
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<String>> getDocumentsForCurrentUser() {
        logger.info("Getting documents for current authenticated user");
        Long userId = getCurrentUserId(); // Get user ID from authenticated principal
        List<String> documents = userService.getDocuments(userId);
        return new ResponseEntity<>(documents, HttpStatus.OK);
    }


    // --- Admin Methods (by Explicit User ID or for all users) ---
    // Mapped to /api/v1/user/** (excluding /me/**) or specific subpaths like /all, /by-id/**

    // Note: If createUser is intended for public registration, move it to the Auth service or protect it with permitAll() in User Service SecurityConfig

    @PostMapping // Create a new user (likely Admin only, or public registration) - Let's assume Admin here based on typical CRUD controllers
    @PreAuthorize("hasRole('ADMIN')") // Assuming Admin for user creation via this endpoint
    public ResponseEntity<User> createUserAdmin(@RequestBody User user) {
        logger.info("Creating new user (Admin)");
        User createdUser = userService.createUser(user);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }


    @GetMapping("/all") // Get all users (Admin only) - New path for clarity
    @PreAuthorize("hasRole('ADMIN')") // Only ADMIN can get all users
    public ResponseEntity<List<User>> getAllUsersAdmin() {
        logger.info("Getting all users (Admin)");
        List<User> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/by-id/{id}") // Get a user by ID (Admin only) - New path for clarity
    @PreAuthorize("hasRole('ADMIN')") // Only ADMIN can get any user by ID
    public ResponseEntity<User> getUserByIdAdmin(@PathVariable Long id) {
        logger.info("Getting user by ID {} (Admin)", id);
        User user = userService.getUserById(id);
        if (user != null) {
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            logger.warn("User with ID {} not found (Admin)", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/by-id/{id}") // Update a user by ID (Admin only) - New path for clarity
    @PreAuthorize("hasRole('ADMIN')") // Only ADMIN can update any user by ID
    public ResponseEntity<User> updateUserAdmin(@PathVariable Long id, @RequestBody User userDetails) {
        logger.info("Updating user with ID {} (Admin)", id);
        // Ensure the ID from the path is used for the update target
        userDetails.setId(id); // Set the ID from the path onto the object from the body
        // Service implementation should also verify that the update applies only to the user matching this ID
        try {
            User updated = userService.partialUpdateUser(id, userDetails); // Assuming partialUpdateUser handles update logic
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (RuntimeException e) {
            if (e.getMessage().equals("User not found")) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            logger.error("Error updating user {}: {}", id, e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @DeleteMapping("/by-id/{id}") // Delete a user by ID (Admin only) - New path for clarity
    @PreAuthorize("hasRole('ADMIN')") // Only ADMIN can delete any user by ID
    public ResponseEntity<Void> deleteUserAdmin(@PathVariable Long id) {
        logger.info("Deleting user with ID {} (Admin)", id);
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/by-email/{email}") // Get a user by email (Admin only) - New path for clarity
    @PreAuthorize("hasRole('ADMIN')") // Only ADMIN can get user by email
    public ResponseEntity<User> getUserByEmailAdmin(@PathVariable String email) {
        logger.info("Getting user by email {} (Admin)", email);
        User user = userService.getUserByEmail(email);
        if (user != null) {
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            logger.warn("User with email {} not found (Admin)", email);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/by-id/{userId}/documents") // Add document for explicit User ID (Admin only) - New path for clarity
    @PreAuthorize("hasRole('ADMIN')") // Only ADMIN can add document for arbitrary user
    public ResponseEntity<Void> addDocumentByUserIdAdmin(
            @PathVariable Long userId, @RequestBody String documentUrl) {
        logger.info("Adding document for user ID {} (Admin)", userId);
        userService.addDocument(userId, documentUrl);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/by-id/{userId}/documents") // Remove document for explicit User ID (Admin only) - New path for clarity
    @PreAuthorize("hasRole('ADMIN')") // Only ADMIN can remove document for arbitrary user
    public ResponseEntity<Void> removeDocumentByUserIdAdmin(
            @PathVariable Long userId, @RequestBody String documentUrl) {
        logger.info("Removing document for user ID {} (Admin)", userId);
        userService.removeDocument(userId, documentUrl);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/by-id/{userId}/documents") // Get documents for explicit User ID (Admin only) - New path for clarity
    @PreAuthorize("hasRole('ADMIN')") // Only ADMIN can get documents for arbitrary user
    public ResponseEntity<List<String>> getDocumentsByUserIdAdmin(
            @PathVariable Long userId) {
        logger.info("Getting documents for user ID {} (Admin)", userId);
        List<String> documents = userService.getDocuments(userId);
        return new ResponseEntity<>(documents, HttpStatus.OK);
    }

    // --- Helper method to get the current authenticated user's ID ---
    // (Copied from previous controllers, assuming Principal name is String User ID)
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