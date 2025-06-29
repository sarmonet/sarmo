package com.sarmo.referralservice.controller;

import com.sarmo.referralservice.dto.CreateReferralRewardDto;
import com.sarmo.referralservice.dto.ReferralRewardDto;
import com.sarmo.referralservice.dto.UpdateReferralRewardDto;
import com.sarmo.referralservice.enums.RewardCondition;
import com.sarmo.referralservice.enums.RewardType;
import com.sarmo.referralservice.service.ReferralRewardService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/referral/reward")
public class ReferralRewardController {

    private final ReferralRewardService referralRewardService;

    public ReferralRewardController(ReferralRewardService referralRewardService) {
        this.referralRewardService = referralRewardService;
    }

    @PostMapping
    public ResponseEntity<ReferralRewardDto> createReward(@Valid @RequestBody CreateReferralRewardDto createDto) {
        try {
            ReferralRewardDto createdReward = referralRewardService.createReward(createDto);
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(createdReward.getId())
                    .toUri();
            return ResponseEntity.created(location).body(createdReward);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not create referral reward", e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReferralRewardDto> getRewardById(@PathVariable Long id) {
        Optional<ReferralRewardDto> rewardDto = referralRewardService.getRewardById(id);
        return rewardDto.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<ReferralRewardDto>> getAllRewards() {
        List<ReferralRewardDto> rewards = referralRewardService.getAllRewards();
        return ResponseEntity.ok(rewards);
    }

    @GetMapping("/referrer/{referrerId}")
    public ResponseEntity<List<ReferralRewardDto>> getRewardsByReferrer(@PathVariable Long referrerId) {
        List<ReferralRewardDto> rewards = referralRewardService.getRewardsByReferrer(referrerId);
        return ResponseEntity.ok(rewards);
    }

    @GetMapping("/referred/{referredId}")
    public ResponseEntity<List<ReferralRewardDto>> getRewardsByReferred(@PathVariable Long referredId) {
        List<ReferralRewardDto> rewards = referralRewardService.getRewardsByReferred(referredId);
        return ResponseEntity.ok(rewards);
    }

    @GetMapping("/type/{rewardType}")
    public ResponseEntity<List<ReferralRewardDto>> getRewardsByType(@PathVariable String rewardType) {
        try {
            List<ReferralRewardDto> rewards = referralRewardService.getRewardsByType(RewardType.valueOf(rewardType.toUpperCase()));
            return ResponseEntity.ok(rewards);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(List.of()); // Or a more specific error response
        }
    }

    @GetMapping("/condition/{rewardCondition}")
    public ResponseEntity<List<ReferralRewardDto>> getRewardsByCondition(@PathVariable String rewardCondition) {
        try {
            List<ReferralRewardDto> rewards = referralRewardService.getRewardsByCondition(RewardCondition.valueOf(rewardCondition.toUpperCase()));
            return ResponseEntity.ok(rewards);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(List.of()); // Or a more specific error response
        }
    }

    @GetMapping("/between")
    public ResponseEntity<List<ReferralRewardDto>> getRewardsBetweenDates(
            @RequestParam("start") LocalDateTime startDate,
            @RequestParam("end") LocalDateTime endDate) {
        List<ReferralRewardDto> rewards = referralRewardService.getRewardsBetweenDates(startDate, endDate);
        return ResponseEntity.ok(rewards);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReferralRewardDto> updateReward(@PathVariable Long id, @Valid @RequestBody UpdateReferralRewardDto updateDto) {
        try {
            ReferralRewardDto updatedReward = referralRewardService.updateReward(id, updateDto);
            return ResponseEntity.ok(updatedReward);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not update referral reward", e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReward(@PathVariable Long id) {
        try {
            referralRewardService.deleteReward(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}