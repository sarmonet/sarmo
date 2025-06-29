package com.sarmo.userservice.repository;

import com.sarmo.userservice.entity.NotificationSettings;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationSettingsRepository extends MongoRepository<NotificationSettings, String> {
}