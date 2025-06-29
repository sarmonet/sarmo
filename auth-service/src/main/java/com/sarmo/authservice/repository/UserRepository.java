package com.sarmo.authservice.repository;

import com.sarmo.authservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByProviderUserId(String providerUserId);

    Optional<User> findByEmail(String email);

    Optional<User> findByPhoneNumber(String phoneNumber);
}