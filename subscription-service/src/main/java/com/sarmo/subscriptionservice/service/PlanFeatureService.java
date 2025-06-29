package com.sarmo.subscriptionservice.service;

import com.sarmo.subscriptionservice.entity.PlanFeature;
import com.sarmo.subscriptionservice.repository.PlanFeatureRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlanFeatureService {

    private static final Logger logger = LoggerFactory.getLogger(PlanFeatureService.class);

    private final PlanFeatureRepository planFeatureRepository;

    public PlanFeatureService(PlanFeatureRepository planFeatureRepository) {
        this.planFeatureRepository = planFeatureRepository;
    }

    public List<PlanFeature> getAllPlanFeatures() {
        logger.info("Retrieving all plan features.");
        return planFeatureRepository.findAll();
    }

    public Optional<PlanFeature> getPlanFeatureById(Long id) {
        logger.info("Retrieving plan feature by ID: {}", id);
        return planFeatureRepository.findById(id);
    }

    public List<PlanFeature> getPlanFeaturesByPlanId(Long planId) {
        logger.info("Retrieving plan features by plan ID: {}", planId);
        return planFeatureRepository.findBySubscriptionPlanId(planId);
    }

    public Optional<PlanFeature> getPlanFeatureByPlanIdAndFeatureName(Long planId, String featureName) {
        logger.info("Retrieving plan feature by plan ID {} and feature name: {}", planId, featureName);
        return Optional.ofNullable(planFeatureRepository.findBySubscriptionPlanIdAndSubscriptionFeatureName(planId, featureName));
    }

    public PlanFeature createPlanFeature(PlanFeature planFeature) {
        logger.info("Creating new plan feature for plan ID {} and feature ID {}",
                planFeature.getSubscriptionPlan().getId(), planFeature.getSubscriptionFeature().getId());
        try {
            return planFeatureRepository.save(planFeature);
        } catch (DataAccessException e) {
            logger.error("Error creating plan feature for plan ID {} and feature ID {}: {}",
                    planFeature.getSubscriptionPlan().getId(), planFeature.getSubscriptionFeature().getId(), e.getMessage());
            return null;
        }
    }

    public PlanFeature updatePlanFeature(Long id, PlanFeature updatedPlanFeature) {
        logger.info("Updating plan feature with ID: {}", id);
        try {
            return planFeatureRepository.findById(id)
                    .map(existingPlanFeature -> {
                        boolean updated = false;
                        if (updatedPlanFeature.getSubscriptionPlan() != null && !existingPlanFeature.getSubscriptionPlan().equals(updatedPlanFeature.getSubscriptionPlan())) {
                            existingPlanFeature.setSubscriptionPlan(updatedPlanFeature.getSubscriptionPlan());
                            updated = true;
                            logger.debug("Updated subscription plan ID to: {}", updatedPlanFeature.getSubscriptionPlan().getId());
                        }
                        if (updatedPlanFeature.getSubscriptionFeature() != null && !existingPlanFeature.getSubscriptionFeature().equals(updatedPlanFeature.getSubscriptionFeature())) {
                            existingPlanFeature.setSubscriptionFeature(updatedPlanFeature.getSubscriptionFeature());
                            updated = true;
                            logger.debug("Updated subscription feature ID to: {}", updatedPlanFeature.getSubscriptionFeature().getId());
                        }
                        if (updatedPlanFeature.getValue() != null && !existingPlanFeature.getValue().equals(updatedPlanFeature.getValue())) {
                            existingPlanFeature.setValue(updatedPlanFeature.getValue());
                            updated = true;
                            logger.debug("Updated value to: {}", updatedPlanFeature.getValue());
                        }
                        if (updatedPlanFeature.getUnit() != null && !existingPlanFeature.getUnit().equals(updatedPlanFeature.getUnit())) {
                            existingPlanFeature.setUnit(updatedPlanFeature.getUnit());
                            updated = true;
                            logger.debug("Updated unit to: {}", updatedPlanFeature.getUnit());
                        }
                        if (updated) {
                            return planFeatureRepository.save(existingPlanFeature);
                        } else {
                            logger.info("No changes for plan feature with ID: {}", id);
                            return existingPlanFeature;
                        }
                    })
                    .orElseGet(() -> {
                        logger.warn("Plan feature with ID {} not found.", id);
                        return null;
                    });
        } catch (DataAccessException e) {
            logger.error("Error updating plan feature with ID {}: {}", id, e.getMessage());
            return null;
        }
    }

    public void deletePlanFeature(Long id) {
        logger.warn("Deleting plan feature with ID: {}", id);
        try {
            planFeatureRepository.deleteById(id);
            logger.info("Plan feature with ID {} successfully deleted.", id);
        } catch (DataAccessException e) {
            logger.error("Error deleting plan feature with ID {}: {}", id, e.getMessage());
        }
    }
}