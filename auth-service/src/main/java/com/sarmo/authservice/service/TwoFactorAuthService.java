package com.sarmo.authservice.service;

import com.sarmo.authservice.entity.PendingUserRegistration;
import com.sarmo.authservice.entity.TwoFactorCode;
import com.sarmo.authservice.entity.User; // Возможно, этот импорт будет нужен, если verifyCodeAndGetUser еще используется
import com.sarmo.authservice.exception.InvalidVerificationCodeException;
import com.sarmo.authservice.exception.UserNotFoundAfterVerificationException; // Если используется
import com.sarmo.authservice.grpc.EmailConfirmationClient;
import com.sarmo.authservice.grpc.SmsClient;
import com.sarmo.authservice.repository.PendingUserRegistrationRepository;
import com.sarmo.authservice.repository.TwoFactorCodeRepository;
import com.sarmo.authservice.repository.UserRepository; // Если используется
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class TwoFactorAuthService {

    private static final Logger logger = LoggerFactory.getLogger(TwoFactorAuthService.class);

    private final TwoFactorCodeRepository twoFactorCodeRepository;
    private final UserRepository userRepository; // Оставляем, если используется для verifyCodeAndGetUser
    private final EmailConfirmationClient emailConfirmationClient;
    private final SmsClient smsClient;
    private final PendingUserRegistrationRepository pendingUserRegistrationRepository;

    public TwoFactorAuthService(TwoFactorCodeRepository twoFactorCodeRepository, UserRepository userRepository, EmailConfirmationClient emailConfirmationClient, SmsClient smsClient, PendingUserRegistrationRepository pendingUserRegistrationRepository) {
        this.twoFactorCodeRepository = twoFactorCodeRepository;
        this.userRepository = userRepository;
        this.emailConfirmationClient = emailConfirmationClient;
        this.smsClient = smsClient;
        this.pendingUserRegistrationRepository = pendingUserRegistrationRepository;
    }

    // ИЗМЕНЕННЫЙ МЕТОД: теперь принимает verificationId, а не генерирует его
    @Transactional
    public UUID generateAndSendCode(UUID verificationId, String emailOrPhoneNumber, int expirationMinutes) {
        Objects.requireNonNull(verificationId, "Verification ID cannot be null");
        Objects.requireNonNull(emailOrPhoneNumber, "Email or phone number cannot be null");
        if (expirationMinutes <= 0) {
            throw new IllegalArgumentException("Expiration minutes must be positive");
        }

        String code = generateCode();
        LocalDateTime creationTime = LocalDateTime.now();
        LocalDateTime expirationTime = creationTime.plusMinutes(expirationMinutes);

        TwoFactorCode twoFactorCode = new TwoFactorCode(verificationId, code, emailOrPhoneNumber, creationTime, expirationTime);

        Optional<PendingUserRegistration> pendingUser = pendingUserRegistrationRepository.findByContact(emailOrPhoneNumber);

        if (pendingUser.isPresent()) {
            PendingUserRegistration existingRegistration = pendingUser.get();

            PendingUserRegistration newRegistration = new PendingUserRegistration();
            newRegistration.setVerificationId(verificationId);
            newRegistration.setContact(existingRegistration.getContact());
            newRegistration.setFirstName(existingRegistration.getFirstName());
            newRegistration.setLastName(existingRegistration.getLastName());
            newRegistration.setHashedPassword(existingRegistration.getHashedPassword());
            newRegistration.setReferralCode(existingRegistration.getReferralCode());
            newRegistration.setCreationTime(creationTime);
            newRegistration.setExpirationTime(expirationTime);

            pendingUserRegistrationRepository.delete(existingRegistration);
            logger.info("Deleted old pending user registration for {}", emailOrPhoneNumber);

            pendingUserRegistrationRepository.save(newRegistration);
            logger.info("Saved new pending user registration with verification ID: {}", verificationId);
        }

        try {
            twoFactorCodeRepository.save(twoFactorCode);
            logger.info("Saved new verification code for {}: verificationId={}", emailOrPhoneNumber, verificationId);
        } catch (Exception e) {
            logger.error("Failed to save verification code for {}", emailOrPhoneNumber, e);
            throw new RuntimeException("Error saving verification code", e);
        }

        if (emailOrPhoneNumber.contains("@")) {
            logger.info("Attempting to send OTP email to: {}", emailOrPhoneNumber);
            try {
                emailConfirmationClient.sendOtpEmail(emailOrPhoneNumber, code);
                logger.info("OTP email sent successfully to: {}", emailOrPhoneNumber);
            } catch (Exception e) {
                logger.error("Failed to send OTP email to {}", emailOrPhoneNumber, e);
            }
        } else {
            logger.info("Attempting to send SMS to: {}", emailOrPhoneNumber);
            try {
                smsClient.sendSms(emailOrPhoneNumber, "Ваш код подтверждения: " + code);
                logger.info("SMS sent successfully to: {}", emailOrPhoneNumber);
            } catch (Exception e) {
                logger.error("Failed to send SMS to {}", emailOrPhoneNumber, e);
            }
        }

        logger.info("Generated and initiated sending of verification code for {}: verificationId={}", emailOrPhoneNumber, verificationId);
        return verificationId;
    }

    private String generateCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    // НОВЫЙ МЕТОД: для верификации кода и получения контакта, без получения пользователя
    @Transactional
    public String verifyCodeAndGetContact(UUID verificationId, String enteredCode) {
        Objects.requireNonNull(verificationId, "Verification ID cannot be null");
        Objects.requireNonNull(enteredCode, "Entered code cannot be null");

        TwoFactorCode twoFactorCode = twoFactorCodeRepository.findByVerificationId(verificationId);

        if (twoFactorCode == null) {
            logger.warn("Verification code not found for verificationId: {}", verificationId);
            throw new InvalidVerificationCodeException("Verification code not found");
        }

        if (twoFactorCode.getExpirationTime().isBefore(LocalDateTime.now())) {
            logger.warn("Verification code expired for verificationId: {}", verificationId);
            try {
                twoFactorCodeRepository.delete(twoFactorCode); // Удаляем просроченный код
                logger.info("Expired verification code with verificationId: {} deleted.", verificationId);
            } catch (Exception e) {
                logger.error("Failed to delete expired verification code with verificationId: {}", verificationId, e);
            }
            throw new InvalidVerificationCodeException("Verification code expired");
        }

        if (twoFactorCode.getCode().equals(enteredCode)) {
            logger.info("Verification successful for verificationId: {}", verificationId);
            try {
                twoFactorCodeRepository.delete(twoFactorCode); // Удаляем код после успешной верификации
                logger.info("Verification code with verificationId: {} deleted after successful verification.", verificationId);
            } catch (Exception e) {
                logger.error("Failed to delete verification code with verificationId: {}", verificationId, e);
            }
            return twoFactorCode.getEmailOrPhoneNumber(); // Возвращаем контакт, связанный с кодом
        }

        logger.warn("Verification failed for verificationId: {}", verificationId);
        throw new InvalidVerificationCodeException("Invalid two-factor code");
    }

    // Существующий метод `verifyCodeAndGetUser` оставим без изменений.
    // Он будет использоваться в случае, если это 2FA для входа уже существующего пользователя.
    @Transactional
    public User verifyCodeAndGetUser(UUID verificationId, String enteredCode) {
        Objects.requireNonNull(verificationId, "Verification ID cannot be null");
        Objects.requireNonNull(enteredCode, "Entered code cannot be null");

        TwoFactorCode twoFactorCode = twoFactorCodeRepository.findByVerificationId(verificationId);

        if (twoFactorCode == null) {
            logger.warn("Verification code not found for verificationId: {}", verificationId);
            throw new InvalidVerificationCodeException("Verification code not found");
        }

        if (twoFactorCode.getExpirationTime().isBefore(LocalDateTime.now())) {
            logger.warn("Verification code expired for verificationId: {}", verificationId);
            try {
                twoFactorCodeRepository.delete(twoFactorCode); // Удаляем просроченный код
                logger.info("Expired verification code with verificationId: {} deleted.", verificationId);
            } catch (Exception e) {
                logger.error("Failed to delete expired verification code with verificationId: {}", verificationId, e);
            }
            throw new InvalidVerificationCodeException("Verification code expired");
        }

        if (twoFactorCode.getCode().equals(enteredCode)) {
            logger.info("Verification successful for verificationId: {}", verificationId);

            try {
                twoFactorCodeRepository.delete(twoFactorCode); // Удаляем код после успешной верификации
                logger.info("Verification code with verificationId: {} deleted after successful verification.", verificationId);
            } catch (Exception e) {
                logger.error("Failed to delete verification code with verificationId: {}", verificationId, e);
            }

            // Этот вызов подразумевает, что пользователь уже существует и связан с двухфакторным кодом.
            // Для регистрации этот метод не используется напрямую.
            User user = getUserByVerificationId(verificationId);
            if (user == null) {
                logger.error("User not found after successful verification for verificationId: {}. This is unexpected for login 2FA.", verificationId);
                throw new UserNotFoundAfterVerificationException("User not found after successful verification");
            }

            return user;
        }

        logger.warn("Verification failed for verificationId: {}", verificationId);
        throw new InvalidVerificationCodeException("Invalid two-factor code");
    }

    // Этот метод, вероятно, стоит оставить без изменений, так как он ищет по TwoFactorCode, который уже связан с существующим пользователем.
    @Transactional(readOnly = true)
    public User getUserByVerificationId(UUID verificationId) {
        Objects.requireNonNull(verificationId, "Verification ID cannot be null");
        logger.info("Starting user search by verificationId: {}", verificationId);
        TwoFactorCode twoFactorCode = twoFactorCodeRepository.findByVerificationId(verificationId);
        if (twoFactorCode == null) {
            logger.warn("Verification code not found when searching for user by verificationId: {}", verificationId);
            return null;
        }
        String identifier = twoFactorCode.getEmailOrPhoneNumber();
        logger.debug("Identifier from TwoFactorCode: {}", identifier);

        User user = null;
        if (identifier != null && identifier.contains("@")) { // Проверяем, является ли это email
            logger.debug("Attempting to find user by email: {}", identifier);
            user = userRepository.findByEmail(identifier).orElse(null);
            if (user != null) {
                logger.debug("User found by email with id: {}", user.getId());
            } else {
                logger.warn("User not found by email: {}", identifier);
            }
        } else if (identifier != null && !identifier.isEmpty()) { // Считаем, что это номер телефона, если не email
            logger.debug("Attempting to find user by phone number: {}", identifier);
            user = userRepository.findByPhoneNumber(identifier).orElse(null);
            if (user != null) {
                logger.debug("User found by phone number with id: {}", user.getId());
            } else {
                logger.warn("User not found by phone number: {}", identifier);
            }
        } else {
            logger.warn("Unrecognized identifier format or null identifier from TwoFactorCode: {}", identifier);
        }

        if (user == null) {
            logger.warn("User not found for identifier: {}", identifier);
        }

        return user;
    }

    // ОБНОВЛЕННЫЙ resendCode: теперь вызывает новую версию generateAndSendCode
    @Transactional
    public UUID resendCode(UUID verificationId, int expirationMinutes) {
        Objects.requireNonNull(verificationId, "Verification ID cannot be null for resend");
        if (expirationMinutes <= 0) {
            throw new IllegalArgumentException("Expiration minutes must be positive");
        }

        logger.info("Attempting to resend verification code for original verificationId: {}", verificationId);

        TwoFactorCode originalCode = twoFactorCodeRepository.findByVerificationId(verificationId);

        if (originalCode == null) {
            logger.warn("Original verification code not found for verificationId: {}. Cannot resend.", verificationId);
            throw new InvalidVerificationCodeException("Original verification code not found. Cannot resend.");
        }

        String emailOrPhoneNumber = originalCode.getEmailOrPhoneNumber();
        logger.info("Found original code for {}. Proceeding to resend.", emailOrPhoneNumber);

        try {
            twoFactorCodeRepository.delete(originalCode);
            logger.info("Original verification code with verificationId: {} deleted for resend operation.", verificationId);
        } catch (Exception e) {
            logger.error("Error deleting original verification code with verificationId: {} during resend.", verificationId, e);
            throw new RuntimeException("Error deleting original code during resend", e);
        }

        try {
            // Генерируем НОВЫЙ verificationId для НОВОГО кода.
            // Это важно, чтобы каждый TwoFactorCode был уникально связан с его verificationId.
            UUID newVerificationId = UUID.randomUUID();
            generateAndSendCode(newVerificationId, emailOrPhoneNumber, expirationMinutes);
            logger.info("New verification code generated and sent for {}. New verificationId={}", emailOrPhoneNumber, newVerificationId);
            return newVerificationId;
        } catch (Exception e) {
            logger.error("Error generating and sending new code during resend for {}.", emailOrPhoneNumber, e);
            throw new RuntimeException("Error generating and sending new code during resend", e);
        }
    }

    @Transactional
    public void deleteTwoFactorCodeByVerificationId(UUID verificationId) {
        Objects.requireNonNull(verificationId, "Verification ID cannot be null for deletion");
        try {
            twoFactorCodeRepository.deleteByVerificationId(verificationId);
            logger.info("TwoFactorCode with verificationId: {} deleted.", verificationId);
        } catch (Exception e) {
            logger.error("Failed to delete TwoFactorCode with verificationId: {}. Error: {}", verificationId, e.getMessage(), e);
        }
    }
}