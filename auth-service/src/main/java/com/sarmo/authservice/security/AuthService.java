package com.sarmo.authservice.security;

import com.sarmo.authservice.entity.RefreshToken;
import com.sarmo.authservice.exception.UserNotFoundAfterVerificationException;
import com.sarmo.authservice.producer.UserRegistrationProducer;
import com.sarmo.authservice.producer.UserRegistrationWithReferralProducer;
import com.sarmo.kafka.dto.UserRegistrationData;
import com.sarmo.kafka.dto.UserRegistrationWithReferralData;
import jakarta.transaction.Transactional;
import com.sarmo.authservice.dto.JwtAuthResponse;
import com.sarmo.authservice.dto.LoginRequest;
import com.sarmo.authservice.dto.RegisterRequest;
import com.sarmo.authservice.entity.Role;
import com.sarmo.authservice.entity.User;
import com.sarmo.authservice.enums.AuthProvider;
import com.sarmo.authservice.enums.RoleName;
import com.sarmo.authservice.exception.EmailAlreadyTakenException;
import com.sarmo.authservice.repository.RoleRepository;
import com.sarmo.authservice.repository.UserRepository;
import com.sarmo.authservice.service.TwoFactorAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

import com.sarmo.authservice.entity.PendingUserRegistration;
import com.sarmo.authservice.repository.PendingUserRegistrationRepository;
import com.sarmo.authservice.exception.UserAlreadyPendingConfirmationException;

import java.time.LocalDateTime;

import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Service
public class AuthService {
    private final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RoleRepository roleRepository;
    private final TwoFactorAuthService twoFactorAuthService;
    private final RefreshTokenService refreshTokenService;
    private final UserRegistrationProducer userRegistrationProducer;
    private final UserRegistrationWithReferralProducer userRegistrationWithReferralProducer;

    private final PendingUserRegistrationRepository pendingUserRegistrationRepository;

    public AuthService(AuthenticationManager authenticationManager, UserRepository userRepository, PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider, RoleRepository roleRepository,
                       TwoFactorAuthService twoFactorAuthService, RefreshTokenService refreshTokenService,
                       UserRegistrationProducer userRegistrationProducer, UserRegistrationWithReferralProducer userRegistrationWithReferralProducer,
                       PendingUserRegistrationRepository pendingUserRegistrationRepository) { // Добавляем в конструктор
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.roleRepository = roleRepository;
        this.twoFactorAuthService = twoFactorAuthService;
        this.refreshTokenService = refreshTokenService;
        this.userRegistrationProducer = userRegistrationProducer;
        this.userRegistrationWithReferralProducer = userRegistrationWithReferralProducer;
        this.pendingUserRegistrationRepository = pendingUserRegistrationRepository; // Инициализируем
    }


    @Transactional
    public UUID register(RegisterRequest registerRequest) {

        if (userRepository.findByEmail(registerRequest.getContact()).isPresent() || userRepository.findByPhoneNumber(registerRequest.getContact()).isPresent()) {
            throw new EmailAlreadyTakenException("Contact already taken by an active user.");
        }

        Optional<PendingUserRegistration> existingPendingUserOpt = pendingUserRegistrationRepository.findByContact(registerRequest.getContact());

        if (existingPendingUserOpt.isPresent()) {
            PendingUserRegistration existingPendingUser = existingPendingUserOpt.get();
            if (existingPendingUser.getExpirationTime().isBefore(LocalDateTime.now())) {
                logger.info("Found expired pending registration for contact: {}. Deleting to allow new registration.", registerRequest.getContact());
                try {
                    pendingUserRegistrationRepository.delete(existingPendingUser);
                    twoFactorAuthService.deleteTwoFactorCodeByVerificationId(existingPendingUser.getVerificationId());
                } catch (Exception e) {
                    logger.error("Failed to delete expired pending registration or its associated TwoFactorCode for contact: {}. Error: {}", registerRequest.getContact(), e.getMessage(), e);
                }
            } else {
                logger.warn("Registration for contact: {} is already pending and not expired.", registerRequest.getContact());
                throw new UserAlreadyPendingConfirmationException("Registration for this contact is already pending and not expired. Please check your email/phone for the verification code or resend it.");
            }
        }

        UUID verificationId = UUID.randomUUID();
        LocalDateTime creationTime = LocalDateTime.now();
        int expirationMinutes = 10;
        LocalDateTime expirationTime = creationTime.plusMinutes(expirationMinutes);

        PendingUserRegistration pendingUser = new PendingUserRegistration(
                verificationId,
                registerRequest.getFirstName(),
                registerRequest.getLastName(),
                registerRequest.getContact(),
                passwordEncoder.encode(registerRequest.getPassword()),
                registerRequest.getReferralCode(),
                creationTime,
                expirationTime
        );

        try {
            pendingUserRegistrationRepository.save(pendingUser);
            logger.info("Saved new pending user registration for contact: {}", registerRequest.getContact());
        } catch (Exception e) {
            logger.error("Error saving new pending user registration for contact: {}", registerRequest.getContact(), e);
            throw new RuntimeException("Failed to save new pending user registration.", e);
        }

        try {
            twoFactorAuthService.generateAndSendCode(verificationId, registerRequest.getContact(), expirationMinutes);
            logger.info("Verification code sent for new pending user registration with verificationId: {}", verificationId);
        } catch (Exception e) {
            logger.error("Error sending verification code for new pending user registration: {}", registerRequest.getContact(), e);
            throw new RuntimeException("Failed to send verification code. Please try again.", e);
        }

        return verificationId;
    }


