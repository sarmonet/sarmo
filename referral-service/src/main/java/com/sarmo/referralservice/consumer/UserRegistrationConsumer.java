package com.sarmo.referralservice.consumer;

import com.sarmo.kafka.dto.UserRegistrationWithReferralData;
import com.sarmo.referralservice.entity.ReferralCode;
import com.sarmo.referralservice.entity.User;
import com.sarmo.referralservice.repository.ReferralCodeRepository;
import com.sarmo.referralservice.repository.UserRepository;
import com.sarmo.referralservice.service.ReferralCodeService;
import com.sarmo.referralservice.service.ReferralUsageService;
import com.sarmo.referralservice.dto.CreateReferralUsageDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
public class UserRegistrationConsumer {

    private static final Logger logger = LoggerFactory.getLogger(UserRegistrationConsumer.class);
    private final UserRepository userRepository;
    private final ReferralCodeService referralCodeService;
    private final ReferralCodeRepository referralCodeRepository;
    private final ReferralUsageService referralUsageService;

    public UserRegistrationConsumer(UserRepository userRepository, ReferralCodeService referralCodeService, ReferralCodeRepository referralCodeRepository, ReferralUsageService referralUsageService) {
        this.userRepository = userRepository;
        this.referralCodeService = referralCodeService;
        this.referralCodeRepository = referralCodeRepository;
        this.referralUsageService = referralUsageService;
    }

    @Transactional
    @KafkaListener(topics = "user-referral-registration", groupId = "referral-service-group")
    public void consumeUserRegistrationMessage(UserRegistrationWithReferralData data) {
        logger.info("Received user registration message: {}", data);
        try {
            User newUser = createUser(data);
            userRepository.save(newUser);
            logger.info("User data saved to database: {}", newUser);

            assignSelfReferralCode(newUser);

            processReferral(data.getReferralCode(), newUser);

        } catch (Exception e) {
            logger.error("Error processing user registration message: {}", data, e);
            logger.debug("Error details:", e); // Выводим подробные детали ошибки в режиме debug
        }
    }

    private User createUser(UserRegistrationWithReferralData data) {
        User user = new User(data.getUserId(), data.getFirstName(), data.getLastName());
        String emailOrPhone = data.getEmailOrPhone();
        if (StringUtils.hasText(emailOrPhone)) {
            if (emailOrPhone.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                user.setEmail(emailOrPhone);
            } else {
                user.setPhoneNumber(emailOrPhone);
            }
        }
        return user;
    }

    private void assignSelfReferralCode(User user) {
        ReferralCode newReferralCode = referralCodeService.createReferralCodeForNewUser(user);
        user.setReferralCode(newReferralCode);
        userRepository.save(user);
        logger.info("Generated and assigned referral code '{}' to user ID: {}", newReferralCode.getCode(), user.getUserId());
    }

    private void processReferral(String referralCodeValue, User newUser) {
        if (StringUtils.hasText(referralCodeValue)) {
            referralCodeRepository.findByCode(referralCodeValue)
                    .ifPresentOrElse(
                            referrerCode -> {
                                User referrer = referrerCode.getUser();
                                if (!referrer.getUserId().equals(newUser.getUserId())) {
                                    recordReferral(referrerCode.getCode(), newUser.getUserId());
                                } else {
                                    logger.warn("User ID {} tried to use their own referral code '{}'.",
                                            newUser.getUserId(), referralCodeValue);
                                }
                            },
                            () -> logger.warn("Referral code '{}' provided during registration by user ID {} is invalid.",
                                    referralCodeValue, newUser.getUserId())
                    );
        }
    }

    private void recordReferral(String referrerCode, Long referredUserId) {
        CreateReferralUsageDto usageDto = new CreateReferralUsageDto(referrerCode, referredUserId);
        referralUsageService.recordReferralUsage(usageDto);
        logger.info("User ID {} was referred by code '{}'. Recorded usage.", referredUserId, referrerCode);
    }
}