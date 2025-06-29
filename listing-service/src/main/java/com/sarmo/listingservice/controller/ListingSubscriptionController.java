package com.sarmo.listingservice.controller;

import com.sarmo.listingservice.service.ListingSubscriptionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

import org.springframework.security.access.prepost.PreAuthorize; // Import for method security

@RestController
@RequestMapping("/api/v1/listing/{listingId}/subscription") // URL path for listing subscription controller
public class ListingSubscriptionController {

    private final ListingSubscriptionService subscriptionService;

    public ListingSubscriptionController(ListingSubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    // POST /api/v1/listing/{listingId}/subscription/add - Add a premium subscription
    // Accessible only to Administrators
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')") // Requires ADMIN role
    public ResponseEntity<Void> addPremiumSubscription(
            @PathVariable Long listingId,
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        // Add logging here if needed
        subscriptionService.addPremiumSubscription(listingId, startDate, endDate);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // DELETE /api/v1/listing/{listingId}/subscription/remove - Remove a premium subscription
    // Accessible only to Administrators
    @DeleteMapping("/remove")
    @PreAuthorize("hasRole('ADMIN')") // Requires ADMIN role
    public ResponseEntity<Void> removePremiumSubscription(@PathVariable Long listingId) {
        // Add logging here if needed
        subscriptionService.removePremiumSubscription(listingId);
        return ResponseEntity.noContent().build();
    }

    // GET /api/v1/listing/{listingId}/subscription/active - Check if premium is active for a listing
    // Accessible to everyone (if URL /api/v1/listing/** or specific path is permitted in SecurityConfig)
    @GetMapping("/active")
    // @PreAuthorize("permitAll()") // Optional: Add if you want to explicitly mark public access here
    public ResponseEntity<Boolean> isPremiumActive(@PathVariable Long listingId) {
        // Add logging here if needed
        boolean isActive = subscriptionService.isPremiumActive(listingId);
        return ResponseEntity.ok(isActive);
    }
}