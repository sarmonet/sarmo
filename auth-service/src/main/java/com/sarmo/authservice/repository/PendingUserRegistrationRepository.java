package com.sarmo.authservice.repository;

import com.sarmo.authservice.entity.PendingUserRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface PendingUserRegistrationRepository extends JpaRepository<PendingUserRegistration, UUID> {
    Optional<PendingUserRegistration> findByContact(String contact);
}