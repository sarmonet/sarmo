package com.sarmo.referralservice.repository;

import com.sarmo.referralservice.entity.ReferralCode;
import com.sarmo.referralservice.entity.ReferralUsage;
import com.sarmo.referralservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReferralUsageRepository extends JpaRepository<ReferralUsage, Long> {

    List<ReferralUsage> findByReferralCode(ReferralCode referralCode);

    List<ReferralUsage> findByReferredUser_UserId(Long referredUserId);

    @Query("SELECT ru.referredUser FROM ReferralUsage ru WHERE ru.referralCode.user.userId = :referrerId")
    List<User> findReferredUsersByReferrerId(@Param("referrerId") Long referrerId);

    List<ReferralUsage> findByUsageDateBetween(LocalDateTime startDate, LocalDateTime endDate);
}