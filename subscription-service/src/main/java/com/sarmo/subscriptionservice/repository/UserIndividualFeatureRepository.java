package com.sarmo.subscriptionservice.repository;

import com.sarmo.subscriptionservice.entity.UserIndividualFeature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserIndividualFeatureRepository extends JpaRepository<UserIndividualFeature, Long> {
    List<UserIndividualFeature> findByUserId(Long userId);
}