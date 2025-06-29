package com.sarmo.authservice.controller;

import com.sarmo.authservice.entity.TwoFactorCode;
import com.sarmo.authservice.service.TwoFactorCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth/two-factor-codes")
public class TwoFactorCodeController {

    private final TwoFactorCodeService twoFactorCodeService;

    @Autowired
    public TwoFactorCodeController(TwoFactorCodeService twoFactorCodeService) {
        this.twoFactorCodeService = twoFactorCodeService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TwoFactorCode>> getAllTwoFactorCodes() {
        return new ResponseEntity<>(twoFactorCodeService.getAllTwoFactorCodes(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TwoFactorCode> getTwoFactorCodeById(@PathVariable Long id) {
        Optional<TwoFactorCode> twoFactorCode = twoFactorCodeService.getTwoFactorCodeById(id);
        return twoFactorCode.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/verification/{verificationId}")
    public ResponseEntity<TwoFactorCode> getTwoFactorCodeByVerificationId(@PathVariable UUID verificationId) {
        TwoFactorCode twoFactorCode = twoFactorCodeService.getTwoFactorCodeByVerificationId(verificationId);
        if (twoFactorCode != null) {
            return new ResponseEntity<>(twoFactorCode, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<TwoFactorCode> createTwoFactorCode(@RequestBody TwoFactorCode twoFactorCode) {
        return new ResponseEntity<>(twoFactorCodeService.createTwoFactorCode(twoFactorCode), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TwoFactorCode> updateTwoFactorCode(@PathVariable Long id, @RequestBody TwoFactorCode twoFactorCodeDetails) {
        TwoFactorCode updatedTwoFactorCode = twoFactorCodeService.updateTwoFactorCode(id, twoFactorCodeDetails);
        if (updatedTwoFactorCode != null) {
            return new ResponseEntity<>(updatedTwoFactorCode, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTwoFactorCode(@PathVariable Long id) {
        twoFactorCodeService.deleteTwoFactorCode(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/verification/{verificationId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTwoFactorCodeByVerificationId(@PathVariable UUID verificationId) {
        twoFactorCodeService.deleteTwoFactorCodeByVerificationId(verificationId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}