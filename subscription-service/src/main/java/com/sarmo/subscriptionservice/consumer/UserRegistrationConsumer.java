package com.sarmo.subscriptionservice.consumer;

import com.sarmo.kafka.dto.UserRegistrationData;
import com.sarmo.subscriptionservice.entity.User;
import com.sarmo.subscriptionservice.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class UserRegistrationConsumer {

    private static final Logger logger = LoggerFactory.getLogger(UserRegistrationConsumer.class);
    private final UserRepository userRepository;

    public UserRegistrationConsumer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @KafkaListener(topics = "user-registration", groupId = "subscription-service-group")
    public void consumeUserRegistrationMessage(UserRegistrationData data) {
        logger.info("Received user registration data: {}", data);

        try {
            User user = new User(data.getUserId(), data.getFirstName(), data.getLastName());
            userRepository.save(user);
            logger.info("User data saved to database: {}", user);
        } catch (Exception e) {
            logger.error("Error processing user registration data: {}", data, e);
        }
    }
}
