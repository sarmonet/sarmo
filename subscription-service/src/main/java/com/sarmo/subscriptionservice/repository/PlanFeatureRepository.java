package com.sarmo.subscriptionservice.repository;

import com.sarmo.subscriptionservice.entity.PlanFeature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PlanFeatureRepository extends JpaRepository<PlanFeature, Long> {
    List<PlanFeature> findBySubscriptionPlanId(Long planId);
    PlanFeature findBySubscriptionPlanIdAndSubscriptionFeatureName(Long planId, String featureName);
}