package com.sarmo.noticeservice.jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.sarmo.noticeservice.exception.JwtParsingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.text.ParseException;
import java.util.Objects;
import java.util.Optional;

@Component
public class JwtTokenDecoder {

    private final RestClient restClient;
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenDecoder.class);

    public JwtTokenDecoder(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.baseUrl("http://auth-service:8081").build();
    }

    public JWTClaimsSet decodeToken(String token, JWKSet jwkSet) throws JOSEException, ParseException {
        logger.info("Decoding token: {}", token);
        if (jwkSet == null || jwkSet.getKeys().isEmpty()) {
            logger.error("JWK set is empty or null");
            throw new JOSEException("JWK set is empty or null");
        }

        SignedJWT signedJWT = SignedJWT.parse(token);

        JWSVerifier verifier = null;
        for (JWK jwk : jwkSet.getKeys()) {
            if (jwk instanceof RSAKey) {
                verifier = new RSASSAVerifier((RSAKey) jwk);
                logger.debug("Found matching JWK: {}", jwk.getKeyID());
                break;
            }
        }

        if (verifier == null) {
            logger.error("No matching JWK found for token");
            throw new JOSEException("No matching JWK found for token");
        }

        if (!signedJWT.verify(verifier)) {
            logger.error("Invalid JWT signature");
            throw new JOSEException("Invalid JWT signature");
        }

        JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
        logger.debug("Decoded claims: {}", claimsSet.toString());
        return claimsSet;
    }

    public Optional<JWKSet> getJwkSet() {
        logger.info("Fetching JWK set from auth-service");
        try {
            String jwkSetJson = restClient.get()
                    .uri("/api/v1/auth/jwks")
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(String.class);

            logger.debug("Fetched JWK set JSON: {}", jwkSetJson); // Логируем JSON

            JWKSet jwkSet = JWKSet.parse(Objects.requireNonNull(jwkSetJson));

            if (jwkSet == null) {
                logger.error("JWK set is null");
                return Optional.empty();
            }

            logger.debug("Fetched JWK set: {}", jwkSet);
            return Optional.of(jwkSet);
        } catch (RestClientException e) {
            logger.error("Failed to fetch JWK set from auth-service", e);
            return Optional.empty();
        } catch (ParseException e) {
            logger.error("Error parsing JWK set JSON", e); // Логируем ошибку парсинга
            throw new JwtParsingException("Failed to parse JWK set JSON", e); // Выбрасываем исключение
        }
    }
}