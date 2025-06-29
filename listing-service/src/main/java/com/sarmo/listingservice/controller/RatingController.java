package com.sarmo.listingservice.controller;

import com.sarmo.listingservice.dto.CreateRatingDto;
import com.sarmo.listingservice.dto.RatingStatsDto;
import com.sarmo.listingservice.dto.UpdateRatingDto;
import com.sarmo.listingservice.entity.Rating;
import com.sarmo.listingservice.service.RatingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


@RestController
@RequestMapping("/api/v1/listing")
public class RatingController {

    private final RatingService ratingService;

    private static final Logger logger = LoggerFactory.getLogger(RatingController.class);

    // Обновите конструктор
    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;

    }

    // POST /api/v1/listing/{listingId}/ratings - Add a rating
    // Accessible only to authenticated users
    @PostMapping("/{listingId}/ratings")
    @PreAuthorize("isAuthenticated()") // Only authenticated users can add ratings
    public ResponseEntity<Rating> addRating(
            @PathVariable Long listingId,
            @RequestBody CreateRatingDto createRatingDto) { // Remove authorizationHeader

        logger.info("POST /api/v1/listing/{}/ratings - Adding rating for listingId", listingId);

        // Get user ID from SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userIdString = authentication.getName(); // Subject from JWT as String
        Long userId = Long.parseLong(userIdString); // Convert to Long

        // Call service method, passing the user ID
        Rating rating = ratingService.addRating(listingId, userId, createRatingDto.getValue());
        logger.info("POST /api/v1/listing/{}/ratings - Rating added with id {} by user id {}", listingId, rating.getId(), userId);
        return new ResponseEntity<>(rating, HttpStatus.CREATED);
    }

    // DELETE /api/v1/listing/ratings/{ratingId} - Delete a rating
    // Accessible only to Admin OR the rating owner
    @DeleteMapping("/ratings/{ratingId}")
    @PreAuthorize("hasRole('ADMIN') or @ratingService.isRatingOwner(#ratingId, authentication.name)") // Access check
    public ResponseEntity<Void> deleteRating(
            @PathVariable Long ratingId) { // Remove authorizationHeader

        logger.info("DELETE /api/v1/listing/ratings/{} - Deleting rating by id", ratingId);

        // Get user ID from SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userIdString = authentication.getName(); // Subject from JWT as String
        Long userId = Long.parseLong(userIdString); // Convert to Long

        // Call service method, passing the user ID
        // Assuming ratingService.deleteRating method accepts userId to verify ownership inside (or trust PreAuthorize)
        ratingService.deleteRating(ratingId, userId);
        logger.info("DELETE /api/v1/listing/ratings/{} - Rating deleted by user id {}", ratingId, userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // PUT /api/v1/listing/ratings/{ratingId} - Update a rating
    // Accessible only to Admin OR the rating owner
    @PutMapping("/ratings/{ratingId}")
    @PreAuthorize("hasRole('ADMIN') or @ratingService.isRatingOwner(#ratingId, authentication.name)") // Access check
    public ResponseEntity<Rating> updateRating(
            @PathVariable Long ratingId,
            @RequestBody UpdateRatingDto updateRatingDto) { // Remove authorizationHeader

        logger.info("PUT /api/v1/listing/ratings/{} - Updating rating by id", ratingId);

        // Get user ID from SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userIdString = authentication.getName(); // Subject from JWT as String
        Long userId = Long.parseLong(userIdString); // Convert to Long

        // Call service method, passing the user ID
        // Assuming ratingService.updateRating method accepts userId to verify ownership inside (or trust PreAuthorize)
        Rating rating = ratingService.updateRating(ratingId, updateRatingDto.getValue(), userId);
        logger.info("PUT /api/v1/listing/ratings/{} - Rating updated by user id {}", ratingId, userId);
        return new ResponseEntity<>(rating, HttpStatus.OK);
    }

    // GET /api/v1/listing/ratings/{ratingId} - Get a rating by ID
    // Accessible to everyone (if URL rule is permitAll())
    @GetMapping("/ratings/{ratingId}")
    // @PreAuthorize("permitAll()") // Optional: Add for explicit public access
    public ResponseEntity<Rating> getRatingById(@PathVariable Long ratingId) {
        logger.info("GET /api/v1/listing/ratings/{} - Getting rating by id", ratingId);
        Rating rating = ratingService.getRatingById(ratingId);
        logger.info("GET /api/v1/listing/ratings/{} - Found rating", ratingId);
        return new ResponseEntity<>(rating, HttpStatus.OK);
    }

    // GET /api/v1/listing/{listingId}/ratings - Get all ratings for a listing
    // Accessible to everyone (if URL rule is permitAll())
    @GetMapping("/{listingId}/ratings")
    // @PreAuthorize("permitAll()") // Optional: Add for explicit public access
    public ResponseEntity<List<Rating>> getRatingsByListingId(@PathVariable Long listingId) {
        logger.info("GET /api/v1/listing/{}/ratings - Getting ratings for listingId", listingId);
        List<Rating> ratings = ratingService.getRatingsByListingId(listingId);
        logger.info("GET /api/v1/listing/{}/ratings - Found {} ratings", listingId, ratings.size());
        return new ResponseEntity<>(ratings, HttpStatus.OK);
    }

    // GET /api/v1/listing/{listingId}/ratings/average - Get average rating for a listing
    // Accessible to everyone (if URL rule is permitAll())
    @GetMapping("/{listingId}/ratings/average")
    // @PreAuthorize("permitAll()") // Optional: Add for explicit public access
    public ResponseEntity<Double> getAverageRatingForListing(@PathVariable Long listingId) {
        logger.info("GET /api/v1/listing/{}/ratings/average - Getting average rating for listingId", listingId);
        Double averageRating = ratingService.getAverageRatingForListing(listingId);
        logger.info("GET /api/v1/listing/{}/ratings/average - Average rating: {}", listingId, averageRating);
        return new ResponseEntity<>(averageRating, HttpStatus.OK);
    }

    // GET /api/v1/listing/{listingId}/ratings/stats - Get rating stats for a listing
    // Accessible to everyone (if URL rule is permitAll())
    @GetMapping("/{listingId}/ratings/stats")
    // @PreAuthorize("permitAll()") // Optional: Add for explicit public access
    public ResponseEntity<RatingStatsDto> getRatingStatsForListing(@PathVariable Long listingId) {
        logger.info("GET /api/v1/listing/{}/ratings/stats - Getting rating stats for listingId", listingId);
        RatingStatsDto ratingStatsDto = ratingService.getRatingStatsForListing(listingId);
        logger.info("GET /api/v1/listing/{}/ratings/stats - Got stats for listingId", listingId);
        return new ResponseEntity<>(ratingStatsDto, HttpStatus.OK);
    }
}