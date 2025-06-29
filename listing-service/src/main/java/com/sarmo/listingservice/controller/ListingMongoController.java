package com.sarmo.listingservice.controller;

import com.sarmo.listingservice.entity.ListingMongo;
import com.sarmo.listingservice.service.ListingMongoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import org.springframework.security.access.prepost.PreAuthorize; // Import for method security

@RestController
@RequestMapping("/api/v1/listing/mongo") // URL path for Mongo listing controller
public class ListingMongoController {

    private final ListingMongoService listingMongoService;

    public ListingMongoController(ListingMongoService listingMongoService) {
        this.listingMongoService = listingMongoService;
    }

    // GET /api/v1/listing/mongo - Get all Mongo listings
    // Accessible to everyone (if URL /api/v1/listing/** or specific path is permitted in SecurityConfig)
    @GetMapping
    // @PreAuthorize("permitAll()") // Optional: Add if you want to explicitly mark public access here
    public ResponseEntity<List<ListingMongo>> getAllListings() {
        // Add logging here if needed
        List<ListingMongo> listings = listingMongoService.getAllListings();
        return ResponseEntity.ok(listings);
    }

    // GET /api/v1/listing/mongo/{id} - Get Mongo listing by its Mongo String ID
    // Accessible to everyone (if URL is permitted)
    @GetMapping("/{id}")
    // @PreAuthorize("permitAll()") // Optional: Add for explicit public access
    public ResponseEntity<ListingMongo> getListingById(@PathVariable String id) {
        // Add logging here if needed
        Optional<ListingMongo> listing = listingMongoService.getListingById(id);
        return listing.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST /api/v1/listing/mongo - Create a Mongo listing
    // Accessible only to Administrators (as likely an internal/admin operation)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')") // Requires ADMIN role
    public ResponseEntity<ListingMongo> createListing(@RequestBody ListingMongo listingMongo) {
        // Add logging here if needed
        try {
            ListingMongo createdListing = listingMongoService.createListing(listingMongo);
            return ResponseEntity.ok(createdListing);
        } catch (IllegalArgumentException e) {
            // Log the specific error e.getMessage()
            return ResponseEntity.badRequest().body(listingMongo);
        }
    }

    // PUT /api/v1/listing/mongo/{id} - Update a Mongo listing by its String ID
    // Accessible only to Administrators
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Requires ADMIN role
    public ResponseEntity<ListingMongo> updateListing(@PathVariable String id, @RequestBody ListingMongo updatedListingMongo) {
        // Add logging here if needed
        try {
            ListingMongo updatedListing = listingMongoService.updateListing(id, updatedListingMongo);
            if (updatedListing != null) {
                return ResponseEntity.ok(updatedListing);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (IllegalArgumentException e) {
            // Log the specific error e.getMessage()
            return ResponseEntity.badRequest().body(updatedListingMongo);
        }
    }

    // DELETE /api/v1/listing/mongo/{id} - Delete a Mongo listing by its String ID
    // Accessible only to Administrators
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Requires ADMIN role
    public ResponseEntity<Void> deleteListing(@PathVariable String id) {
        // Add logging here if needed
        listingMongoService.deleteListing(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/v1/listing/mongo/category/{categoryId} - Get Mongo listings by category ID
    // Accessible to everyone (if URL is permitted)
    @GetMapping("/category/{categoryId}")
    // @PreAuthorize("permitAll()") // Optional: Add for explicit public access
    public ResponseEntity<List<ListingMongo>> getListingsByCategoryId(@PathVariable Long categoryId) {
        // Add logging here if needed
        List<ListingMongo> listings = listingMongoService.getListingsByCategoryId(categoryId);
        return ResponseEntity.ok(listings);
    }

    // GET /api/v1/listing/mongo/listingId/{listingId} - Get Mongo listing by the main listing's Long ID
    // Accessible to everyone (if URL is permitted)
    @GetMapping("/listingId/{listingId}")
    // @PreAuthorize("permitAll()") // Optional: Add for explicit public access
    public ResponseEntity<ListingMongo> getListingByListingId(@PathVariable Long listingId) {
        // Add logging here if needed
        ListingMongo listing = listingMongoService.getListingByListingId(listingId);
        if (listing != null) {
            return ResponseEntity.ok(listing);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}