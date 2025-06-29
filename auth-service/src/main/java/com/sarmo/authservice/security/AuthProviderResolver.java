package com.sarmo.authservice.security;

import com.sarmo.authservice.enums.AuthProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AuthProviderResolver {

    private static final Logger logger = LoggerFactory.getLogger(AuthProviderResolver.class);

    public AuthProvider resolveAuthProvider(String provider) {
        if (provider == null || provider.trim().isEmpty()) {
            throw new IllegalArgumentException("Provider cannot be null or empty");
        }

        return switch (provider.toLowerCase()) {
            case "google" -> AuthProvider.GOOGLE;
            case "facebook" -> AuthProvider.FACEBOOK;
            case "apple" -> AuthProvider.APPLE;
            default -> {
                logger.error("Unknown provider: {}", provider);
                throw new IllegalArgumentException("Unknown authentication provider: " + provider);
            }
        };
    }
}