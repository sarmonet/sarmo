package com.sarmo.listingservice.controller;

import com.sarmo.listingservice.dto.ListingDto;
import com.sarmo.listingservice.dto.UpdateListingDto;
import com.sarmo.listingservice.entity.Listing;
import com.sarmo.listingservice.service.ListingService;
import com.sarmo.listingservice.service.RedisViewCountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.sarmo.listingservice.dto.ListingFullInfoDto;
import com.sarmo.listingservice.dto.CreateListingDto;


import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


@RestController
@RequestMapping("/api/v1/listing") // Base path remains /api/v1/listing
public class ListingController {
    private final Logger logger = LoggerFactory.getLogger(ListingController.class);
    private final ListingService listingService;
    private final RedisViewCountService  redisViewCountService;



    // Update the constructor to remove JwtTokenDataExtractor
    public ListingController(ListingService listingService, RedisViewCountService redisViewCountService) {
        this.listingService = listingService;
        this.redisViewCountService = redisViewCountService;
    }

    @GetMapping
    // @PreAuthorize("permitAll()") // Optional: Add for explicit public access if URL rule isn't enough
    public ResponseEntity<Page<ListingDto>> getAllListings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "21") int size,
            @RequestParam(defaultValue = "createdat") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder
    ) {
        logger.info("GET /api/v1/listing - Getting all listings with pagination. Page: {}, Size: {}, Sort by: {}, Order: {}", page, size, sortBy, sortOrder);

        Page<ListingDto> listingsPage = listingService.getAllListings(page, size, sortBy, sortOrder);

        logger.info("GET /api/v1/listing - Found {} total listings across {} pages. Current page {} ({} elements).",
                listingsPage.getTotalElements(),
                listingsPage.getTotalPages(),
                listingsPage.getNumber() + 1,
                listingsPage.getNumberOfElements());

        return new ResponseEntity<>(listingsPage, HttpStatus.OK);
    }

    // GET /api/v1/listing/{id} - Get listing by ID
    // Accessible to everyone (if URL rule is permitAll())
    @GetMapping("/{id}")
    // @PreAuthorize("permitAll()") // Optional: Add for explicit public access
    public ResponseEntity<Listing> getListingById(@PathVariable Long id) {
        logger.info("GET /api/v1/listing/{} - Getting listing by id", id);
        Optional<Listing> listing = listingService.getListingById(id);
        return listing.map(ResponseEntity::ok)
                .orElseGet(() -> {
                    logger.warn("GET /api/v1/listing/{} - Listing not found", id);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                });
    }

    // GET /api/v1/listing/full/{id} - Get full listing info by ID + increment view count
    // Accessible to everyone (if URL rule is permitAll())
    @GetMapping("/full/{id}")
    // @PreAuthorize("permitAll()") // Optional: Add for explicit public access
    public ResponseEntity<ListingFullInfoDto> getFullListingById(@PathVariable Long id) {
        logger.info("GET /api/v1/listing/full/{} - Getting full listing by id", id);
        Optional<ListingFullInfoDto> listingFullInfoDto = listingService.getFullListingById(id);
        if (listingFullInfoDto.isPresent()) {
            redisViewCountService.incrementViewCount(id);
            logger.info("GET /api/v1/listing/full/{} - View count incremented for listing id", id);
        } else {
            logger.warn("GET /api/v1/listing/full/{} - Full listing not found", id);
        }
        return listingFullInfoDto.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST /api/v1/listing - Create listing (simple)
    // Accessible to any authenticated user (USER or ADMIN)
    // Get authenticated user's ID from SecurityContext
    @PostMapping
    @PreAuthorize("isAuthenticated()") // Or hasAnyRole('USER', 'ADMIN')
    public ResponseEntity<ListingFullInfoDto> createListing(@RequestBody CreateListingDto listing) {
        logger.info("POST /api/v1/listing - Received request to create listing");
        // Get user ID from security context and pass to service
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userIdString = authentication.getName(); // authentication.name is the Subject from JWT (String)
        Long userId = Long.parseLong(userIdString); // Convert String ID to Long

        // Service should create the listing, associating it with this userId
        // You need a service method like public Listing createListing(Long userId, Listing listing)

        ListingFullInfoDto createdListing = listingService.createListing(userId, listing);
        logger.info("POST /api/v1/listing - Created listing with id {} for user {}", createdListing.getId(), userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdListing);
    }

    // POST /api/v1/listing/full - Create full listing (includes Mongo data)
    // Accessible to any authenticated user (USER or ADMIN)
    // userId path variable is removed for security. Get userId from SecurityContext.
    @PostMapping("/full")
    @PreAuthorize("isAuthenticated()") // Or hasAnyRole('USER', 'ADMIN')
    public ResponseEntity<ListingFullInfoDto> createListingFullInfo(@RequestBody CreateListingDto createListingDto) {
        logger.info("POST /api/v1/listing/full - Received request to create full listing");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userIdString = authentication.getName();
        Long userId = Long.parseLong(userIdString); // Convert String ID to Long

        logger.info("POST /api/v1/listing/full - Creating listing for user {}", userId);
        // Service should create the listing and associate it with this userId
        // This calls your existing service method createListing(Long userId, CreateListingDto createListingDto)
        ListingFullInfoDto responseDto = listingService.createListing(userId, createListingDto);
        logger.info("POST /api/v1/listing/full - Listing created successfully: {}", responseDto.getId());
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/full/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ListingFullInfoDto> createListingFullInfoAdmin(@RequestBody CreateListingDto createListingDto, @PathVariable Long userId) {

        logger.info("POST /api/v1/listing/full/user/{} - Creating listing for user", userId);

        ListingFullInfoDto responseDto = listingService.createListing(userId, createListingDto);
        logger.info("POST /api/v1/listing/full/user/{} - Listing created successfully", responseDto.getId());
        return ResponseEntity.ok(responseDto);
    }

// PUT /api/v1/listing/{id} - Update listing (User/Owner)
    /**
     * Updates a listing by ID. Accessible only to the listing owner or Admin.
     * Expects updated listing details and potentially dynamic fields in the request body.
     * Returns the full updated listing information.
     *
     * @param id The ID of the listing to update.
     * @param listingDetailsDto The DTO containing updated listing details and dynamic fields.
     * @return ResponseEntity containing the updated ListingFullInfoDto.
     * @throws AccessDeniedException if the authenticated user is not the owner or Admin.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @listingService.isOwner(#id, authentication.name)")
    public ResponseEntity<ListingFullInfoDto> updateListing( // Changed return type ResponseEntity
                                                             @PathVariable Long id,
                                                             @RequestBody UpdateListingDto listingDetailsDto
    ) throws AccessDeniedException {
        logger.info("PUT /api/v1/listing/{} - Received request to update listing", id);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userIdString = authentication.getName();
        Long userId = Long.parseLong(userIdString);

        // Call the service method, which now returns ListingFullInfoDto
        ListingFullInfoDto updatedListingFullInfo = listingService.updateListing(userId, id, listingDetailsDto);
        logger.info("PUT /api/v1/listing/{} - Listing updated successfully, returning full info", id);
        // Return ResponseEntity with the new DTO
        return ResponseEntity.ok(updatedListingFullInfo);
    }

// PUT /api/v1/listing/{listingId}/user/{userId} - Update listing (Admin)
    /**
     * Updates a listing by ID for a specific user (Admin access only).
     * Expects updated listing details and potentially dynamic fields in the request body.
     * Returns the full updated listing information.
     *
     * @param listingId The ID of the listing to update.
     * @param listingDetailsDto The DTO containing updated listing details and dynamic fields.
     * @return ResponseEntity containing the updated ListingFullInfoDto.
     * @throws AccessDeniedException This method should ideally not throw AccessDeniedException due to @PreAuthorize, but the service method might.
     */
    @PutMapping("/admin/{listingId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ListingFullInfoDto> updateListingAdmin( // Changed return type ResponseEntity
                                                                  @PathVariable Long listingId,
                                                                  @RequestBody UpdateListingDto listingDetailsDto
    ) throws AccessDeniedException {
        logger.info("PUT /api/v1/listing/admin/{} - Received request to update listing", listingId);

        // Call the service method, which now returns ListingFullInfoDto
        ListingFullInfoDto updatedListingFullInfo = listingService.updateListing(listingId, listingDetailsDto);
        logger.info("PUT /api/v1/listing/admin/{} - Listing updated successfully, returning full info", listingId);
        // Return ResponseEntity with the new DTO
        return ResponseEntity.ok(updatedListingFullInfo);
    }


    // DELETE /api/v1/listing/{id} - Delete listing
    // Accessible only to Admin OR the listing owner
    // Get user ID from SecurityContext, not from path
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @listingService.isOwner(#id, authentication.name)") // Access check
    public ResponseEntity<Void> deleteListing(@PathVariable Long id) throws AccessDeniedException {
        logger.info("DELETE /api/v1/listing/{} - Received request to delete listing", id);

        // This calls your existing service method public void deleteListing(Long listingId)
        listingService.deleteListing(id);
        logger.info("DELETE /api/v1/listing/{} - Listing deleted successfully", id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/v1/listing/count - Count all listings
    // Accessible to everyone (if URL rule is permitAll())
    @GetMapping("/count")
    // @PreAuthorize("permitAll()") // Optional: Add for explicit public access
    public ResponseEntity<Long> countAllListings() {
        logger.info("GET /api/v1/listing/count - Counting all listings");
        long count = listingService.countAllListings();
        logger.info("GET /api/v1/listing/count - Total number of listings: {}", count);
        return ResponseEntity.ok(count);
    }

    // GET /api/v1/listing/count/category/{categoryId} - Count listings by category
    // Accessible to everyone (if URL rule is permitAll())
    @GetMapping("/count/category/{categoryId}")
    // @PreAuthorize("permitAll()") // Optional: Add for explicit public access
    public ResponseEntity<Long> countListingsByCategory(@PathVariable Long categoryId) {
        logger.info("GET /api/v1/listing/count/category/{} - Counting listings by category id", categoryId);
        long count = listingService.countListingsByCategory(categoryId);
        logger.info("GET /api/v1/listing/count/category/{} - Listing count for category: {}", categoryId, count);
        return ResponseEntity.ok(count);
    }

    // GET /api/v1/listing/count/subcategory/{subCategoryId} - Count listings by subcategory
    // Accessible to everyone (if URL rule is permitAll())
    @GetMapping("/count/subcategory/{subCategoryId}")
    // @PreAuthorize("permitAll()") // Optional: Add for explicit public access
    public ResponseEntity<Long> countListingsBySubCategory(@PathVariable Long subCategoryId) {
        logger.info("GET /api/v1/listing/count/subcategory/{} - Counting listings by subcategory id", subCategoryId);
        long count = listingService.countListingsBySubCategory(subCategoryId);
        logger.info("GET /api/v1/listing/count/subcategory/{} - Listing count for subcategory: {}", subCategoryId, count);
        return ResponseEntity.ok(count);
    }

    // PUT /api/v1/listing/{id}/images - Update listing images
    // Accessible only to Admin OR the listing owner
    // Remove userId from path for security. Get userId from SecurityContext.
    @PutMapping("/{id}/images")
    @PreAuthorize("hasRole('ADMIN') or @listingService.isOwner(#id, authentication.name)") // Access check
    public ResponseEntity<Listing> updateListingImages(@PathVariable Long id, @RequestBody List<String> images) throws AccessDeniedException {
        logger.info("Received request to update images for listing id {}", id);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userIdString = authentication.getName();
        Long userId = Long.parseLong(userIdString); // Convert String ID to Long

        // Pass user ID to the service
        // You need a service method like public Listing updateListingImages(Long userId, Long listingId, List<String> images)
        Listing updatedListing = listingService.updateListingImages(userId, id, images);
        logger.info("Images updated for listing id {}", id);
        return ResponseEntity.ok(updatedListing);
    }

    // PUT /api/v1/listing/{id}/main-image - Update listing main image
    // Accessible only to Admin OR the listing owner
    // Remove userId from path for security. Get userId from SecurityContext.
    @PutMapping("/{id}/main-image")
    @PreAuthorize("hasRole('ADMIN') or @listingService.isOwner(#id, authentication.name)") // Access check
    public ResponseEntity<Listing> updateListingMainImage(@PathVariable Long id, @RequestBody String image) throws AccessDeniedException {
        logger.info("Received request to update main image for listing id {}", id);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userIdString = authentication.getName();
        Long userId = Long.parseLong(userIdString); // Convert String ID to Long

        // Pass user ID to the service
        // You need a service method like public Listing updateListingMainImage(Long userId, Long listingId, String image)
        Listing updatedListing = listingService.updateListingMainImage(userId, id, image);
        logger.info("Main image updated for listing id {}", id);
        return ResponseEntity.ok(updatedListing);
    }

    // POST /api/v1/listing/by-ids - Get listings by a list of IDs
    // Accessible to everyone (if URL rule is permitAll())
    @PostMapping("/by-ids") // POST because the list of IDs is in the request body
    // @PreAuthorize("permitAll()") // Optional: Add for explicit public access
    public ResponseEntity<List<ListingDto>> getListingsByIds(@RequestBody List<Long> ids) {
        logger.info("POST /api/v1/listing/by-ids - Received request to get listings by ids: {}", ids);
        List<ListingDto> listings = listingService.getListingsByIds(ids);
        if (listings.isEmpty()) {
            logger.warn("POST /api/v1/listing/by-ids - No listings found for ids: {}", ids);
            return ResponseEntity.notFound().build();
        }
        logger.info("POST /api/v1/listing/by-ids - Found {} listings by ids", listings.size());
        return ResponseEntity.ok(listings);
    }

    // GET /api/v1/listing/inactive - Get all inactive listings with pagination
    // Accessible only to Admin
    @GetMapping("/inactive")
    @PreAuthorize("hasRole('ADMIN')") // Access check
    public ResponseEntity<Page<ListingDto>> getAllInactiveListings(
            @RequestParam(defaultValue = "0") int page, // Page number, 0-indexed, default 0
            @RequestParam(defaultValue = "21") int size, // Page size, default 21
            @RequestParam(defaultValue = "createdat") String sortBy, // Field to sort by, default "createdat"
            @RequestParam(defaultValue = "asc") String sortOrder // Sort order, default "asc"
    ) {
        logger.info("GET /api/v1/listing/inactive - Retrieving all inactive listings (admin only). " +
                        "Page: {}, Size: {}, Sort by: {}, Order: {}",
                page, size, sortBy, sortOrder);

        Page<ListingDto> inactiveListings = listingService.getAllInactiveListings(page, size, sortBy, sortOrder);

        logger.info("GET /api/v1/listing/inactive - Found {} inactive listings on current page, total: {}",
                inactiveListings.getContent().size(), inactiveListings.getTotalElements());
        return new ResponseEntity<>(inactiveListings, HttpStatus.OK);
    }

    // GET /api/v1/listing/active - Get all active listings with pagination
    @GetMapping("/active")
    public ResponseEntity<Page<ListingDto>> getAllActiveListings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "21") int size,
            @RequestParam(defaultValue = "createdat") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder
    ) {
        logger.info("GET /api/v1/listing/active - Retrieving all active listings. " +
                        "Page: {}, Size: {}, Sort by: {}, Order: {}",
                page, size, sortBy, sortOrder);

        Page<ListingDto> activeListings = listingService.getAllActiveListings(page, size, sortBy, sortOrder);

        logger.info("GET /api/v1/listing/active - Found {} active listings on current page, total: {}",
                activeListings.getContent().size(), activeListings.getTotalElements());
        return new ResponseEntity<>(activeListings, HttpStatus.OK);
    }

    // GET /api/v1/listing/rejected - Get all rejected listings with pagination
    @GetMapping("/rejected")
    public ResponseEntity<Page<ListingDto>> getAllRejectedListings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdat") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder
    ) {
        logger.info("GET /api/v1/listing/rejected - Retrieving all rejected listings. " +
                        "Page: {}, Size: {}, Sort by: {}, Order: {}",
                page, size, sortBy, sortOrder);

        Page<ListingDto> rejectedListings = listingService.getAllRejectedListings(page, size, sortBy, sortOrder);

        logger.info("GET /api/v1/listing/rejected - Found {} rejected listings on current page, total: {}",
                rejectedListings.getContent().size(), rejectedListings.getTotalElements());
        return new ResponseEntity<>(rejectedListings, HttpStatus.OK);
    }


    // GET /api/v1/listing/user/{userId}/inactive - Get inactive listings for a specific user
    // Accessible only to Admin OR the user themselves
    // #userId from path is compared with authenticated user ID from token
    @GetMapping("/user/{userId}/inactive") // userId here is the ID of the user whose listings are requested
    @PreAuthorize("hasRole('ADMIN') or #userId.toString() == authentication.name") // Access check: Admin OR requested ID matches ID from token
    public ResponseEntity<List<Listing>> getAllInactiveListingsByUserId(@PathVariable Long userId) {
        logger.info("GET /api/v1/listing/user/{}/inactive - Getting all inactive listings for user (admin or owner)", userId);
        // Service should get listings for the user with id=userId
        List<Listing> inactiveListings = listingService.getAllInactiveListingsByUserId(userId);
        logger.info("GET /api/v1/listing/user/{}/inactive - Found {} inactive listings for user", userId, inactiveListings.size());
        return new ResponseEntity<>(inactiveListings, HttpStatus.OK);
    }

    // --- Methods merged and updated from ListingTokenController ---

    // GET /api/v1/listing/user - Get all listings for the current authenticated user
    // Renamed from getAllListingsByUserToken (originally GET /api/v1/listing/token/user)
    @GetMapping("/user") // New URL: /api/v1/listing/user
    @PreAuthorize("isAuthenticated()") // Accessible to any authenticated user
    public ResponseEntity<List<Listing>> getAllListingsForAuthenticatedUser() { // New method name
        logger.info("GET /api/v1/listing/user - Getting all listings for authenticated user");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userIdString = authentication.getName(); // Get user ID string from token
        Long userId = Long.parseLong(userIdString); // Convert to Long

        // Call service method to get listings by userId
        List<Listing> listings = listingService.getAllListingsByUserId(userId);
        logger.info("GET /api/v1/listing/user - Found {} listings for user {}", listings.size(), userId);
        return new ResponseEntity<>(listings, HttpStatus.OK);
    }

    // GET /api/v1/listing/my-inactive - Get all inactive listings for the current authenticated user
    // Renamed from getAllInactiveListingsByUserToken (originally GET /api/v1/listing/token/user/inactive)
    // Added a specific path for clarity, distinct from /user/{userId}/inactive
    @GetMapping("/my-inactive") // New URL: /api/v1/listing/my-inactive
    @PreAuthorize("isAuthenticated()") // Accessible to any authenticated user (to see their *own*)
    public ResponseEntity<List<Listing>> getAllInactiveListingsForAuthenticatedUser() { // New method name
        logger.info("GET /api/v1/listing/my-inactive - Getting all inactive listings for authenticated user");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userIdString = authentication.getName(); // Get user ID string from token
        Long userId = Long.parseLong(userIdString); // Convert to Long

        // Call service method to get inactive listings by userId
        List<Listing> inactiveListings = listingService.getAllInactiveListingsByUserId(userId);
        logger.info("GET /api/v1/listing/my-inactive - Found {} inactive listings for user {}", inactiveListings.size(), userId);
        return new ResponseEntity<>(inactiveListings, HttpStatus.OK);
    }


    // GET /api/v1/listing/{id}/owner - Get a specific listing, accessible only if the authenticated user is the owner
    // Renamed from getListingByIdAndToken (originally GET /api/v1/listing/token/{id}/user)
    // URL path /{id}/user changed to /{id}/owner for clarity
    @GetMapping("/{id}/owner") // New URL: /api/v1/listing/{id}/owner
    @PreAuthorize("@listingService.isOwner(#id, authentication.name)") // Access check: only owner (using service method and token ID)
    public ResponseEntity<Listing> getListingByIdIfOwner(@PathVariable Long id) {
        logger.info("GET /api/v1/listing/{}/owner - Getting listing for owner check", id);
        // Ownership check is done by @PreAuthorize. Just get the listing by ID.
        // Use existing getListingById method which returns Optional<Listing>
        Optional<Listing> listing = listingService.getListingById(id);

        return listing.map(ResponseEntity::ok)
                .orElseGet(() -> {
                    logger.warn("GET /api/v1/listing/{}/owner - Listing not found during owner retrieval", id);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                });
    }
}