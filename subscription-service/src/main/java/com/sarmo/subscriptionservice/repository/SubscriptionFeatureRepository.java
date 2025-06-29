package com.sarmo.subscriptionservice.repository;

import com.sarmo.subscriptionservice.entity.SubscriptionFeature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionFeatureRepository extends JpaRepository<SubscriptionFeature, Long> {
    SubscriptionFeature findByName(String name);
}