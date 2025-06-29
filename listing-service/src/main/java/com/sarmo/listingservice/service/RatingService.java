package com.sarmo.listingservice.service;

import com.sarmo.listingservice.dto.RatingStatsDto;
import com.sarmo.listingservice.entity.Listing;
import com.sarmo.listingservice.entity.Rating;
import com.sarmo.listingservice.repository.ListingRepository;
import com.sarmo.listingservice.repository.RatingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RatingService {

    private static final Logger logger = LoggerFactory.getLogger(RatingService.class);

    private final RatingRepository ratingRepository;
    private final ListingRepository listingRepository;

    public RatingService(RatingRepository ratingRepository, ListingRepository listingRepository) {
        this.ratingRepository = ratingRepository;
        this.listingRepository = listingRepository;
    }

    public boolean isRatingOwner(Long ratingId, String userIdString) {
        logger.debug("Checking ownership for rating {} by user (string) {}", ratingId, userIdString);

        if (userIdString == null) {
            logger.debug("userIdString is null, cannot check ownership for rating.");
            return false; // Cannot be an owner if user ID is unknown
        }

        // 1. Find the rating by its ID
        Optional<Rating> ratingOptional = ratingRepository.findById(ratingId);

        if (ratingOptional.isPresent()) {
            Rating rating = ratingOptional.get();

            // Проверка на случай, если у рейтинга по какой-то причине не установлен userId (хотя поле @NotNull)
            if (rating.getUserId() == null) {
                logger.warn("Rating {} found but has no userId assigned (should not happen based on @NotNull).", ratingId);
                return false; // У рейтинга нет владельца (хотя должно быть), пользователь не может быть им
            }

            // 2. Получить Long ID владельца рейтинга из поля userId сущности Rating
            Long ownerUserIdLong = rating.getUserId();

            // 3. Сравнить Long ownerUserId (преобразованный в String) с предоставленным String ID пользователя из токена
            boolean isActualOwner = String.valueOf(ownerUserIdLong).equals(userIdString);

            if (isActualOwner) {
                logger.debug("User (string) {} IS owner of rating {}", userIdString, ratingId);
            } else {
                logger.debug("User (string) {} is NOT owner of rating {}. Actual owner userId: {}", userIdString, ratingId, ownerUserIdLong);
            }

            return isActualOwner;

        } else {
            logger.warn("Rating with id {} not found during ownership check", ratingId);
            // Рейтинг не найден. Пользователь не может быть его владельцем.
            return false;
        }
    }

    @Transactional
    public Rating addRating(Long listingId, Long userId, Integer value) {
        logger.info("Adding rating for listingId: {} with value: {}", listingId, value);

        if (value == null || value < 1 || value > 5) {
            logger.error("Invalid rating value: {}. Value must be between 1 and 5.", value);
            throw new IllegalArgumentException("Rating value must be between 1 and 5.");
        }

        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> {
                    logger.error("Listing not found for listingId: {}", listingId);
                    return new RuntimeException("Listing not found");
                });

        Rating rating = new Rating(userId, listing, value);
        Rating savedRating = ratingRepository.save(rating);

        logger.info("Successfully added rating with id: {}", savedRating.getId());
        return savedRating;
    }

    public Rating getRatingById(Long ratingId) {
        logger.info("Getting rating by id: {}", ratingId);
        return ratingRepository.findById(ratingId)
                .orElseThrow(() -> new RuntimeException("Rating not found"));
    }

    public List<Rating> getRatingsByListingId(Long listingId) {
        logger.info("Getting ratings by listingId: {}", listingId);
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new RuntimeException("Listing not found"));
        return listing.getRatings();
    }

    public Double getAverageRatingForListing(Long listingId) {
        logger.info("Getting average rating for listingId: {}", listingId);
        List<Rating> ratings = getRatingsByListingId(listingId);
        if (ratings.isEmpty()) {
            return 0.0;
        }
        return ratings.stream()
                .mapToInt(Rating::getValue)
                .average()
                .orElse(0.0);
    }

    @Transactional
    public void deleteRating(Long ratingId, Long userId) {
        logger.info("Deleting rating by id: {}", ratingId);
        Rating rating = getRatingById(ratingId);

        if (!rating.getUserId().equals(userId)) {
            logger.error("User with id: {} is not authorized to delete rating with id: {}", userId, ratingId);
            throw new RuntimeException("Unauthorized to delete rating");
        }

        ratingRepository.deleteById(ratingId);
        logger.info("Rating with id: {} deleted by user with id: {}", ratingId, userId);
    }

    @Transactional
    public Rating updateRating(Long ratingId, Integer value, Long userId) {
        logger.info("Updating rating id: {} with value: {}", ratingId, value);
        Rating rating = getRatingById(ratingId);

        if (!rating.getUserId().equals(userId)) {
            logger.error("User with id: {} is not authorized to update rating with id: {}", userId, ratingId);
            throw new RuntimeException("Unauthorized to update rating");
        }

        rating.setValue(value);
        logger.info("Rating with id: {} updated by user with id: {}", ratingId, userId);
        return ratingRepository.save(rating);
    }

    public RatingStatsDto getRatingStatsForListing(Long listingId){
        logger.info("Getting rating stats for listing id: {}", listingId);
        List<Rating> ratings = getRatingsByListingId(listingId);
        Double averageRating = getAverageRatingForListing(listingId);
        return new RatingStatsDto(averageRating, (long) ratings.size());
    }
}