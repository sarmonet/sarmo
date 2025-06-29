package com.sarmo.referralservice.repository;

import com.sarmo.referralservice.entity.ReferralReward;
import com.sarmo.referralservice.enums.RewardCondition;
import com.sarmo.referralservice.enums.RewardType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReferralRewardRepository extends JpaRepository<ReferralReward, Long> {

    List<ReferralReward> findByReferrer_UserId(Long referrerId);

    List<ReferralReward> findByReferred_UserId(Long referredId);

    List<ReferralReward> findByRewardType(RewardType rewardType);

    List<ReferralReward> findByRewardCondition(RewardCondition rewardCondition);

    List<ReferralReward> findByRewardDateBetween(LocalDateTime startDate, LocalDateTime endDate);

}