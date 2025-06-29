package com.sarmo.contentservice.consumer;

import com.sarmo.contentservice.entity.User;
import com.sarmo.contentservice.repository.UserRepository;
import com.sarmo.kafka.dto.UserRegistrationData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserUpdateConsumer {

    private static final Logger logger = LoggerFactory.getLogger(UserUpdateConsumer.class);
    private final UserRepository userRepository;

    public UserUpdateConsumer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Listens to the "user-update" Kafka topic and updates user information
     * in the content-service's database.
     *
     * @param data The UserRegistrationData object containing the updated user details.
     */
    @KafkaListener(topics = "user-update", groupId = "content-service-group")
    public void consumeUserUpdateMessage(UserRegistrationData data) {
        logger.info("Received user update message: {}", data);
        try {
            // Find the existing user by ID from the message
            Optional<User> existingUserOptional = userRepository.findById(data.getUserId());

            if (existingUserOptional.isPresent()) {
                User existingUser = existingUserOptional.get();

                // Update the fields of the existing user
                if (data.getFirstName() != null) {
                    existingUser.setFirstName(data.getFirstName());
                }
                if (data.getLastName() != null) {
                    existingUser.setLastName(data.getLastName());
                }

                // Update email or phone number, mirroring the logic in UserRegistrationConsumer
                if (data.getEmailOrPhone() != null) {
                    if (data.getEmailOrPhone().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                        existingUser.setEmail(data.getEmailOrPhone());
                        existingUser.setPhoneNumber(null); // If it's an email, clear phone number
                    } else {
                        existingUser.setPhoneNumber(data.getEmailOrPhone());
                        existingUser.setEmail(null); // If it's a phone number, clear email
                    }
                }
                // Note: If UserRegistrationData can include other updatable fields (like profilePictureUrl
                // if you decide to unify updates), you'd handle them here too.

                userRepository.save(existingUser);
                logger.info("User with ID {} updated in content-service database: {}", data.getUserId(), existingUser);
            } else {
                logger.warn("User with ID {} not found in content-service, cannot update. " +
                        "This may indicate a synchronization issue or a missed registration event.", data.getUserId());
                // Depending on your service's requirements, you might want to create the user here
                // if they weren't found, but for "update" events, they typically should exist.
            }
        } catch (Exception e) {
            logger.error("Error processing user update message for user ID {}: {}", data.getUserId(), e.getMessage(), e);
            // Avoid e.printStackTrace() in production code; use the logger for full stack traces if needed.
        }
    }
}