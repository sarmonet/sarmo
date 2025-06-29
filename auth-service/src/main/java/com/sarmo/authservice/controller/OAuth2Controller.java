package com.sarmo.authservice.controller;

import com.sarmo.authservice.dto.JwtAuthResponse;
import com.sarmo.authservice.service.OAuth2AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth/oauth2")
public class OAuth2Controller {

    private final OAuth2AuthService oAuth2AuthService;

    public OAuth2Controller(OAuth2AuthService oAuth2AuthService) {
        this.oAuth2AuthService = oAuth2AuthService;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> login(
            @RequestParam("code") String code,
            @RequestParam("codeVerifier") String codeVerifier,
            @RequestParam("provider") String provider) {
        return ResponseEntity.ok(oAuth2AuthService.authenticateOAuth2(code, codeVerifier, provider));
    }
}