    public Object authenticate(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getContact(), loginRequest.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            User user = userRepository.findByEmail(loginRequest.getContact())
                    .orElse(userRepository.findByPhoneNumber(loginRequest.getContact())
                            .orElse(null));
            if (user == null) {
                throw new UsernameNotFoundException("User not found after successful authentication attempt.");
            }


            if (user.isTwoFactorEnabled()) {
                return twoFactorAuthService.generateAndSendCode(UUID.randomUUID(), user.getEmail() != null ? user.getEmail() : user.getPhoneNumber(), 10);
            }

            // Обычный вход: генерируем JWT и Refresh Token
            String jwt = jwtTokenProvider.generateToken(authentication);
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getEmail() != null ? user.getEmail() : user.getPhoneNumber());

            return new JwtAuthResponse(jwt, refreshToken.getToken());

        } catch (BadCredentialsException e) {
            if (pendingUserRegistrationRepository.findByContact(loginRequest.getContact()).isPresent()) {
                logger.warn("Authentication attempt for unverified pending user: {}", loginRequest.getContact());
                throw new DisabledException("Your account is not yet active. Please verify your email or phone number using the code sent to you to complete registration.");
            } else {
                throw new BadCredentialsException("Invalid contact or password", e);
            }
        } catch (LockedException e) {
            throw new LockedException("Account is locked", e);
        } catch (DisabledException e) {
            throw new DisabledException(e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Authentication failed for {}: {}", loginRequest.getContact(), e.getMessage(), e);
            throw new BadCredentialsException("Authentication failed", e);
        }
    }


    @Transactional
    public JwtAuthResponse verifyTwoFactorCode(UUID verificationId, String code) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        String contact = twoFactorAuthService.verifyCodeAndGetContact(verificationId, code);

        PendingUserRegistration pendingUser = pendingUserRegistrationRepository.findById(verificationId)
                .orElse(null);

        if (pendingUser != null) {
            logger.info("Verification successful for pending registration with verificationId: {}. Creating user.", verificationId);

            User user = new User();

            user.setName(pendingUser.getFirstName() + " " + pendingUser.getLastName());

            String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";

            String phoneNumberRegex = "^\\+?[0-9\\s()-]{7,20}$";

            if (contact.matches(emailRegex)) {
                user.setEmail(contact);
                user.setPhoneNumber(null);
            } else if (contact.matches(phoneNumberRegex)) {
                user.setEmail(null);
                user.setPhoneNumber(contact);
            } else {
                logger.error("Contact '{}' from pending registration is neither a valid email nor a valid phone number format. Cannot create user.", contact);
                throw new IllegalArgumentException("Invalid contact format provided during registration confirmation. Contact must be a valid email or phone number.");
            }

            user.setPassword(pendingUser.getHashedPassword());
            user.setAuthProvider(AuthProvider.LOCAL);
            user.setTwoFactorEnabled(false);

            Role userRole = roleRepository.findByName(RoleName.USER.name())
                    .orElseThrow(() -> new RuntimeException("Role USER not found"));
            user.setRoles(new HashSet<>());
            user.getRoles().add(userRole);

            User createdUser = userRepository.save(user);
            logger.info("User created successfully after verification: {}", createdUser.getEmail() != null ? createdUser.getEmail() : createdUser.getPhoneNumber());

            try {
                pendingUserRegistrationRepository.delete(pendingUser);
                logger.info("Pending user registration deleted for verificationId: {}", verificationId);
            } catch (Exception e) {
                logger.error("Error deleting pending user registration for verificationId: {}", verificationId, e);
            }

            try {
                userRegistrationProducer.sendUserRegistrationMessage(new UserRegistrationData(createdUser.getId(), createdUser.getEmail() != null ? createdUser.getEmail() : createdUser.getPhoneNumber(), pendingUser.getFirstName(), pendingUser.getLastName()));
            } catch (Exception e) {
                logger.error("Error sending user registration message to Kafka", e);
            }

            if (pendingUser.getReferralCode() != null && !pendingUser.getReferralCode().isEmpty()) {
                try {
                    userRegistrationWithReferralProducer.sendUserRegistrationMessage(new UserRegistrationWithReferralData(createdUser.getId(), createdUser.getEmail() != null ? createdUser.getEmail() : createdUser.getPhoneNumber(), pendingUser.getFirstName(), pendingUser.getLastName(), pendingUser.getReferralCode()));
                } catch (Exception e) {
                    logger.error("Error sending user registration with referral message to Kafka", e);
                }
            }

            UserPrincipal userPrincipal = UserPrincipal.create(createdUser);
            Authentication authentication = new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
            String jwt = jwtTokenProvider.generateToken(authentication);
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(createdUser.getEmail() != null ? createdUser.getEmail() : createdUser.getPhoneNumber());

            logger.info("JWT token generated for newly created user: {}", createdUser.getEmail() != null ? createdUser.getEmail() : createdUser.getPhoneNumber());
            return new JwtAuthResponse(jwt, refreshToken.getToken());

        } else {
            logger.info("Verification successful for existing user with verificationId: {}. Generating tokens.", verificationId);

            User user = twoFactorAuthService.verifyCodeAndGetUser(verificationId, code);

            if (user == null) {
                throw new UserNotFoundAfterVerificationException("User not found for provided verification ID after 2FA.");
            }

            UserPrincipal userPrincipal = UserPrincipal.create(user);
            Authentication authentication = new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
            String jwt = jwtTokenProvider.generateToken(authentication);
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getEmail() != null ? user.getEmail() : user.getPhoneNumber());

            logger.info("JWT token generated for user: {}", user.getEmail() != null ? user.getEmail() : user.getPhoneNumber());
            return new JwtAuthResponse(jwt, refreshToken.getToken());
        }
    }
}