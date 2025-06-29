package com.sarmo.referralservice.service;

import com.sarmo.referralservice.dto.CreateReferralRewardDto;
import com.sarmo.referralservice.dto.ReferralRewardDto;
import com.sarmo.referralservice.dto.UpdateReferralRewardDto;
import com.sarmo.referralservice.entity.ReferralReward;
import com.sarmo.referralservice.entity.User;
import com.sarmo.referralservice.enums.RewardCondition;
import com.sarmo.referralservice.enums.RewardType;
import com.sarmo.referralservice.repository.ReferralRewardRepository;
import com.sarmo.referralservice.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReferralRewardService {

    private static final Logger logger = LoggerFactory.getLogger(ReferralRewardService.class);

    private final ReferralRewardRepository referralRewardRepository;
    private final UserRepository userRepository;

    public ReferralRewardService(ReferralRewardRepository referralRewardRepository, UserRepository userRepository) {
        this.referralRewardRepository = referralRewardRepository;
        this.userRepository = userRepository;
    }

    private ReferralRewardDto convertToDto(ReferralReward reward) {
        return new ReferralRewardDto(
                reward.getId(),
                reward.getReferrer().getUserId(),
                reward.getReferred().getUserId(),
                reward.getRewardType(),
                reward.getRewardAmount(),
                reward.getRewardDate(),
                reward.getRewardCondition()
        );
    }

    @Transactional
    public ReferralRewardDto createReward(CreateReferralRewardDto createDto) {
        User referrer = userRepository.findById(createDto.getReferrerId())
                .orElseThrow(() -> new EntityNotFoundException("Referrer with ID " + createDto.getReferrerId() + " not found"));
        User referred = userRepository.findById(createDto.getReferredId())
                .orElseThrow(() -> new EntityNotFoundException("Referred user with ID " + createDto.getReferredId() + " not found"));

        ReferralReward reward = new ReferralReward();
        reward.setReferrer(referrer);
        reward.setReferred(referred);
        reward.setRewardType(createDto.getRewardType());
        reward.setRewardAmount(createDto.getRewardAmount());
        reward.setRewardCondition(createDto.getRewardCondition());

        ReferralReward savedReward = referralRewardRepository.save(reward);
        logger.info("Created referral reward for referrer ID {} and referred ID {}, type: {}, amount: {}, condition: {}",
                createDto.getReferrerId(), createDto.getReferredId(), createDto.getRewardType(), createDto.getRewardAmount(), createDto.getRewardCondition());
        return convertToDto(savedReward);
    }

    @Transactional(readOnly = true)
    public Optional<ReferralRewardDto> getRewardById(Long id) {
        logger.info("Fetching referral reward by ID: {}", id);
        return referralRewardRepository.findById(id)
                .map(this::convertToDto);
    }

    @Transactional(readOnly = true)
    public List<ReferralRewardDto> getAllRewards() {
        logger.info("Fetching all referral rewards.");
        return referralRewardRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReferralRewardDto> getRewardsByReferrer(Long referrerId) {
        logger.info("Fetching referral rewards for referrer ID: {}", referrerId);
        return referralRewardRepository.findByReferrer_UserId(referrerId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReferralRewardDto> getRewardsByReferred(Long referredId) {
        logger.info("Fetching referral rewards for referred user ID: {}", referredId);
        return referralRewardRepository.findByReferred_UserId(referredId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReferralRewardDto> getRewardsByType(RewardType rewardType) {
        logger.info("Fetching referral rewards by type: {}", rewardType);
        return referralRewardRepository.findByRewardType(rewardType).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReferralRewardDto> getRewardsByCondition(RewardCondition rewardCondition) {
        logger.info("Fetching referral rewards by condition: {}", rewardCondition);
        return referralRewardRepository.findByRewardCondition(rewardCondition).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReferralRewardDto> getRewardsBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        logger.info("Fetching referral rewards between {} and {}", startDate, endDate);
        return referralRewardRepository.findByRewardDateBetween(startDate, endDate).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReferralRewardDto updateReward(Long id, UpdateReferralRewardDto updateDto) {
        ReferralReward reward = referralRewardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Referral reward with ID " + id + " not found"));

        if (updateDto.getRewardType() != null) {
            reward.setRewardType(updateDto.getRewardType());
        }
        if (updateDto.getRewardAmount() != null) {
            reward.setRewardAmount(updateDto.getRewardAmount());
        }
        if (updateDto.getRewardCondition() != null) {
            reward.setRewardCondition(updateDto.getRewardCondition());
        }

        ReferralReward updatedReward = referralRewardRepository.save(reward);
        logger.info("Updated referral reward with ID {}, type: {}, amount: {}, condition: {}",
                id, updatedReward.getRewardType(), updatedReward.getRewardAmount(), updatedReward.getRewardCondition());
        return convertToDto(updatedReward);
    }

    @Transactional
    public void deleteReward(Long id) {
        referralRewardRepository.findById(id)
                .ifPresentOrElse(reward -> {
                    referralRewardRepository.delete(reward);
                    logger.info("Deleted referral reward with ID: {}", id);
                }, () -> logger.warn("Referral reward with ID {} not found, cannot delete.", id));
    }
}