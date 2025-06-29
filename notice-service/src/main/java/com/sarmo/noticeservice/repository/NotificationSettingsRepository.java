package com.sarmo.noticeservice.repository;

import com.sarmo.noticeservice.entity.NotificationSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationSettingsRepository extends JpaRepository<NotificationSettings, Long> {
    List<NotificationSettings> findByUser_Id(Long userId);
}
