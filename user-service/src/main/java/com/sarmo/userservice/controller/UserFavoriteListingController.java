package com.sarmo.userservice.controller;

import com.sarmo.userservice.entity.UserFavoriteListing;
import com.sarmo.userservice.service.interfaces.UserFavoriteListingService;
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
@RequestMapping("/api/v1/user/favorite/listing")
// PreAuthorize annotations are on method level for mixed access
public class UserFavoriteListingController {

    private final UserFavoriteListingService userFavoriteListingService;

    private static final Logger logger = LoggerFactory.getLogger(UserFavoriteListingController.class);

    public UserFavoriteListingController(UserFavoriteListingService userFavoriteListingService) {
        this.userFavoriteListingService = userFavoriteListingService;
    }

    // --- Methods for the current authenticated user (using principal ID) ---
    // Replaces functionality of UserFavoriteListingTokenController

    @PostMapping("/{listingId}") // Add favorite listing for the current user and a specific listing
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserFavoriteListing> addFavoriteListingForCurrentUser(
            @PathVariable Long listingId) {
        logger.info("Adding favorite listing for current user for listing {}", listingId);
        Long userId = getCurrentUserId(); // Get user ID from authenticated principal
        UserFavoriteListing createdListing = userFavoriteListingService.addFavoriteListing(userId, listingId);
        return new ResponseEntity<>(createdListing, HttpStatus.CREATED);
    }

    @GetMapping("/{listingId}") // Get a specific favorite listing entry for the current user and a specific listing
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserFavoriteListing> getUserFavoriteListingForCurrentUser(@PathVariable Long listingId) {
        logger.info("Getting favorite listing for current user for listing {}", listingId);
        Long userId = getCurrentUserId(); // Get user ID from authenticated principal
        UserFavoriteListing listing = userFavoriteListingService.getUserFavoriteListing(userId, listingId);
        if (listing != null) {
            return new ResponseEntity<>(listing, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{listingId}") // Remove favorite listing for the current user and a specific listing
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> removeFavoriteListingForCurrentUser(@PathVariable Long listingId) {
        logger.info("Removing favorite listing for current user for listing {}", listingId);
        Long userId = getCurrentUserId(); // Get user ID from authenticated principal
        userFavoriteListingService.removeFavoriteListing(userId, listingId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping // Get favorite listings WITH DETAILS for the current user
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Object>> getFavoriteListingsWithDetailsForCurrentUser() { // Renamed
        logger.info("Getting favorite listings with details for current user");
        Long userId = getCurrentUserId(); // Get user ID from authenticated principal
        List<Object> listings = userFavoriteListingService.getFavoriteListingsWithDetails(userId);
        if (listings.isEmpty()) {
            logger.info("No favorite listings with details found for current user {}", userId);
            return ResponseEntity.notFound().build();
        }
        logger.info("Found {} favorite listings with details for current user {}", listings.size(), userId);
        return ResponseEntity.ok(listings);
    }


    // --- Methods by Explicit User ID and Listing ID (Admin) ---
    // Replaces methods from the original UserFavoriteListingController
    // Paths changed for clarity

    @PostMapping("/by-user/{userId}/{listingId}") // Add favorite listing by explicit User ID and Listing ID
    @PreAuthorize("hasRole('ADMIN')") // Only ADMIN can add for arbitrary user/listing
    public ResponseEntity<UserFavoriteListing> addFavoriteListingByUserIdAndListingId(
            @PathVariable Long userId, @PathVariable Long listingId) {
        logger.info("Adding favorite listing for user {} and listing {} (Admin)", userId, listingId);
        UserFavoriteListing createdListing = userFavoriteListingService.addFavoriteListing(userId, listingId);
        return new ResponseEntity<>(createdListing, HttpStatus.CREATED);
    }

    @GetMapping("/by-user/{userId}/{listingId}") // Get a specific favorite listing entry by explicit User ID and Listing ID
    @PreAuthorize("hasRole('ADMIN')") // Only ADMIN can get for arbitrary user/listing
    public ResponseEntity<UserFavoriteListing> getUserFavoriteListingByUserIdAndListingId(
            @PathVariable Long userId, @PathVariable Long listingId) {
        logger.info("Getting favorite listing for user {} and listing {} (Admin)", userId, listingId);
        UserFavoriteListing listing = userFavoriteListingService.getUserFavoriteListing(userId, listingId);
        if (listing != null) {
            return new ResponseEntity<>(listing, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/by-user/{userId}/{listingId}") // Remove favorite listing by explicit User ID and Listing ID
    @PreAuthorize("hasRole('ADMIN')") // Only ADMIN can remove for arbitrary user/listing
    public ResponseEntity<Void> removeFavoriteListingByUserIdAndListingId(
            @PathVariable Long userId, @PathVariable Long listingId) {
        logger.info("Removing favorite listing for user {} and listing {} (Admin)", userId, listingId);
        userFavoriteListingService.removeFavoriteListing(userId, listingId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // --- Methods by Explicit User ID (Admin) ---
    // Replaces getFavoriteListings and getFavoriteListingsWithDetails from the original UserFavoriteListingController

    @GetMapping("/by-user/{userId}") // Get all favorite listing entries (simple list) by explicit User ID
    @PreAuthorize("hasRole('ADMIN')") // Only ADMIN can get all entries for arbitrary user
    public ResponseEntity<List<UserFavoriteListing>> getFavoriteListingsByUserId(@PathVariable Long userId) {
        logger.info("Getting favorite listings for user {} (Admin)", userId);
        List<UserFavoriteListing> listings = userFavoriteListingService.getFavoriteListings(userId);
        return new ResponseEntity<>(listings, HttpStatus.OK);
    }

    @GetMapping("/by-user/{userId}/details") // Get favorite listings WITH DETAILS by explicit User ID
    @PreAuthorize("hasRole('ADMIN')") // Only ADMIN can get details for arbitrary user
    public ResponseEntity<List<Object>> getFavoriteListingsWithDetailsByUserId(@PathVariable Long userId) {
        logger.info("Getting favorite listings with details for user {} (Admin)", userId);
        List<Object> listings = userFavoriteListingService.getFavoriteListingsWithDetails(userId);
        if (listings.isEmpty()) {
            logger.info("No favorite listings with details found for user {} (Admin)", userId);
            return ResponseEntity.notFound().build();
        }
        logger.info("Found {} favorite listings with details for user {} (Admin)", listings.size(), userId);
        return ResponseEntity.ok(listings);
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