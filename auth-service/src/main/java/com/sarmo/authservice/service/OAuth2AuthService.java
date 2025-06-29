package com.sarmo.authservice.service;

import com.sarmo.authservice.dto.GoogleTokenResponse;
import com.sarmo.authservice.dto.JwtAuthResponse;
import com.sarmo.authservice.entity.RefreshToken;
import com.sarmo.authservice.entity.Role;
import com.sarmo.authservice.entity.User;
import com.sarmo.authservice.enums.AuthProvider;
import com.sarmo.authservice.enums.RoleName;
import com.sarmo.authservice.producer.UserRegistrationProducer;
import com.sarmo.authservice.repository.RoleRepository;
import com.sarmo.authservice.repository.UserRepository;
import com.sarmo.authservice.security.AuthProviderResolver;
import com.sarmo.authservice.security.JwtTokenProvider;
import com.sarmo.authservice.security.RefreshTokenService;
import com.sarmo.authservice.security.UserPrincipal;
import com.sarmo.kafka.dto.UserRegistrationData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;

@Service
public class OAuth2AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private static final Logger logger = LoggerFactory.getLogger(OAuth2AuthService.class);
    private final AuthProviderResolver authProviderResolver;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserRegistrationProducer userRegistrationProducer;

    public OAuth2AuthService(JwtTokenProvider jwtTokenProvider, ClientRegistrationRepository clientRegistrationRepository,
                             UserRepository userRepository, RefreshTokenService refreshTokenService, AuthProviderResolver authProviderResolver, PasswordEncoder passwordEncoder, RoleRepository roleRepository, UserRegistrationProducer userRegistrationProducer) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.userRepository = userRepository;
        this.refreshTokenService = refreshTokenService;
        this.authProviderResolver = authProviderResolver;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.userRegistrationProducer = userRegistrationProducer;
    }

    public JwtAuthResponse authenticateOAuth2(String code, String codeVerifier, String provider) {
        logger.info("Starting OAuth2 authentication for provider: {}", provider);
        try {
            ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(provider);
            if (clientRegistration == null) {
                logger.error("ClientRegistration not found for provider: {}", provider);
                throw new IllegalArgumentException("Invalid provider: " + provider);
            }
            GoogleTokenResponse tokenResponse = exchangeAuthorizationCodeForToken(clientRegistration, code, codeVerifier);

            // Создаем OAuth2AccessToken из полученной строки
            OAuth2AccessToken accessToken = new OAuth2AccessToken(
                    OAuth2AccessToken.TokenType.BEARER,
                    tokenResponse.getAccessToken(),
                    Instant.now(),
                    Instant.now().plusSeconds(tokenResponse.getExpiresIn())
            );

            HashMap<String, Object> userAttributes = getUserAttributes(clientRegistration, accessToken);
            User user = createUserOrUpdate(userAttributes, provider);
            JwtAuthResponse jwtAuthResponse = generateJwtAuthResponse(user);
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getEmail());
            jwtAuthResponse.setRefreshToken(refreshToken.getToken());
            logger.info("OAuth2 authentication completed for provider: {}", provider);
            return jwtAuthResponse;
        } catch (WebClientResponseException e) {
            logger.error("WebClient error: {}", e.getMessage());
            throw new RuntimeException("OAuth2 authentication failed.", e);
        } catch (Exception e) {
            logger.error("Authentication failed: {}", e.getMessage());
            throw new RuntimeException("OAuth2 authentication failed.", e);
        }
    }

    private GoogleTokenResponse exchangeAuthorizationCodeForToken(ClientRegistration clientRegistration, String code, String codeVerifier) {
        String tokenUri = UriComponentsBuilder.fromUriString(clientRegistration.getProviderDetails().getTokenUri()).build().toUriString();
        return WebClient.create()
                .post()
                .uri(tokenUri)
                .body(BodyInserters.fromFormData("grant_type", "authorization_code")
                        .with("code", code)
                        .with("redirect_uri", clientRegistration.getRedirectUri())
                        .with("client_id", clientRegistration.getClientId())
                        .with("client_secret", clientRegistration.getClientSecret())
                        .with("code_verifier", codeVerifier))
                .retrieve()
                .bodyToMono(GoogleTokenResponse.class)
                .block();
    }

    private HashMap<String, Object> getUserAttributes(ClientRegistration clientRegistration, OAuth2AccessToken accessToken) {
        String userInfoUri = UriComponentsBuilder.fromUriString(clientRegistration.getProviderDetails().getUserInfoEndpoint().getUri()).build().toUriString();
        return WebClient.create()
                .get()
                .uri(userInfoUri)
                .headers(headers -> headers.setBearerAuth(accessToken.getTokenValue()))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<HashMap<String, Object>>() {})
                .block();
    }

    private User createUserOrUpdate(HashMap<String, Object> userAttributes, String provider) {
        if (userAttributes == null) {
            logger.error("User attributes are null for provider: {}", provider);
            throw new IllegalArgumentException("User attributes cannot be null");
        }

        // Используем "sub" в качестве providerUserId для Google
        String providerUserId = getStringAttribute(userAttributes, "sub", provider);
        String email = getStringAttribute(userAttributes, "email", provider);
        String name = getStringAttribute(userAttributes, "name", provider);
        String phoneNumber = getStringAttribute(userAttributes, "phoneNumber", provider); // Пытаемся получить номер телефона

        AuthProvider authProvider = authProviderResolver.resolveAuthProvider(provider);

        NameParts nameParts = splitFullName(name);

        try{
            User user = userRepository.findByProviderUserId(providerUserId);

            if (user == null) {
                user = new User();
                user.setProviderUserId(providerUserId);
                user.setEmail(email);
                user.setName(name); // Сохраняем полное имя в сущности User
                user.setPhoneNumber(phoneNumber); // Сохраняем номер телефона
                user.setAuthProvider(authProvider);
                user.setPassword(passwordEncoder.encode("oauth2user"));
                user.setRoles(new HashSet<>());

                Role userRole = roleRepository.findByName(RoleName.USER.name())
                        .orElseThrow(() -> new RuntimeException("Role USER not found"));
                user.getRoles().add(userRole);

                user = userRepository.save(user);

                userRegistrationProducer.sendUserRegistrationMessage(
                        new UserRegistrationData(user.getId(), email != null ? email : phoneNumber, nameParts.firstName(), nameParts.lastName())
                );

                logger.info("New user registered: {} with provider: {}", email != null ? email : phoneNumber, provider);
            } else {
                user.setEmail(email);
                user.setName(name); // Обновляем полное имя в сущности User
                user.setPhoneNumber(phoneNumber); // Обновляем номер телефона
                userRepository.save(user);
                logger.info("Existing user updated: {} with provider: {}", email != null ? email : phoneNumber, provider);
            }
            return user;
        } catch (Exception e) {
            logger.error("Error during user creation or update for provider: {}", provider, e);
            throw new RuntimeException("Error during user creation or update", e);
        }
    }

    private NameParts splitFullName(String fullName) {
        String firstName = null;
        String lastName = null;

        if (fullName != null && !fullName.isEmpty()) {
            String[] namePartsArray = fullName.split(" ", 2); // Разделяем на две части по первому пробелу
            firstName = namePartsArray[0];
            if (namePartsArray.length > 1) {
                lastName = namePartsArray[1];
            }
        }
        return new NameParts(firstName, lastName);
    }

    private record NameParts(String firstName, String lastName) {}

    private String getStringAttribute(HashMap<String, Object> attributes, String key, String provider) {
        if (attributes.containsKey(key) && attributes.get(key) != null) {
            return attributes.get(key).toString();
        } else {
            logger.warn("Attribute '{}' is missing or null for provider: {}", key, provider);
            return null;
        }
    }
    private JwtAuthResponse generateJwtAuthResponse(User user) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        UserPrincipal userPrincipal = UserPrincipal.create(user);
        String jwt = jwtTokenProvider.generateToken(userPrincipal);
        logger.info("User authenticated: {}", user.getEmail());
        return new JwtAuthResponse(jwt);
    }
}