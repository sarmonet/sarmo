package com.sarmo.authservice.repository;

import com.sarmo.authservice.entity.RefreshToken;
import com.sarmo.authservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByUserId(Long userId);

    void deleteByUser(User user);
}