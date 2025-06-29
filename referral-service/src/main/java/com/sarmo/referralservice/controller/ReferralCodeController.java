package com.sarmo.referralservice.controller;

import com.sarmo.referralservice.dto.CreateReferralCodeDto;
import com.sarmo.referralservice.dto.ReferralCodeDto;
import com.sarmo.referralservice.dto.UpdateReferralCodeDto;
import com.sarmo.referralservice.exception.EntityNotFoundException;
import com.sarmo.referralservice.service.ReferralCodeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/referral/code")
public class ReferralCodeController {

    private final ReferralCodeService referralCodeService;

    public ReferralCodeController(ReferralCodeService referralCodeService) {
        this.referralCodeService = referralCodeService;
    }

    @PostMapping
    public ResponseEntity<ReferralCodeDto> createReferralCode(@Valid @RequestBody CreateReferralCodeDto createDto) {
        try {
            // Note: The createReferralCode method in the service returns the entity, not DTO.
            // We should ideally update the service method to return DTO directly.
            // For now, we'll fetch it again or update the service.
            referralCodeService.createReferralCode(createDto);
            return referralCodeService.getReferralCodeByUserId(createDto.getUserId())
                    .map(dto -> {
                        URI location = ServletUriComponentsBuilder
                                .fromCurrentRequest()
                                .path("/{id}")
                                .buildAndExpand(dto.getId())
                                .toUri();
                        return ResponseEntity.created(location).body(dto);
                    })
                    .orElse(ResponseEntity.status(HttpStatus.CREATED).build()); // Should always have a DTO here
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not create referral code", e);
        }
    }

    @GetMapping("/{code}")
    public ResponseEntity<ReferralCodeDto> getReferralCodeByCode(@PathVariable String code) {
        Optional<ReferralCodeDto> referralCodeDto = referralCodeService.getReferralCodeByCode(code);
        return referralCodeDto.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<ReferralCodeDto> getReferralCodeByUserId(@PathVariable Long userId) {
        Optional<ReferralCodeDto> referralCodeDto = referralCodeService.findByUserId(userId);
        return referralCodeDto.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReferralCodeDto> updateReferralCode(@PathVariable Long id, @Valid @RequestBody UpdateReferralCodeDto updateDto) {
        try {
            ReferralCodeDto updatedDto = referralCodeService.updateReferralCode(id, updateDto);
            return ResponseEntity.ok(updatedDto);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not update referral code", e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReferralCode(@PathVariable Long id) {
        try {
            referralCodeService.deleteReferralCode(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Consider adding an endpoint to generate a referral code for a specific user
    @PostMapping("/users/{userId}")
    public ResponseEntity<ReferralCodeDto> generateReferralCodeForUser(@PathVariable Long userId) {
        try {
            ReferralCodeDto createdDto = referralCodeService.createReferralCodeForUser(userId);
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequestUri()
                    .path("/{code}")
                    .buildAndExpand(createdDto.getCode())
                    .toUri();
            return ResponseEntity.created(location).body(createdDto);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not generate referral code for user", e);
        }
    }
}