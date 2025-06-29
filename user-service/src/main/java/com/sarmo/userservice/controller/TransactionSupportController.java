package com.sarmo.userservice.controller;

import com.sarmo.userservice.entity.TransactionSupport;
import com.sarmo.userservice.service.interfaces.TransactionSupportService;
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
@RequestMapping("/api/v1/user/transaction-support")
// No class-level PreAuthorize here, mixed access requires method-level
public class TransactionSupportController { // Kept the original name

    private final TransactionSupportService transactionSupportService;

    private static final Logger logger = LoggerFactory.getLogger(TransactionSupportController.class); // Updated logger name

    public TransactionSupportController(TransactionSupportService transactionSupportService) {
        this.transactionSupportService = transactionSupportService;
    }

    // --- Methods for the current authenticated user (using principal ID) ---
    // Replaces functionality of TransactionSupportTokenController

    @PostMapping("/{listingId}") // Add support for the current user and a specific listing
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TransactionSupport> addTransactionSupportForCurrentUser(
            @PathVariable Long listingId, @RequestBody TransactionSupport transactionSupport) {
        logger.info("Adding transaction support for current user for listing {}", listingId);
        Long userId = getCurrentUserId(); // Get user ID from authenticated principal
        TransactionSupport createdSupport = transactionSupportService.addTransactionSupport(userId, listingId, transactionSupport);
        return new ResponseEntity<>(createdSupport, HttpStatus.CREATED);
    }

    @GetMapping("/{listingId}") // Get support for the current user and a specific listing
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TransactionSupport> getTransactionSupportForCurrentUser(@PathVariable Long listingId) {
        logger.info("Getting transaction support for current user for listing {}", listingId);
        Long userId = getCurrentUserId(); // Get user ID from authenticated principal
        TransactionSupport support = transactionSupportService.getTransactionSupport(userId, listingId);
        if (support != null) {
            return new ResponseEntity<>(support, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{listingId}") // Update support for the current user and a specific listing
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<TransactionSupport> updateTransactionSupportForCurrentUser(
            @PathVariable Long listingId, @RequestBody TransactionSupport transactionSupport) {
        logger.info("Updating transaction support for current user for listing {}", listingId);
        Long userId = getCurrentUserId(); // Get user ID from authenticated principal
        // Service should verify that the update is for the correct user/listing
        TransactionSupport updatedSupport = transactionSupportService.updateTransactionSupport(userId, listingId, transactionSupport);
        if (updatedSupport != null) {
            return new ResponseEntity<>(updatedSupport, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Or FORBIDDEN if not authorized by service
        }
    }

    @DeleteMapping("/{listingId}") // Remove support for the current user and a specific listing
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> removeTransactionSupportForCurrentUser(@PathVariable Long listingId) {
        logger.info("Removing transaction support for current user for listing {}", listingId);
        Long userId = getCurrentUserId(); // Get user ID from authenticated principal
        // Service should verify that the delete is for the correct user/listing
        transactionSupportService.removeTransactionSupport(userId, listingId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping // Get all support entries for the current user
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<TransactionSupport>> getTransactionSupportsForCurrentUser() {
        logger.info("Getting all transaction supports for current user");
        Long userId = getCurrentUserId(); // Get user ID from authenticated principal
        List<TransactionSupport> supports = transactionSupportService.getTransactionSupportsByUserId(userId);
        return new ResponseEntity<>(supports, HttpStatus.OK);
    }

    // --- Methods by Explicit User ID and Listing ID (Admin) ---
    // Replaces methods from the original TransactionSupportController
    // Paths changed for clarity

    @PostMapping("/by-user/{userId}/{listingId}") // Add support by explicit User ID and Listing ID
    @PreAuthorize("hasRole('ADMIN')") // Only ADMIN can add support for arbitrary user/listing
    public ResponseEntity<TransactionSupport> addTransactionSupportByUserIdAndListingId(
            @PathVariable Long userId, @PathVariable Long listingId, @RequestBody TransactionSupport transactionSupport) {
        logger.info("Adding transaction support for user {} and listing {} (Admin)", userId, listingId);
        TransactionSupport createdSupport = transactionSupportService.addTransactionSupport(userId, listingId, transactionSupport);
        return new ResponseEntity<>(createdSupport, HttpStatus.CREATED);
    }

    @GetMapping("/by-user/{userId}/{listingId}") // Get support by explicit User ID and Listing ID
    @PreAuthorize("hasRole('ADMIN')") // Only ADMIN can get support for arbitrary user/listing
    public ResponseEntity<TransactionSupport> getTransactionSupportByUserIdAndListingId(
            @PathVariable Long userId, @PathVariable Long listingId) {
        logger.info("Getting transaction support for user {} and listing {} (Admin)", userId, listingId);
        TransactionSupport support = transactionSupportService.getTransactionSupport(userId, listingId);
        if (support != null) {
            return new ResponseEntity<>(support, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/by-user/{userId}/{listingId}") // Update support by explicit User ID and Listing ID
    @PreAuthorize("hasRole('ADMIN')") // Only ADMIN can update support for arbitrary user/listing
    public ResponseEntity<TransactionSupport> updateTransactionSupportByUserIdAndListingId(
            @PathVariable Long userId, @PathVariable Long listingId, @RequestBody TransactionSupport transactionSupport) {
        logger.info("Updating transaction support for user {} and listing {} (Admin)", userId, listingId);
        TransactionSupport updatedSupport = transactionSupportService.updateTransactionSupport(userId, listingId, transactionSupport);
        if (updatedSupport != null) {
            return new ResponseEntity<>(updatedSupport, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/by-user/{userId}/{listingId}") // Remove support by explicit User ID and Listing ID
    @PreAuthorize("hasRole('ADMIN')") // Only ADMIN can remove support for arbitrary user/listing
    public ResponseEntity<Void> removeTransactionSupportByUserIdAndListingId(
            @PathVariable Long userId, @PathVariable Long listingId) {
        logger.info("Removing transaction support for user {} and listing {} (Admin)", userId, listingId);
        transactionSupportService.removeTransactionSupport(userId, listingId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // --- Methods by Explicit User ID (Admin) ---
    // Replaces getTransactionSupportsByUserId from the original TransactionSupportController

    @GetMapping("/by-user/{userId}") // Get all support entries by explicit User ID
    @PreAuthorize("hasRole('ADMIN')") // Only ADMIN can get all support entries for arbitrary user
    public ResponseEntity<List<TransactionSupport>> getTransactionSupportsByUserId(@PathVariable Long userId) {
        logger.info("Getting all transaction supports for user {} (Admin)", userId);
        List<TransactionSupport> supports = transactionSupportService.getTransactionSupportsByUserId(userId);
        return new ResponseEntity<>(supports, HttpStatus.OK);
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