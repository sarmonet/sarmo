package com.sarmo.authservice.controller;

import com.sarmo.authservice.entity.RefreshToken;
import com.sarmo.authservice.entity.User;
import com.sarmo.authservice.security.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth/refresh-tokens")
@PreAuthorize("hasRole('ADMIN')")
public class RefreshTokenController {

    private final RefreshTokenService refreshTokenService;

    public RefreshTokenController(RefreshTokenService refreshTokenService) {
        this.refreshTokenService = refreshTokenService;
    }

    @GetMapping
    public ResponseEntity<List<RefreshToken>> getAllRefreshTokens() {
        return new ResponseEntity<>(refreshTokenService.getAllRefreshTokens(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RefreshToken> getRefreshTokenById(@PathVariable Long id) {
        Optional<RefreshToken> refreshToken = refreshTokenService.getRefreshTokenById(id);
        return refreshToken.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/token/{token}")
    public ResponseEntity<RefreshToken> getRefreshTokenByToken(@PathVariable String token) {
        Optional<RefreshToken> refreshToken = refreshTokenService.getRefreshTokenByToken(token);
        return refreshToken.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<RefreshToken> getRefreshTokenByUserId(@PathVariable Long userId) {
        Optional<RefreshToken> refreshToken = refreshTokenService.getRefreshTokenByUserId(userId);
        return refreshToken.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<RefreshToken> createRefreshToken(@RequestBody RefreshToken refreshToken) {
        return new ResponseEntity<>(refreshTokenService.createRefreshToken(refreshToken), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RefreshToken> updateRefreshToken(@PathVariable Long id, @RequestBody RefreshToken refreshTokenDetails) {
        RefreshToken updatedRefreshToken = refreshTokenService.updateRefreshToken(id, refreshTokenDetails);
        if (updatedRefreshToken != null) {
            return new ResponseEntity<>(updatedRefreshToken, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRefreshToken(@PathVariable Long id) {
        refreshTokenService.deleteRefreshToken(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/user")
    public ResponseEntity<Void> deleteRefreshTokenByUser(@RequestBody User user) {
        refreshTokenService.deleteRefreshTokenByUser(user);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}