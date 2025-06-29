package com.sarmo.referralservice.service;

import com.sarmo.referralservice.dto.CreateReferralUsageDto;
import com.sarmo.referralservice.dto.ReferralUsageDto;
import com.sarmo.referralservice.entity.ReferralCode;
import com.sarmo.referralservice.entity.ReferralUsage;
import com.sarmo.referralservice.entity.User;
import com.sarmo.referralservice.repository.ReferralCodeRepository;
import com.sarmo.referralservice.repository.ReferralUsageRepository;
import com.sarmo.referralservice.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReferralUsageService {

    private static final Logger logger = LoggerFactory.getLogger(ReferralUsageService.class);

    private final ReferralUsageRepository referralUsageRepository;
    private final ReferralCodeRepository referralCodeRepository;
    private final UserRepository userRepository;

    public ReferralUsageService(ReferralUsageRepository referralUsageRepository,
                                ReferralCodeRepository referralCodeRepository,
                                UserRepository userRepository) {
        this.referralUsageRepository = referralUsageRepository;
        this.referralCodeRepository = referralCodeRepository;
        this.userRepository = userRepository;
    }

    private ReferralUsageDto convertToDto(ReferralUsage referralUsage) {
        return new ReferralUsageDto(
                referralUsage.getId(),
                referralUsage.getReferralCode().getCode(),
                referralUsage.getReferredUser().getUserId(),
                referralUsage.getUsageDate()
        );
    }

    @Transactional
    public ReferralUsageDto recordReferralUsage(CreateReferralUsageDto createDto) {
        Optional<ReferralCode> referralCodeOptional = referralCodeRepository.findByCode(createDto.getReferralCodeValue());
        Optional<User> referredUserOptional = userRepository.findById(createDto.getReferredUserId());

        if (referralCodeOptional.isPresent() && referredUserOptional.isPresent()) {
            ReferralCode referralCode = referralCodeOptional.get();
            User referredUser = referredUserOptional.get();

            if (!referralCode.getUser().getUserId().equals(createDto.getReferredUserId())) {
                ReferralUsage referralUsage = new ReferralUsage(referralCode, referredUser);
                ReferralUsage savedUsage = referralUsageRepository.save(referralUsage);
                logger.info("Recorded usage of code '{}' by user ID: {}", createDto.getReferralCodeValue(), createDto.getReferredUserId());
                return convertToDto(savedUsage);
            } else {
                logger.warn("User ID {} tried to use their own referral code '{}'.", createDto.getReferredUserId(), createDto.getReferralCodeValue());
                throw new IllegalArgumentException("Cannot use your own referral code.");
            }
        } else {
            logger.warn("Referral code '{}' not found or referred user ID {} not found.", createDto.getReferralCodeValue(), createDto.getReferredUserId());
            throw new EntityNotFoundException("Invalid referral code or referred user.");
        }
    }

    @Transactional(readOnly = true)
    public Optional<ReferralUsageDto> getReferralUsageById(Long id) {
        logger.info("Fetching referral usage by ID: {}", id);
        return referralUsageRepository.findById(id)
                .map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    public List<ReferralUsageDto> getAllReferralUsages() {
        logger.info("Fetching all referral usages.");
        return referralUsageRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReferralUsageDto> getReferralUsagesByReferredUser(Long referredUserId) {
        logger.info("Fetching referral usages for referred user ID: {}", referredUserId);
        return referralUsageRepository.findByReferredUser_UserId(referredUserId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReferralUsageDto> getReferralUsagesByReferralCode(String referralCodeValue) {
        logger.info("Fetching referral usages for code: {}", referralCodeValue);
        Optional<ReferralCode> referralCodeOptional = referralCodeRepository.findByCode(referralCodeValue);
        return referralCodeOptional.map(code -> referralUsageRepository.findByReferralCode(code).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList())).orElse(List.of());
    }

    @Transactional
    public void deleteReferralUsage(Long id) {
        referralUsageRepository.findById(id)
                .ifPresentOrElse(usage -> {
                    referralUsageRepository.delete(usage);
                    logger.info("Deleted referral usage with ID: {}", id);
                }, () -> logger.warn("Referral usage with ID {} not found, cannot delete.", id));
    }

    @Transactional(readOnly = true)
    public List<Long> getReferredUsersByReferrerId(Long referrerId) {
        logger.info("Fetching referred users for referrer ID: {}", referrerId);
        return referralUsageRepository.findReferredUsersByReferrerId(referrerId).stream()
                .map(User::getUserId)
                .collect(Collectors.toList());
    }
}