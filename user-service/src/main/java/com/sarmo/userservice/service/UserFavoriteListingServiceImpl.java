package com.sarmo.userservice.service;

import com.sarmo.userservice.entity.UserFavoriteListing;
import com.sarmo.userservice.entity.compositeKey.UserFavoriteListingId;
import com.sarmo.userservice.repository.UserFavoriteListingRepository;
import com.sarmo.userservice.service.interfaces.UserFavoriteListingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserFavoriteListingServiceImpl implements UserFavoriteListingService {

    private static final Logger logger = LoggerFactory.getLogger(UserFavoriteListingServiceImpl.class);

    private final UserFavoriteListingRepository userFavoriteListingRepository;
    private final RestClient restClient;

    public UserFavoriteListingServiceImpl(UserFavoriteListingRepository userFavoriteListingRepository, RestClient.Builder restClientBuilder) {
        this.userFavoriteListingRepository = userFavoriteListingRepository;
        this.restClient = restClientBuilder.baseUrl("http://listing-service:8083").build();
    }

    @Override
    public UserFavoriteListing addUserFavoriteListing(UserFavoriteListing userFavoriteListing) {
        try {
            logger.info("Adding user favorite listing: {}", userFavoriteListing);
            return userFavoriteListingRepository.save(userFavoriteListing);
        } catch (Exception e) {
            logger.error("Error adding user favorite listing: {}", e.getMessage());
            throw new RuntimeException("Failed to add user favorite listing", e);
        }
    }

    @Override
    public UserFavoriteListing getUserFavoriteListing(Long userId, Long listingId) {
        try {
            logger.debug("Getting user favorite listing for user {} and listing {}", userId, listingId);
            UserFavoriteListingId id = new UserFavoriteListingId(userId, listingId);
            Optional<UserFavoriteListing> userFavoriteListing = userFavoriteListingRepository.findById(id);
            return userFavoriteListing.orElse(null);
        } catch (Exception e) {
            logger.error("Error getting user favorite listing: {}", e.getMessage());
            throw new RuntimeException("Failed to get user favorite listing", e);
        }
    }

    @Override
    public void removeUserFavoriteListing(Long userId, Long listingId) {
        try {
            logger.info("Removing user favorite listing for user {} and listing {}", userId, listingId);
            UserFavoriteListingId id = new UserFavoriteListingId(userId, listingId);
            userFavoriteListingRepository.deleteById(id);
        } catch (Exception e) {
            logger.error("Error removing user favorite listing: {}", e.getMessage());
            throw new RuntimeException("Failed to remove user favorite listing", e);
        }
    }

    @Override
    public List<UserFavoriteListing> getUserFavoriteListingsByUserId(Long userId) {
        try {
            logger.debug("Getting user favorite listings by user id: {}", userId);
            return userFavoriteListingRepository.findByUserId(userId);
        } catch (Exception e) {
            logger.error("Error getting user favorite listings by user id: {}", e.getMessage());
            throw new RuntimeException("Failed to get user favorite listings by user id", e);
        }
    }

    @Override
    public UserFavoriteListing addFavoriteListing(Long userId, Long listingId) {
        try {
            logger.debug("Adding favorite listing for user {} and listing {}", userId, listingId);
            UserFavoriteListing userFavoriteListing = new UserFavoriteListing(userId, listingId);
            return userFavoriteListingRepository.save(userFavoriteListing);
        } catch (Exception e) {
            logger.error("Error adding favorite listing: {}", e.getMessage());
            throw new RuntimeException("Failed to add favorite listing", e);
        }
    }

    @Override
    public void removeFavoriteListing(Long userId, Long listingId) {
        try {
            logger.debug("Removing favorite listing for user {} and listing {}", userId, listingId);
            UserFavoriteListingId id = new UserFavoriteListingId(userId, listingId);
            userFavoriteListingRepository.deleteById(id);
        } catch (Exception e) {
            logger.error("Error removing favorite listing: {}", e.getMessage());
            throw new RuntimeException("Failed to remove favorite listing", e);
        }
    }

    @Override
    public List<UserFavoriteListing> getFavoriteListings(Long userId) {
        try {
            logger.debug("Getting favorite listings for user {}", userId);
            return userFavoriteListingRepository.findByUserId(userId);
        } catch (Exception e) {
            logger.error("Error getting favorite listings: {}", e.getMessage());
            throw new RuntimeException("Failed to get favorite listings", e);
        }
    }

    @Override
    public List<Object> getFavoriteListingsWithDetails(Long userId) {
        try {
            logger.debug("Getting favorite listings with details for user {}", userId);
            List<UserFavoriteListing> favoriteListings = userFavoriteListingRepository.findByUserId(userId);

            List<Long> listingIds = favoriteListings.stream()
                    .map(UserFavoriteListing::getListingId)
                    .collect(Collectors.toList());

            return restClient.post()
                    .uri("/api/v1/listing/by-ids")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(listingIds)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<Object>>() {});

        } catch (Exception e) {
            logger.error("Error getting favorite listings with details: {}", e.getMessage());
            throw new RuntimeException("Failed to get favorite listings with details", e);
        }
    }
}