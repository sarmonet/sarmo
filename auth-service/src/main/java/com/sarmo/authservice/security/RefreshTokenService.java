package com.sarmo.authservice.security;

import com.sarmo.authservice.dto.JwtAuthResponse;
import com.sarmo.authservice.entity.RefreshToken;
import com.sarmo.authservice.entity.User;
import com.sarmo.authservice.exception.InvalidRefreshTokenException;
import com.sarmo.authservice.repository.RefreshTokenRepository;
import com.sarmo.authservice.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public RefreshTokenService(JwtTokenProvider jwtTokenProvider, UserDetailsService userDetailsService, RefreshTokenRepository refreshTokenRepository, UserRepository userRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    public JwtAuthResponse refreshTokens(String refreshTokenValue) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            throw new RuntimeException("Refresh token expired");
        }

        User user = refreshToken.getUser();
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String accessToken = jwtTokenProvider.generateToken(userDetails);

        Instant expiryThreshold = refreshToken.getExpiryDate().minusSeconds((refreshToken.getExpiryDate().toEpochMilli() - refreshToken.getCreationDate().toEpochMilli()) / 5000); // 20%

        if (refreshToken.getExpiryDate().isBefore(expiryThreshold)) {
            String newRefreshTokenValue = UUID.randomUUID().toString();
            Instant newExpiryDate = Instant.now().plusMillis(172_800_000); // 2 суток
//            refreshToken.builder()
//                    .token(newRefreshTokenValue)
//                    .creationDate(Instant.now())
//                    .expiryDate(newExpiryDate);
            refreshToken.setToken(newRefreshTokenValue);
            refreshToken.setCreationDate(Instant.now());
            refreshToken.setExpiryDate(newExpiryDate);
            refreshTokenRepository.save(refreshToken);

            JwtAuthResponse response = new JwtAuthResponse();
            response.setAccessToken(accessToken);
            response.setTokenType("Bearer");
            response.setRefreshToken(newRefreshTokenValue);
            return response;
        } else {
            // Возвращаем только access токен
            JwtAuthResponse response = new JwtAuthResponse();
            response.setAccessToken(accessToken);
            response.setTokenType("Bearer");
            return response;
        }
    }


    public RefreshToken createRefreshToken(String userIdentifier) {
        // Ищем пользователя по email или phoneNumber
        User user = userRepository.findByEmail(userIdentifier)
                .or(() -> userRepository.findByPhoneNumber(userIdentifier))
                .orElseThrow(() -> new RuntimeException(
                        "User not found with identifier: " + userIdentifier +
                                " (expected email or phone number)"));

        Optional<RefreshToken> existingToken = refreshTokenRepository.findByUserId(user.getId());

        RefreshToken refreshToken;
        if (existingToken.isPresent()) {
            refreshToken = existingToken.get();
            refreshToken.setToken(UUID.randomUUID().toString());
            refreshToken.setExpiryDate(Instant.now().plusMillis(172_800_000));
        } else {
            refreshToken = RefreshToken.builder()
                    .user(user)
                    .creationDate(Instant.now())
                    .expiryDate(Instant.now().plusMillis(172_800_000))
                    .token(UUID.randomUUID().toString())
                    .build();
        }

        return refreshTokenRepository.save(refreshToken);
    }

    public void deleteRefreshToken(String token) {
        refreshTokenRepository.findByToken(token).ifPresent(refreshTokenRepository::delete);
    }

    public RefreshToken verifyRefreshToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .filter(refreshToken -> refreshToken.getExpiryDate().isAfter(Instant.now()))
                .orElseThrow(() -> new InvalidRefreshTokenException("Refresh token is not valid"));
    }

    public List<RefreshToken> getAllRefreshTokens() {
        return refreshTokenRepository.findAll();
    }

    public Optional<RefreshToken> getRefreshTokenById(Long id) {
        return refreshTokenRepository.findById(id);
    }

    public Optional<RefreshToken> getRefreshTokenByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public Optional<RefreshToken> getRefreshTokenByUserId(Long userId) {
        return refreshTokenRepository.findByUserId(userId);
    }

    public RefreshToken createRefreshToken(RefreshToken refreshToken) {
        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken updateRefreshToken(Long id, RefreshToken refreshTokenDetails) {
        Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findById(id);
        if (optionalRefreshToken.isPresent()) {
            RefreshToken refreshToken = optionalRefreshToken.get();
            refreshToken.setToken(refreshTokenDetails.getToken());
            refreshToken.setUser(refreshTokenDetails.getUser());
            refreshToken.setExpiryDate(refreshTokenDetails.getExpiryDate());
            return refreshTokenRepository.save(refreshToken);
        }
        return null;
    }

    public void deleteRefreshToken(Long id) {
        refreshTokenRepository.deleteById(id);
    }

    public void deleteRefreshTokenByUser(User user) {
        refreshTokenRepository.deleteByUser(user);
    }
}