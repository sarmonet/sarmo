package com.sarmo.subscriptionservice.service;

import com.sarmo.subscriptionservice.entity.UserIndividualFeature;
import com.sarmo.subscriptionservice.repository.UserIndividualFeatureRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserIndividualFeatureService {

    private static final Logger logger = LoggerFactory.getLogger(UserIndividualFeatureService.class);

    private final UserIndividualFeatureRepository userIndividualFeatureRepository;

    public UserIndividualFeatureService(UserIndividualFeatureRepository userIndividualFeatureRepository) {
        this.userIndividualFeatureRepository = userIndividualFeatureRepository;
    }

    public List<UserIndividualFeature> getAllUserIndividualFeatures() {
        logger.info("Retrieving all user individual features.");
        return userIndividualFeatureRepository.findAll();
    }

    public Optional<UserIndividualFeature> getUserIndividualFeatureById(Long id) {
        logger.info("Retrieving user individual feature by ID: {}", id);
        return userIndividualFeatureRepository.findById(id);
    }

    public List<UserIndividualFeature> getUserIndividualFeaturesByUserId(Long userId) {
        logger.info("Retrieving user individual features by user ID: {}", userId);
        return userIndividualFeatureRepository.findByUserId(userId);
    }

    public UserIndividualFeature createUserIndividualFeature(UserIndividualFeature userIndividualFeature) {
        logger.info("Creating new user individual feature for user ID {} and feature ID {}",
                userIndividualFeature.getUser().getId(), userIndividualFeature.getIndividualFeature().getId());
        try {
            return userIndividualFeatureRepository.save(userIndividualFeature);
        } catch (DataAccessException e) {
            logger.error("Error creating user individual feature for user ID {} and feature ID {}: {}",
                    userIndividualFeature.getUser().getId(), userIndividualFeature.getIndividualFeature().getId(), e.getMessage());
            return null;
        }
    }

    public UserIndividualFeature updateUserIndividualFeature(Long id, UserIndividualFeature updatedFeature) {
        logger.info("Updating user individual feature with ID: {}", id);
        try {
            return userIndividualFeatureRepository.findById(id)
                    .map(existingFeature -> {
                        boolean updated = false;
                        if (updatedFeature.getUser() != null && !existingFeature.getUser().equals(updatedFeature.getUser())) {
                            existingFeature.setUser(updatedFeature.getUser());
                            updated = true;
                            logger.debug("Updated user ID to: {}", updatedFeature.getUser().getId());
                        }
                        if (updatedFeature.getIndividualFeature() != null && !existingFeature.getIndividualFeature().equals(updatedFeature.getIndividualFeature())) {
                            existingFeature.setIndividualFeature(updatedFeature.getIndividualFeature());
                            updated = true;
                            logger.debug("Updated individual feature ID to: {}", updatedFeature.getIndividualFeature().getId());
                        }
                        if (updatedFeature.getPurchaseDate() != null && !existingFeature.getPurchaseDate().equals(updatedFeature.getPurchaseDate())) {
                            existingFeature.setPurchaseDate(updatedFeature.getPurchaseDate());
                            updated = true;
                            logger.debug("Updated purchase date to: {}", updatedFeature.getPurchaseDate());
                        }
                        if (updatedFeature.getExpirationDate() != null && (existingFeature.getExpirationDate() == null || !existingFeature.getExpirationDate().equals(updatedFeature.getExpirationDate()))) {
                            existingFeature.setExpirationDate(updatedFeature.getExpirationDate());
                            updated = true;
                            logger.debug("Updated expiration date to: {}", updatedFeature.getExpirationDate());
                        }
                        if (updatedFeature.getStatus() != null && !existingFeature.getStatus().equals(updatedFeature.getStatus())) {
                            existingFeature.setStatus(updatedFeature.getStatus());
                            updated = true;
                            logger.debug("Updated status to: {}", updatedFeature.getStatus());
                        }
                        if (updatedFeature.getAdditionalInfo() != null && !existingFeature.getAdditionalInfo().equals(updatedFeature.getAdditionalInfo())) {
                            existingFeature.setAdditionalInfo(updatedFeature.getAdditionalInfo());
                            updated = true;
                            logger.debug("Updated additional info.");
                        }
                        if (updated) {
                            return userIndividualFeatureRepository.save(existingFeature);
                        } else {
                            logger.info("No changes for user individual feature with ID: {}", id);
                            return existingFeature;
                        }
                    })
                    .orElseGet(() -> {
                        logger.warn("User individual feature with ID {} not found.", id);
                        return null;
                    });
        } catch (DataAccessException e) {
            logger.error("Error updating user individual feature with ID {}: {}", id, e.getMessage());
            return null;
        }
    }

    public void deleteUserIndividualFeature(Long id) {
        logger.warn("Deleting user individual feature with ID: {}", id);
        try {
            userIndividualFeatureRepository.deleteById(id);
            logger.info("User individual feature with ID {} successfully deleted.", id);
        } catch (DataAccessException e) {
            logger.error("Error deleting user individual feature with ID {}: {}", id, e.getMessage());
        }
    }
}