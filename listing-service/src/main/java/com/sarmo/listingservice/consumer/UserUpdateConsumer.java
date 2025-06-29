package com.sarmo.listingservice.consumer;

import com.sarmo.kafka.dto.UserRegistrationData;
import com.sarmo.listingservice.entity.User;
import com.sarmo.listingservice.repository.UserRepository;
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
     * Слушает Kafka топик "user-update" и обновляет информацию о пользователе
     * в базе данных listing-service.
     *
     * @param data Объект UserRegistrationData, содержащий обновленные данные пользователя.
     */
    @KafkaListener(topics = "user-update", groupId = "listing-service-group")
    public void consumeUserUpdateMessage(UserRegistrationData data) {
        logger.info("Received user update message: {}", data);
        try {
            Optional<User> existingUserOptional = userRepository.findById(data.getUserId());

            if (existingUserOptional.isPresent()) {
                User existingUser = existingUserOptional.get();

                if (data.getFirstName() != null) {
                    existingUser.setFirstName(data.getFirstName());
                }
                if (data.getLastName() != null) {
                    existingUser.setLastName(data.getLastName());
                }

                if (data.getEmailOrPhone() != null) {
                    if (data.getEmailOrPhone().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                        existingUser.setEmail(data.getEmailOrPhone());
                        existingUser.setPhoneNumber(null);
                    } else {
                        existingUser.setPhoneNumber(data.getEmailOrPhone());
                        existingUser.setEmail(null);
                    }
                }
                userRepository.save(existingUser);
                logger.info("User with ID {} updated in database: {}", data.getUserId(), existingUser);
            } else {
                logger.warn("User with ID {} not found in listing-service, cannot update. " +
                        "This might indicate an out-of-sync situation or a missing registration event.", data.getUserId());
            }
        } catch (Exception e) {
            logger.error("Error processing user update message for user ID {}: {}", data.getUserId(), e.getMessage(), e);
        }
    }
}
