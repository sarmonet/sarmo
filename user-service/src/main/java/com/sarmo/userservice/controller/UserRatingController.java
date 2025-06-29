package com.sarmo.userservice.controller;

import com.sarmo.userservice.entity.UserRating;
import com.sarmo.userservice.service.interfaces.UserRatingService;
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
@RequestMapping("/api/v1/user/rating")
// PreAuthorize annotations are on method level for mixed access
public class UserRatingController {

    private final UserRatingService userRatingService;

    private static final Logger logger = LoggerFactory.getLogger(UserRatingController.class);

    public UserRatingController(UserRatingService userRatingService) {
        this.userRatingService = userRatingService;
    }

    // --- Methods for the current authenticated user (using principal ID) ---
    // Replaces functionality of UserRatingTokenController

    @PostMapping // Create a user rating (the authenticated user is the author)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserRating> createUserRatingForCurrentUser(@RequestBody UserRating userRating) {
        logger.info("Creating user rating for current user");
        Long userId = getCurrentUserId(); // Get user ID from authenticated principal
        userRating.setUserId(userId); // Set the author of the rating to the current user
        // The userRating object from the body should contain the ratedUserId
        UserRating createdRating = userRatingService.createUserRating(userRating);
        return new ResponseEntity<>(createdRating, HttpStatus.CREATED);
    }

    @GetMapping("/{id}") // Get a user rating by ID, but ONLY if it belongs to the current user
    @PreAuthorize("isAuthenticated() and @userRatingService.isOwner(#id, authentication.name)") // Check if current user is the author of rating #id
    public ResponseEntity<UserRating> getUserRatingByIdIfOwner(@PathVariable Long id) {
        logger.info("Getting user rating by ID {} for owner check", id);
        // isOwner check is done by PreAuthorize. Just get the rating by ID.
        UserRating rating = userRatingService.getUserRatingById(id);
        // The service might return null if ID not found, handle this
        if (rating != null) {
            return new ResponseEntity<>(rating, HttpStatus.OK);
        } else {
            // This case might be redundant if isOwner check passes, but good practice
            logger.warn("User rating with ID {} not found for owner check", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping // Update a user rating (the authenticated user is the author)
    @PreAuthorize("isAuthenticated()") // Just require authentication; service must verify ownership using rating ID in body
    // Alternative (stricter check in PreAuthorize): @PreAuthorize("isAuthenticated() and @userRatingService.isOwner(userRating.id, authentication.name)")
    // The alternative requires the 'id' field to be set in the @RequestBody userRating object for PreAuthorize evaluation
    public ResponseEntity<UserRating> updateUserRatingForCurrentUser(@RequestBody UserRating userRating) {
        logger.info("Updating user rating for current user (ID {})", userRating.getId());
        Long userId = getCurrentUserId(); // Get user ID from authenticated principal
        userRating.setUserId(userId); // Ensure the rating object is linked to the current user ID before update
        // Service implementation should ALSO verify that the rating object being updated belongs to this user ID
        UserRating updatedRating = userRatingService.updateUserRating(userRating);
        if (updatedRating != null) {
            return new ResponseEntity<>(updatedRating, HttpStatus.OK);
        } else {
            // Could return HttpStatus.FORBIDDEN if service detects user mismatch
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}") // Delete a user rating by ID, but ONLY if it belongs to the current user
    @PreAuthorize("isAuthenticated() and @userRatingService.isOwner(#id, authentication.name)") // Check if current user is the author of rating #id
    public ResponseEntity<Void> deleteUserRatingByIdIfOwner(@PathVariable Long id) {
        logger.info("Deleting user rating by ID {} for owner check", id);
        // isOwner check is done by PreAuthorize. Just delete the rating by ID.
        userRatingService.deleteUserRating(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/to-me") // Get ratings given TO the current user
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<UserRating>> getUserRatingsGivenToCurrentUser() {
        logger.info("Getting ratings given TO the current user");
        Long ratedUserId = getCurrentUserId(); // Get user ID from authenticated principal (they are the one being rated)
        List<UserRating> ratings = userRatingService.getUserRatingsByRatedUserId(ratedUserId);
        return new ResponseEntity<>(ratings, HttpStatus.OK);
    }

    @GetMapping("/by-me") // Get ratings given BY the current user
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<UserRating>> getUserRatingsGivenByCurrentUser() {
        logger.info("Getting ratings given BY the current user");
        Long userId = getCurrentUserId(); // Get user ID from authenticated principal (they are the author of the ratings)
        List<UserRating> ratings = userRatingService.getUserRatingsByUserId(userId);
        return new ResponseEntity<>(ratings, HttpStatus.OK);
    }


    @GetMapping("/all") // Get all user ratings (Admin only) - New path
    @PreAuthorize("hasRole('ADMIN')") // Only ADMIN can get all ratings
    public ResponseEntity<List<UserRating>> getAllUserRatingsAdmin() {
        logger.info("Getting all user ratings (Admin)");
        List<UserRating> ratings = userRatingService.getAllUserRatings();
        return new ResponseEntity<>(ratings, HttpStatus.OK);
    }

    @GetMapping("/by-id/{id}") // Get a user rating by ID (Admin only) - New path
    @PreAuthorize("hasRole('ADMIN')") // Only ADMIN can get any rating by ID
    public ResponseEntity<UserRating> getUserRatingByIdAdmin(@PathVariable Long id) {
        logger.info("Getting user rating by ID {} (Admin)", id);
        UserRating rating = userRatingService.getUserRatingById(id);
        if (rating != null) {
            return new ResponseEntity<>(rating, HttpStatus.OK);
        } else {
            logger.warn("User rating with ID {} not found (Admin)", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/by-id/{id}") // Update a user rating by ID (Admin only) - New path
    @PreAuthorize("hasRole('ADMIN')") // Only ADMIN can update any rating by ID
    public ResponseEntity<UserRating> updateUserRatingByIdAdmin(@PathVariable Long id, @RequestBody UserRating userRatingDetails) {
        logger.info("Updating user rating by ID {} (Admin)", id);
        // Ensure the ID from the path is used for the update target
        userRatingDetails.setId(id); // Make sure service updates the rating with this ID
        UserRating updatedRating = userRatingService.updateUserRating(userRatingDetails);
        if (updatedRating != null) {
            return new ResponseEntity<>(updatedRating, HttpStatus.OK);
        } else {
            logger.warn("User rating with ID {} not found for update (Admin)", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @DeleteMapping("/by-id/{id}") // Delete a user rating by ID (Admin only) - New path
    @PreAuthorize("hasRole('ADMIN')") // Only ADMIN can delete any rating by ID
    public ResponseEntity<Void> deleteUserRatingByIdAdmin(@PathVariable Long id) {
        logger.info("Deleting user rating by ID {} (Admin)", id);
        userRatingService.deleteUserRating(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/by-rated-user/{ratedUserId}") // Get ratings given TO a specific user (Admin only) - New path
    @PreAuthorize("hasRole('ADMIN')") // Only ADMIN can get ratings given to any user
    public ResponseEntity<List<UserRating>> getUserRatingsByRatedUserIdAdmin(@PathVariable Long ratedUserId) {
        logger.info("Getting ratings given TO user {} (Admin)", ratedUserId);
        List<UserRating> ratings = userRatingService.getUserRatingsByRatedUserId(ratedUserId);
        return new ResponseEntity<>(ratings, HttpStatus.OK);
    }

    @GetMapping("/by-user/{userId}") // Get ratings given BY a specific user (Admin only) - New path
    @PreAuthorize("hasRole('ADMIN')") // Only ADMIN can get ratings given by any user
    public ResponseEntity<List<UserRating>> getUserRatingsByUserIdAdmin(@PathVariable Long userId) {
        logger.info("Getting ratings given BY user {} (Admin)", userId);
        List<UserRating> ratings = userRatingService.getUserRatingsByUserId(userId);
        return new ResponseEntity<>(ratings, HttpStatus.OK);
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