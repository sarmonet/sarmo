package com.sarmo.subscriptionservice.service;

import com.sarmo.subscriptionservice.entity.User;
import com.sarmo.subscriptionservice.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        logger.info("Retrieving all users.");
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        logger.info("Retrieving user by ID: {}", id);
        return userRepository.findById(id);
    }

    public User createUser(User user) {
        logger.info("Creating new user with ID: {}", user.getId());
        try {
            return userRepository.save(user);
        } catch (DataAccessException e) {
            logger.error("Error creating user with ID {}: {}", user.getId(), e.getMessage());
            return null;
        }
    }

    public User updateUser(Long id, User updatedUser) {
        logger.info("Updating user with ID: {}", id);
        try {
            return userRepository.findById(id)
                    .map(existingUser -> {
                        boolean updated = false;
                        if (updatedUser.getRegistrationDate() != null && !existingUser.getRegistrationDate().equals(updatedUser.getRegistrationDate())) {
                            existingUser.setRegistrationDate(updatedUser.getRegistrationDate());
                            updated = true;
                            logger.debug("Updated registration date to: {}", updatedUser.getRegistrationDate());
                        }
//                        if (updatedUser.getUserSubscriptions() != null && !existingUser.getUserSubscriptions().equals(updatedUser.getUserSubscriptions())) {
//                            existingUser.setUserSubscriptions(updatedUser.getUserSubscriptions());
//                            updated = true;
//                            logger.debug("Updated user subscriptions.");
//                        }
                        if (updatedUser.getActiveSubscription() != null && !existingUser.getActiveSubscription().equals(updatedUser.getActiveSubscription())) {
                            existingUser.setActiveSubscription(updatedUser.getActiveSubscription());
                            updated = true;
                            logger.debug("Updated active subscription ID to: {}", updatedUser.getActiveSubscription().getId());
                        }
                        if (updatedUser.getUserIndividualFeatures() != null && !existingUser.getUserIndividualFeatures().equals(updatedUser.getUserIndividualFeatures())) {
                            existingUser.setUserIndividualFeatures(updatedUser.getUserIndividualFeatures());
                            updated = true;
                            logger.debug("Updated user individual features.");
                        }
                        if (updated) {
                            return userRepository.save(existingUser);
                        } else {
                            logger.info("No changes for user with ID: {}", id);
                            return existingUser;
                        }
                    })
                    .orElseGet(() -> {
                        logger.warn("User with ID {} not found.", id);
                        return null;
                    });
        } catch (DataAccessException e) {
            logger.error("Error updating user with ID {}: {}", id, e.getMessage());
            return null;
        }
    }

    public void deleteUser(Long id) {
        logger.warn("Deleting user with ID: {}", id);
        try {
            userRepository.deleteById(id);
            logger.info("User with ID {} successfully deleted.", id);
        } catch (DataAccessException e) {
            logger.error("Error deleting user with ID {}: {}", id, e.getMessage());
        }
    }
}