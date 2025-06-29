package com.sarmo.userservice.repository;

import com.sarmo.userservice.entity.UserSettings;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSettingsRepository extends MongoRepository<UserSettings, String> {
}
