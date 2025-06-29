package com.sarmo.subscriptionservice.service;

import com.sarmo.subscriptionservice.entity.SubscriptionFeature;
import com.sarmo.subscriptionservice.repository.SubscriptionFeatureRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SubscriptionFeatureService {

    private static final Logger logger = LoggerFactory.getLogger(SubscriptionFeatureService.class);

    private final SubscriptionFeatureRepository subscriptionFeatureRepository;

    public SubscriptionFeatureService(SubscriptionFeatureRepository subscriptionFeatureRepository) {
        this.subscriptionFeatureRepository = subscriptionFeatureRepository;
    }

    public List<SubscriptionFeature> getAllSubscriptionFeatures() {
        logger.info("Retrieving all subscription features.");
        return subscriptionFeatureRepository.findAll();
    }

    public Optional<SubscriptionFeature> getSubscriptionFeatureById(Long id) {
        logger.info("Retrieving subscription feature by ID: {}", id);
        return subscriptionFeatureRepository.findById(id);
    }

    public Optional<SubscriptionFeature> getSubscriptionFeatureByName(String name) {
        logger.info("Retrieving subscription feature by name: {}", name);
        return Optional.ofNullable(subscriptionFeatureRepository.findByName(name));
    }

    public SubscriptionFeature createSubscriptionFeature(SubscriptionFeature subscriptionFeature) {
        logger.info("Creating new subscription feature: {}", subscriptionFeature.getName());
        try {
            return subscriptionFeatureRepository.save(subscriptionFeature);
        } catch (DataAccessException e) {
            logger.error("Error creating subscription feature {}: {}", subscriptionFeature.getName(), e.getMessage());
            return null;
        }
    }

    public SubscriptionFeature updateSubscriptionFeature(Long id, SubscriptionFeature updatedFeature) {
        logger.info("Updating subscription feature with ID: {}", id);
        try {
            return subscriptionFeatureRepository.findById(id)
                    .map(existingFeature -> {
                        boolean updated = false;
                        if (updatedFeature.getName() != null && !existingFeature.getName().equals(updatedFeature.getName())) {
                            existingFeature.setName(updatedFeature.getName());
                            updated = true;
                            logger.debug("Updated name to: {}", updatedFeature.getName());
                        }
                        if (updatedFeature.getDisplayName() != null && !existingFeature.getDisplayName().equals(updatedFeature.getDisplayName())) {
                            existingFeature.setDisplayName(updatedFeature.getDisplayName());
                            updated = true;
                            logger.debug("Updated display name to: {}", updatedFeature.getDisplayName());
                        }
                        if (updatedFeature.getDescription() != null && !existingFeature.getDescription().equals(updatedFeature.getDescription())) {
                            existingFeature.setDescription(updatedFeature.getDescription());
                            updated = true;
                            logger.debug("Updated description.");
                        }
                        if (updatedFeature.getType() != null && !existingFeature.getType().equals(updatedFeature.getType())) {
                            existingFeature.setType(updatedFeature.getType());
                            updated = true;
                            logger.debug("Updated type to: {}", updatedFeature.getType());
                        }
                        if (updated) {
                            return subscriptionFeatureRepository.save(existingFeature);
                        } else {
                            logger.info("No changes for subscription feature with ID: {}", id);
                            return existingFeature;
                        }
                    })
                    .orElseGet(() -> {
                        logger.warn("Subscription feature with ID {} not found.", id);
                        return null;
                    });
        } catch (DataAccessException e) {
            logger.error("Error updating subscription feature with ID {}: {}", id, e.getMessage());
            return null;
        }
    }

    public void deleteSubscriptionFeature(Long id) {
        logger.warn("Deleting subscription feature with ID: {}", id);
        try {
            subscriptionFeatureRepository.deleteById(id);
            logger.info("Subscription feature with ID {} successfully deleted.", id);
        } catch (DataAccessException e) {
            logger.error("Error deleting subscription feature with ID {}: {}", id, e.getMessage());
        }
    }
}