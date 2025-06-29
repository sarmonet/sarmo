package com.sarmo.referralservice.repository;

import com.sarmo.referralservice.entity.ReferralCode;
import com.sarmo.referralservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReferralCodeRepository extends JpaRepository<ReferralCode, Long> {

    Optional<ReferralCode> findByCode(String code);

    Optional<ReferralCode> findByUser_UserId(Long userId);

    Optional<ReferralCode> findByUser(User user); // Поиск по объекту User
}