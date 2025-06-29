package com.sarmo.authservice.repository;

import com.sarmo.authservice.entity.TwoFactorCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TwoFactorCodeRepository extends JpaRepository<TwoFactorCode, Long> {

    TwoFactorCode findByVerificationId(UUID verificationId);

    void delete(TwoFactorCode twoFactorCode);

    void deleteByVerificationId(UUID verificationId);
}