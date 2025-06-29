package com.sarmo.contentservice.consumer;

import com.sarmo.contentservice.entity.User;
import com.sarmo.contentservice.repository.UserRepository;
import com.sarmo.kafka.dto.UserImageData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserImageConsumer {

    private static final Logger logger = LoggerFactory.getLogger(UserImageConsumer.class);
    private final UserRepository userRepository;

    public UserImageConsumer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @KafkaListener(topics = "user-image", groupId = "content-service-group")
    public void consumeUserRegistrationMessage(UserImageData data) {
        logger.info("Received user registration message: {}", data);
        try {
            Optional<User> user = userRepository.findById(data.getUserId());

            user.ifPresent(existingUser -> {
                existingUser.setProfilePictureUrl(data.getUserImageUrl());
                userRepository.save(existingUser);
            });

            logger.info("User data saved to database: {}", user);
        } catch (Exception e) {
            logger.error("Error processing user registration message: {}", data, e);
            e.printStackTrace();
        }
    }

}
