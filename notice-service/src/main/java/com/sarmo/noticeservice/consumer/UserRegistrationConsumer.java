package com.sarmo.noticeservice.consumer;

import com.sarmo.kafka.dto.UserRegistrationData;

import com.sarmo.noticeservice.entity.User;
import com.sarmo.noticeservice.repository.UserRepository;
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

    @KafkaListener(topics = "user-registration", groupId = "notice-service-group")
    public void consumeUserRegistrationMessage(UserRegistrationData data) {
        logger.info("Received user registration message: {}", data);
        try {
            User user = new User(data.getUserId(), data.getFirstName(), data.getLastName());

            if (data.getEmailOrPhone().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                user.setEmail(data.getEmailOrPhone());
            } else {
                user.setPhoneNumber(data.getEmailOrPhone());
            }

            userRepository.save(user);
            logger.info("User data saved to database: {}", user);
        } catch (Exception e) {
            logger.error("Error processing user registration message: {}", data, e);
            e.printStackTrace();
        }
    }
}