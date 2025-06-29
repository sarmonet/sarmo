package com.sarmo.userservice.service;

import com.sarmo.userservice.entity.UserRating;
import com.sarmo.userservice.repository.UserRatingRepository;
import com.sarmo.userservice.service.interfaces.UserRatingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserRatingServiceImpl implements UserRatingService {

    private static final Logger logger = LoggerFactory.getLogger(UserRatingServiceImpl.class);

    private final UserRatingRepository userRatingRepository;

    @Autowired
    public UserRatingServiceImpl(UserRatingRepository userRatingRepository) {
        this.userRatingRepository = userRatingRepository;
    }

    @Override
    public UserRating createUserRating(UserRating userRating) {
        try {
            logger.info("Creating user rating: {}", userRating);
            return userRatingRepository.save(userRating);
        } catch (Exception e) {
            logger.error("Error creating user rating: {}", e.getMessage());
            throw new RuntimeException("Failed to create user rating", e);
        }
    }

    @Override
    public UserRating getUserRatingById(Long id) {
        try {
            logger.debug("Getting user rating by id: {}", id);
            Optional<UserRating> userRating = userRatingRepository.findById(id);
            return userRating.orElse(null);
        } catch (Exception e) {
            logger.error("Error getting user rating by id {}: {}", id, e.getMessage());
            throw new RuntimeException("Failed to get user rating by id", e);
        }
    }

    @Override
    public List<UserRating> getAllUserRatings() {
        try {
            logger.info("Getting all user ratings");
            return userRatingRepository.findAll();
        } catch (Exception e) {
            logger.error("Error getting all user ratings: {}", e.getMessage());
            throw new RuntimeException("Failed to get all user ratings", e);
        }
    }

    @Override
    public UserRating updateUserRating(UserRating userRating) {
        try {
            logger.info("Updating user rating: {}", userRating);
            return userRatingRepository.save(userRating);
        } catch (Exception e) {
            logger.error("Error updating user rating: {}", e.getMessage());
            throw new RuntimeException("Failed to update user rating", e);
        }
    }

    @Override
    public void deleteUserRating(Long id) {
        try {
            logger.info("Deleting user rating by id: {}", id);
            userRatingRepository.deleteById(id);
        } catch (Exception e) {
            logger.error("Error deleting user rating by id {}: {}", id, e.getMessage());
            throw new RuntimeException("Failed to delete user rating by id", e);
        }
    }

    @Override
    public List<UserRating> getUserRatingsByRatedUserId(Long ratedUserId) {
        try {
            logger.debug("Getting user ratings by rated user id: {}", ratedUserId);
            return userRatingRepository.findByRatedUser_Id(ratedUserId);
        } catch (Exception e) {
            logger.error("Error getting user ratings by rated user id {}: {}", ratedUserId, e.getMessage());
            throw new RuntimeException("Failed to get user ratings by rated user id", e);
        }
    }

    @Override
    public List<UserRating> getUserRatingsByUserId(Long userId) {
        try {
            logger.debug("Getting user ratings by user id: {}", userId);
            return userRatingRepository.findByUserId(userId);
        } catch (Exception e) {
            logger.error("Error getting user ratings by user id {}: {}", userId, e.getMessage());
            throw new RuntimeException("Failed to get user ratings by user id", e);
        }
    }
}