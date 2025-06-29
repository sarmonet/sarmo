package com.sarmo.subscriptionservice.service;

import com.sarmo.subscriptionservice.entity.UserSubscription;
import com.sarmo.subscriptionservice.repository.UserSubscriptionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class UserSubscriptionService {

    private static final Logger logger = LoggerFactory.getLogger(UserSubscriptionService.class);

    private final UserSubscriptionRepository userSubscriptionRepository;

    public UserSubscriptionService(UserSubscriptionRepository userSubscriptionRepository) {
        this.userSubscriptionRepository = userSubscriptionRepository;
    }

    public List<UserSubscription> getAllUserSubscriptions() {
        logger.info("Retrieving all user subscriptions.");
        return userSubscriptionRepository.findAll();
    }

    public Optional<UserSubscription> getUserSubscriptionById(Long id) {
        logger.info("Retrieving user subscription by ID: {}", id);
        return userSubscriptionRepository.findById(id);
    }

    public List<UserSubscription> getUserSubscriptionsByUserId(Long userId) {
        logger.info("Retrieving user subscriptions by user ID: {}", userId);
        return userSubscriptionRepository.findByUserId(userId);
    }

    public Optional<UserSubscription> findActiveUserSubscription(Long userId) {
        logger.info("Finding active user subscription for user ID: {}", userId);
        return userSubscriptionRepository.findByUserIdAndEndDateAfterOrEndDateIsNull(userId, LocalDate.now());
    }

    public UserSubscription createUserSubscription(UserSubscription userSubscription) {
        logger.info("Creating new user subscription for user ID {} and plan ID {}",
                userSubscription.getUserId(), userSubscription.getSubscriptionPlan().getId());
        try {
            return userSubscriptionRepository.save(userSubscription);
        } catch (DataAccessException e) {
            logger.error("Error creating user subscription for user ID {} and plan ID {}: {}",
                    userSubscription.getUserId(), userSubscription.getSubscriptionPlan().getId(), e.getMessage());
            return null;
        }
    }

    public UserSubscription updateUserSubscription(Long id, UserSubscription updatedSubscription) {
        logger.info("Updating user subscription with ID: {}", id);
        try {
            return userSubscriptionRepository.findById(id)
                    .map(existingSubscription -> {
                        boolean updated = false;
                        if (updatedSubscription.getUserId() != null && !existingSubscription.getUserId().equals(updatedSubscription.getUserId())) {
                            existingSubscription.setUserId(updatedSubscription.getUserId());
                            updated = true;
                            logger.debug("Updated user ID to: {}", updatedSubscription.getUserId());
                        }
                        if (updatedSubscription.getSubscriptionPlan() != null && !existingSubscription.getSubscriptionPlan().equals(updatedSubscription.getSubscriptionPlan())) {
                            existingSubscription.setSubscriptionPlan(updatedSubscription.getSubscriptionPlan());
                            updated = true;
                            logger.debug("Updated subscription plan ID to: {}", updatedSubscription.getSubscriptionPlan().getId());
                        }
                        if (updatedSubscription.getStartDate() != null && !existingSubscription.getStartDate().equals(updatedSubscription.getStartDate())) {
                            existingSubscription.setStartDate(updatedSubscription.getStartDate());
                            updated = true;
                            logger.debug("Updated start date to: {}", updatedSubscription.getStartDate());
                        }
                        if (updatedSubscription.getEndDate() != null && (existingSubscription.getEndDate() == null || !existingSubscription.getEndDate().equals(updatedSubscription.getEndDate()))) {
                            existingSubscription.setEndDate(updatedSubscription.getEndDate());
                            updated = true;
                            logger.debug("Updated end date to: {}", updatedSubscription.getEndDate());
                        }
                        if (updatedSubscription.getStatus() != null && !existingSubscription.getStatus().equals(updatedSubscription.getStatus())) {
                            existingSubscription.setStatus(updatedSubscription.getStatus());
                            updated = true;
                            logger.debug("Updated status to: {}", updatedSubscription.getStatus());
                        }
                        if (updated) {
                            return userSubscriptionRepository.save(existingSubscription);
                        } else {
                            logger.info("No changes for user subscription with ID: {}", id);
                            return existingSubscription;
                        }
                    })
                    .orElseGet(() -> {
                        logger.warn("User subscription with ID {} not found.", id);
                        return null;
                    });
        } catch (DataAccessException e) {
            logger.error("Error updating user subscription with ID {}: {}", id, e.getMessage());
            return null;
        }
    }

    public void deleteUserSubscription(Long id) {
        logger.warn("Deleting user subscription with ID: {}", id);
        try {
            userSubscriptionRepository.deleteById(id);
            logger.info("User subscription with ID {} successfully deleted.", id);
        } catch (DataAccessException e) {
            logger.error("Error deleting user subscription with ID {}: {}", id, e.getMessage());
        }
    }
}