package com.sarmo.authservice.service;

import com.sarmo.authservice.entity.TwoFactorCode;
import com.sarmo.authservice.repository.TwoFactorCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TwoFactorCodeService {

    private final TwoFactorCodeRepository twoFactorCodeRepository;

    @Autowired
    public TwoFactorCodeService(TwoFactorCodeRepository twoFactorCodeRepository) {
        this.twoFactorCodeRepository = twoFactorCodeRepository;
    }

    public List<TwoFactorCode> getAllTwoFactorCodes() {
        return twoFactorCodeRepository.findAll();
    }

    public Optional<TwoFactorCode> getTwoFactorCodeById(Long id) {
        return twoFactorCodeRepository.findById(id);
    }

    public TwoFactorCode getTwoFactorCodeByVerificationId(UUID verificationId) {
        return twoFactorCodeRepository.findByVerificationId(verificationId);
    }

    public TwoFactorCode createTwoFactorCode(TwoFactorCode twoFactorCode) {
        return twoFactorCodeRepository.save(twoFactorCode);
    }

    public TwoFactorCode updateTwoFactorCode(Long id, TwoFactorCode twoFactorCodeDetails) {
        Optional<TwoFactorCode> optionalTwoFactorCode = twoFactorCodeRepository.findById(id);
        if (optionalTwoFactorCode.isPresent()) {
            TwoFactorCode twoFactorCode = optionalTwoFactorCode.get();
            twoFactorCode.setCode(twoFactorCodeDetails.getCode());
            twoFactorCode.setCreationTime(twoFactorCodeDetails.getCreationTime());
            twoFactorCode.setExpirationTime(twoFactorCodeDetails.getExpirationTime());
            twoFactorCode.setEmailOrPhoneNumber(twoFactorCodeDetails.getEmailOrPhoneNumber());
            twoFactorCode.setVerificationId(twoFactorCodeDetails.getVerificationId());
            return twoFactorCodeRepository.save(twoFactorCode);
        }
        return null;
    }

    public void deleteTwoFactorCode(Long id) {
        twoFactorCodeRepository.deleteById(id);
    }

    public void deleteTwoFactorCodeByVerificationId(UUID verificationId) {
        twoFactorCodeRepository.deleteByVerificationId(verificationId);
    }
}