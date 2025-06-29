package com.sarmo.subscriptionservice.repository;

import com.sarmo.subscriptionservice.entity.IndividualFeature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IndividualFeatureRepository extends JpaRepository<IndividualFeature, Long> {
    IndividualFeature findByName(String name);
}