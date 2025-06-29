package com.sarmo.chatservice.jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jwt.JWTClaimsSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.List;
import java.util.Optional;

@Service
public class JwtTokenDataExtractor {

    private final JwtTokenDecoder jwtTokenDecoder;
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenDataExtractor.class);

    public JwtTokenDataExtractor(JwtTokenDecoder jwtTokenDecoder) {
        this.jwtTokenDecoder = jwtTokenDecoder;
    }

    public Long getUserId(String token, JWKSet jwkSet) throws JOSEException, ParseException {
        logger.info("Extracting userId from token: {}", token);
        JWTClaimsSet claimsSet = jwtTokenDecoder.decodeToken(token, jwkSet);
        return Long.parseLong(claimsSet.getSubject());
    }

    public List<String> getRoles(String token) throws JOSEException, ParseException {
        logger.info("Extracting roles from token: {}", token);
        Optional<JWTClaimsSet> claimsSetOptional = getClaimsSet(token);
        if (claimsSetOptional.isPresent()) {
            return claimsSetOptional.get().getStringListClaim("roles");
        }
        return null; // Или выбросить исключение, в зависимости от логики вашего приложения
    }

    public JWTClaimsSet getAllClaims(String token) throws JOSEException, ParseException {
        logger.info("Extracting all claims from token: {}", token);
        Optional<JWTClaimsSet> claimsSetOptional = getClaimsSet(token);
        return claimsSetOptional.orElse(null); // Или выбросить исключение, в зависимости от логики вашего приложения
    }

    public String extractToken(String authorizationHeader) {
        logger.info("Extracting token from Authorization header: {}", authorizationHeader);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String extractedToken = authorizationHeader.substring(7);
            logger.debug("Extracted token: {}", extractedToken);
            return extractedToken;
        }
        logger.warn("Invalid Authorization header: {}", authorizationHeader);
        throw new IllegalArgumentException("Invalid Authorization header");
    }

    private Optional<JWTClaimsSet> getClaimsSet(String token) throws JOSEException, ParseException {
        Optional<JWKSet> jwkSetOptional = jwtTokenDecoder.getJwkSet();
        if (jwkSetOptional.isPresent()) {
            return Optional.of(jwtTokenDecoder.decodeToken(token, jwkSetOptional.get()));
        }
        return Optional.empty();
    }

    public Long extractUserIdFromToken(String authorizationHeader) throws JOSEException, ParseException {
        logger.debug("Extracting user id from token");
        String token = extractToken(authorizationHeader);
        logger.debug("Extracted token: {}", token);

        Optional<JWKSet> jwkSetOptional = jwtTokenDecoder.getJwkSet();
        if (jwkSetOptional.isEmpty()) {
            logger.error("Failed to fetch JWK set from auth-service");
            throw new RuntimeException("Failed to fetch JWK set from auth-service");
        }
        JWKSet jwkSet = jwkSetOptional.get();
        logger.debug("Fetched JWK set: {}", jwkSet);

        return getUserId(token, jwkSet);
    }

}