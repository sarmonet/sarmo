package com.sarmo.chatservice.service;


import com.sarmo.chatservice.entity.User;
import com.sarmo.chatservice.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserBatchSaveService {

    private static final Logger logger = LoggerFactory.getLogger(UserBatchSaveService.class);

    private final UserRepository userRepository;

    public UserBatchSaveService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Saves a batch of users within a transaction.
     * This method is called from another service, so Spring AOP can intercept it.
     * @param users The list of users to save.
     */
    @Transactional
    public void saveBatch(List<User> users) {
        if (users != null && !users.isEmpty()) {
            logger.info("Saving batch of {} users within transaction.", users.size());
            userRepository.saveAll(users);
        }
    }
}