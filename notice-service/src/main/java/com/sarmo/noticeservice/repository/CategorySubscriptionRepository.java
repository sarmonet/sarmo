package com.sarmo.noticeservice.repository;

import com.sarmo.noticeservice.entity.CategorySubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategorySubscriptionRepository extends JpaRepository<CategorySubscription, Long> {
    @Query("SELECT cs FROM CategorySubscription cs WHERE cs.active = true AND HOUR(cs.createdAt) = :hour AND MINUTE(cs.createdAt) = :minute")
    List<CategorySubscription> findAllActiveByCreatedAtHourAndMinute(@Param("hour") int hour, @Param("minute") int minute);

}