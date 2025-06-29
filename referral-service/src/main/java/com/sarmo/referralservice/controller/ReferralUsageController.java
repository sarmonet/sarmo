package com.sarmo.referralservice.controller;

import com.sarmo.referralservice.dto.CreateReferralUsageDto;
import com.sarmo.referralservice.dto.ReferralUsageDto;
import com.sarmo.referralservice.exception.EntityNotFoundException;
import com.sarmo.referralservice.service.ReferralUsageService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/referral/usage")
public class ReferralUsageController {

    private final ReferralUsageService referralUsageService;

    public ReferralUsageController(ReferralUsageService referralUsageService) {
        this.referralUsageService = referralUsageService;
    }

    @PostMapping
    public ResponseEntity<ReferralUsageDto> recordReferralUsage(@Valid @RequestBody CreateReferralUsageDto createDto) {
        try {
            ReferralUsageDto recordedUsage = referralUsageService.recordReferralUsage(createDto);
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(recordedUsage.getId())
                    .toUri();
            return ResponseEntity.created(location).body(recordedUsage);
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not record referral usage", e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReferralUsageDto> getReferralUsageById(@PathVariable Long id) {
        Optional<ReferralUsageDto> usageDto = referralUsageService.getReferralUsageById(id);
        return usageDto.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<ReferralUsageDto>> getAllReferralUsages() {
        List<ReferralUsageDto> usages = referralUsageService.getAllReferralUsages();
        return ResponseEntity.ok(usages);
    }

    @GetMapping("/referred-user/{referredUserId}")
    public ResponseEntity<List<ReferralUsageDto>> getReferralUsagesByReferredUser(@PathVariable Long referredUserId) {
        List<ReferralUsageDto> usages = referralUsageService.getReferralUsagesByReferredUser(referredUserId);
        return ResponseEntity.ok(usages);
    }

    @GetMapping("/referral-code/{referralCodeValue}")
    public ResponseEntity<List<ReferralUsageDto>> getReferralUsagesByReferralCode(@PathVariable String referralCodeValue) {
        List<ReferralUsageDto> usages = referralUsageService.getReferralUsagesByReferralCode(referralCodeValue);
        return ResponseEntity.ok(usages);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReferralUsage(@PathVariable Long id) {
        try {
            referralUsageService.deleteReferralUsage(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/referrer/{referrerId}/referred-users")
    public ResponseEntity<List<Long>> getReferredUsersByReferrerId(@PathVariable Long referrerId) {
        List<Long> referredUsers = referralUsageService.getReferredUsersByReferrerId(referrerId);
        return ResponseEntity.ok(referredUsers);
    }
}