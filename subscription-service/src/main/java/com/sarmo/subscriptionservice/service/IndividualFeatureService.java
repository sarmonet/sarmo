package com.sarmo.subscriptionservice.service;

import com.sarmo.subscriptionservice.entity.IndividualFeature;
import com.sarmo.subscriptionservice.repository.IndividualFeatureRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class IndividualFeatureService {

    private static final Logger logger = LoggerFactory.getLogger(IndividualFeatureService.class);

    private final IndividualFeatureRepository individualFeatureRepository;

    public IndividualFeatureService(IndividualFeatureRepository individualFeatureRepository) {
        this.individualFeatureRepository = individualFeatureRepository;
    }

    public List<IndividualFeature> getAllIndividualFeatures() {
        logger.info("Retrieving all individual features.");
        return individualFeatureRepository.findAll();
    }

    public Optional<IndividualFeature> getIndividualFeatureById(Long id) {
        logger.info("Retrieving individual feature by ID: {}", id);
        return individualFeatureRepository.findById(id);
    }

    public Optional<IndividualFeature> getIndividualFeatureByName(String name) {
        logger.info("Retrieving individual feature by name: {}", name);
        return Optional.ofNullable(individualFeatureRepository.findByName(name));
    }

    public IndividualFeature createIndividualFeature(IndividualFeature individualFeature) {
        logger.info("Creating new individual feature: {}", individualFeature.getName());
        try {
            return individualFeatureRepository.save(individualFeature);
        } catch (DataAccessException e) {
            logger.error("Error creating individual feature {}: {}", individualFeature.getName(), e.getMessage());
            // You might want to throw a custom exception here
            return null;
        }
    }

    public IndividualFeature updateIndividualFeature(Long id, IndividualFeature updatedFeature) {
        logger.info("Updating individual feature with ID: {}", id);
        try {
            return individualFeatureRepository.findById(id)
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
                        if (updatedFeature.getPrice() != null && !existingFeature.getPrice().equals(updatedFeature.getPrice())) {
                            existingFeature.setPrice(updatedFeature.getPrice());
                            updated = true;
                            logger.debug("Updated price to: {}", updatedFeature.getPrice());
                        }
                        if (updated) {
                            return individualFeatureRepository.save(existingFeature);
                        } else {
                            logger.info("No changes for individual feature with ID: {}", id);
                            return existingFeature;
                        }
                    })
                    .orElseGet(() -> {
                        logger.warn("Individual feature with ID {} not found.", id);
                        return null;
                    });
        } catch (DataAccessException e) {
            logger.error("Error updating individual feature with ID {}: {}", id, e.getMessage());
            return null;
        }
    }

    public void deleteIndividualFeature(Long id) {
        logger.warn("Deleting individual feature with ID: {}", id);
        try {
            individualFeatureRepository.deleteById(id);
            logger.info("Individual feature with ID {} successfully deleted.", id);
        } catch (DataAccessException e) {
            logger.error("Error deleting individual feature with ID {}: {}", id, e.getMessage());
            // You might want to throw a custom exception here
        }
    }
}