package com.sarmo.authservice.controller;

import com.nimbusds.jose.jwk.JWKSet;
import com.sarmo.authservice.exception.InvalidVerificationCodeException;
import com.sarmo.authservice.exception.UserNotFoundAfterVerificationException;
import com.sarmo.authservice.service.TwoFactorAuthService;
import jakarta.validation.Valid;
import com.sarmo.authservice.dto.JwtAuthResponse;
import com.sarmo.authservice.dto.LoginRequest;
import com.sarmo.authservice.dto.RefreshTokenRequest;
import com.sarmo.authservice.dto.RegisterRequest;
import com.sarmo.authservice.security.AuthService;
import com.sarmo.authservice.security.JwtTokenProvider;
import com.sarmo.authservice.security.RefreshTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;
import java.util.UUID;

// Импорты для обработки исключений
import com.sarmo.authservice.exception.EmailAlreadyTakenException; // Добавим, если еще нет
import com.sarmo.authservice.exception.UserAlreadyPendingConfirmationException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;


@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final TwoFactorAuthService twoFactorAuthService;

    public AuthController(AuthService authService, JwtTokenProvider jwtTokenProvider, RefreshTokenService refreshTokenService, TwoFactorAuthService twoFactorAuthService) {
        this.authService = authService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenService = refreshTokenService;
        this.twoFactorAuthService = twoFactorAuthService;
    }

    @PostMapping("/register")
    public ResponseEntity<UUID> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            UUID verificationId = authService.register(registerRequest);
            return new ResponseEntity<>(verificationId, HttpStatus.ACCEPTED);
        } catch (EmailAlreadyTakenException e) {
            logger.warn("Registration failed: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.CONFLICT); // 409 Conflict
        } catch (UserAlreadyPendingConfirmationException e) {
            logger.warn("Registration failed: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.CONFLICT); // 409 Conflict
        } catch (RuntimeException e) {
            logger.error("An unexpected error occurred during registration for contact: {}", registerRequest.getContact(), e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error
        } catch (Exception e) {
            logger.error("An unexpected error occurred during registration for contact: {}", registerRequest.getContact(), e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error
        }
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Object response = authService.authenticate(loginRequest);
            if (response instanceof UUID) {
                return new ResponseEntity<>(Map.of("verificationId", response.toString(), "message", "Two-factor authentication required. Please verify your contact."), HttpStatus.OK);
            } else if (response instanceof JwtAuthResponse) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body("Unknown response type from authentication.");
            }
        } catch (DisabledException e) {
            logger.warn("Login failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (BadCredentialsException e) {
            logger.warn("Login failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (LockedException e) {
            logger.warn("Login failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            logger.error("An unexpected error occurred during login for contact: {}", loginRequest.getContact(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An internal server error occurred during login.");
        }
    }

    @PostMapping("/two-factor/verify/{verificationId}")
    public ResponseEntity<?> verifyTwoFactorCode(
            @PathVariable UUID verificationId,
            @RequestParam("code") String code) {
        try {
            JwtAuthResponse jwtAuthResponse = authService.verifyTwoFactorCode(verificationId, code);
            return ResponseEntity.ok(jwtAuthResponse);
        } catch (InvalidVerificationCodeException e) {
            logger.warn("Invalid verification code or registration session expired: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (UserNotFoundAfterVerificationException e) {
            logger.warn("User not found after verification: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            logger.error("Internal server error during verification and/or token generation", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        } catch (RuntimeException e) {
            logger.error("An unexpected error occurred during 2FA verification or user creation: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/two-factor/resend/{verificationId}")
    public ResponseEntity<?> resendCode(
            @PathVariable UUID verificationId,
            @RequestParam(value = "expirationMinutes", defaultValue = "10", required = false) int expirationMinutes) {
        logger.info("Received resend request for verificationId: {}", verificationId);

        try {
            UUID newVerificationId = twoFactorAuthService.resendCode(verificationId, expirationMinutes);

            logger.info("Resend successful for original verificationId: {}. New verificationId: {}", verificationId, newVerificationId);

            return ResponseEntity.ok().body(Map.of("newVerificationId", newVerificationId.toString(), "message", "New verification code sent."));

        } catch (InvalidVerificationCodeException e) {
            logger.warn("Resend failed for verificationId {}: {}", verificationId, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());

        } catch (IllegalArgumentException e) {
            logger.warn("Resend failed for verificationId {} due to invalid arguments: {}", verificationId, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());

        } catch (Exception e) {
            logger.error("An unexpected error occurred during resend for verificationId {}: {}", verificationId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An internal server error occurred during resend.");
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtAuthResponse> refreshTokens(@RequestBody RefreshTokenRequest request) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        JwtAuthResponse response = refreshTokenService.refreshTokens(request.getRefreshToken());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/jwks")
    public ResponseEntity<Map<String, Object>> getJwks() {
        return ResponseEntity.ok(jwtTokenProvider.getJwkSet().toJSONObject());
    }
}