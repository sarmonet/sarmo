package com.sarmo.subscriptionservice.repository;

import com.sarmo.subscriptionservice.entity.UserSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

@Repository
public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, Long> {
    List<UserSubscription> findByUserId(Long userId);
    Optional<UserSubscription> findByUserIdAndEndDateAfterOrEndDateIsNull(Long userId, LocalDate now);
}