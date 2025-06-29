package com.sarmo.referralservice.service;

import com.sarmo.referralservice.dto.CreateReferralCodeDto;
import com.sarmo.referralservice.dto.ReferralCodeDto;
import com.sarmo.referralservice.dto.UpdateReferralCodeDto;
import com.sarmo.referralservice.entity.ReferralCode;
import com.sarmo.referralservice.entity.User;
import com.sarmo.referralservice.repository.ReferralCodeRepository;
import com.sarmo.referralservice.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class ReferralCodeService {

    private static final Logger logger = LoggerFactory.getLogger(ReferralCodeService.class);

    private final ReferralCodeRepository referralCodeRepository;

    private final UserRepository userRepository;

    public ReferralCodeService(ReferralCodeRepository referralCodeRepository, UserRepository userRepository) {
        this.referralCodeRepository = referralCodeRepository;
        this.userRepository = userRepository;
    }

    private ReferralCodeDto convertToDto(ReferralCode referralCode) {
        return new ReferralCodeDto(
                referralCode.getId(),
                referralCode.getCode(),
                referralCode.getUser().getUserId(),
                referralCode.getCreationDate()
        );
    }

    @Transactional
    public ReferralCodeDto createReferralCodeForUser(Long userId) {
        return userRepository.findById(userId)
                .map(user -> {
                    Optional<ReferralCode> existingCode = referralCodeRepository.findByUser(user);
                    if (existingCode.isPresent()) {
                        logger.warn("Referral code already exists for user ID: {}", userId);
                        return convertToDto(existingCode.get());
                    } else {
                        String code = generateUniqueReferralCode(userId);
                        ReferralCode referralCode = new ReferralCode(code, user);
                        ReferralCode savedCode = referralCodeRepository.save(referralCode);
                        logger.info("Created referral code '{}' for user ID: {}", savedCode.getCode(), userId);
                        return convertToDto(savedCode);
                    }
                })
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + userId + " not found"));
    }

    @Transactional
    public ReferralCode createReferralCodeForNewUser(User user) {
        String code = generateUniqueReferralCode(user.getUserId());
        ReferralCode referralCode = new ReferralCode(code, user);
        ReferralCode savedCode = referralCodeRepository.save(referralCode);
        logger.info("Created referral code '{}' for user ID: {}", savedCode.getCode(), user.getUserId());
        return savedCode;
    }

    @Transactional
    public String generateUniqueReferralCode(Long userId) {
        String code = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        while (referralCodeRepository.findByCode(code).isPresent()) {
            code = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            logger.warn("Generated referral code '{}' already exists, generating a new one.", code);
        }
        logger.info("Generated unique referral code '{}' for user ID: {}", code, userId);
        return code;
    }

    @Transactional(readOnly = true)
    public Optional<ReferralCodeDto> getReferralCodeByCode(String code) {
        logger.info("Fetching referral code by code: '{}'", code);
        return referralCodeRepository.findByCode(code)
                .map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    public Optional<ReferralCodeDto> getReferralCodeByUserId(Long userId) {
        logger.info("Fetching referral code for user ID: {}", userId);
        return referralCodeRepository.findByUser_UserId(userId)
                .map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    public Optional<ReferralCodeDto> getReferralCodeByUser(User user) {
        logger.info("Fetching referral code for user: {}", user.getUserId());
        return referralCodeRepository.findByUser(user)
                .map(this::convertToDto);
    }

    @Transactional
    public ReferralCodeDto saveReferralCode(Long userId, String referralCodeValue) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + userId + " not found"));
        Optional<ReferralCode> existingCode = referralCodeRepository.findByUser(user);
        if (existingCode.isPresent()) {
            logger.warn("Referral code already exists for user ID: {}", userId);
            return convertToDto(existingCode.get());
        }
        ReferralCode referralCode = new ReferralCode(referralCodeValue, user);
        ReferralCode savedCode = referralCodeRepository.save(referralCode);
        logger.info("Saved referral code '{}' for user ID: {}", referralCodeValue, userId);
        return convertToDto(savedCode);
    }

    @Transactional
    public void deleteReferralCode(Long codeId) {
        referralCodeRepository.findById(codeId)
                .ifPresentOrElse(code -> {
                    referralCodeRepository.delete(code);
                    logger.info("Deleted referral code with ID: {}", codeId);
                }, () -> logger.warn("Referral code with ID {} not found, cannot delete.", codeId));
    }

    @Transactional
    public ReferralCodeDto updateReferralCode(Long codeId, UpdateReferralCodeDto updateDto) {
        ReferralCode referralCode = referralCodeRepository.findById(codeId)
                .orElseThrow(() -> new EntityNotFoundException("Referral code with ID " + codeId + " not found"));
        if (!referralCode.getCode().equals(updateDto.getCode()) && referralCodeRepository.findByCode(updateDto.getCode()).isEmpty()) {
            referralCode.setCode(updateDto.getCode());
            ReferralCode updatedCode = referralCodeRepository.save(referralCode);
            logger.info("Updated referral code with ID {} to: '{}'", codeId, updateDto.getCode());
            return convertToDto(updatedCode);
        } else if (referralCodeRepository.findByCode(updateDto.getCode()).isPresent()) {
            logger.warn("Referral code '{}' already exists, cannot update ID {}.", updateDto.getCode(), codeId);
            return convertToDto(referralCode); // Return the existing code as DTO
        } else {
            logger.warn("Referral code with ID {} already has the code '{}', no update needed.", codeId, updateDto.getCode());
            return convertToDto(referralCode); // Return the existing code as DTO
        }
    }

    @Transactional(readOnly = true)
    public Optional<ReferralCodeDto> findByUserId(Long userId) {
        logger.info("Fetching referral code for user ID: {}", userId);
        return userRepository.findById(userId)
                .flatMap(referralCodeRepository::findByUser)
                .map(this::convertToDto);
    }

    @Transactional
    public ReferralCode createReferralCode(CreateReferralCodeDto createDto) {
        return userRepository.findById(createDto.getUserId())
                .map(user -> {
                    ReferralCode referralCode = new ReferralCode(createDto.getCode(), user);
                    return referralCodeRepository.save(referralCode);
                })
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + createDto.getUserId() + " not found"));
    }
}