package com.sarmo.authservice.security;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import com.sarmo.authservice.config.JwtConfig;
import com.sarmo.authservice.exception.InvalidJwtRolesException;
import com.sarmo.authservice.exception.JwtGenerationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {
    private final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class); // Добавляем логгер

    private final JwtConfig jwtConfig;

    private final RSAKey rsaKey;

    public JwtTokenProvider(JwtConfig jwtConfig, RSAKey rsaKey) {
        this.jwtConfig = jwtConfig;
        this.rsaKey = rsaKey;
    }

    public JWKSet getJwkSet() {
        return new JWKSet(rsaKey);
    }

//    public JWKSet getJwkSet() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
//        try {
//            RSAKey rsaKey = jwtConfig.getRsaKey();
//            logger.debug("RSAKey from JwtConfig: {}", rsaKey.toString()); // Добавляем логирование
//            return new JWKSet(rsaKey);
//        } catch (Exception e) {
//            logger.error("Failed to create JWKSet", e); // Добавляем обработку исключений
//            throw e;
//        }
//    }

    public String generateToken(Authentication authentication) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        return generateToken((UserPrincipal) authentication.getPrincipal());
    }

    public String generateToken(UserDetails userDetails) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        return generateToken((UserPrincipal) userDetails);
    }

    public String generateToken(UserPrincipal userPrincipal) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtConfig.getAccessTokenExpiration());
        PrivateKey privateKey = jwtConfig.privateKey();

        try {
            return Jwts.builder()
                    .setSubject(String.valueOf(userPrincipal.getId()))
                    .setIssuedAt(now)
                    .setExpiration(expiryDate)
                    .claim("roles", userPrincipal.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.toList()))
                    .signWith(privateKey, SignatureAlgorithm.RS256)
                    .compact();
        } catch (Exception e) {
            logger.error("Failed to generate JWT", e);
            throw new JwtGenerationException("Failed to generate JWT", e);
        }
    }

    public Long getUserIdFromJWT(String token) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        PublicKey publicKey = jwtConfig.publicKey();

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return Long.valueOf(claims.getSubject());
    }

    public List<String> getRolesFromToken(String token) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidJwtRolesException {
        PublicKey publicKey = jwtConfig.publicKey();

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        Object rolesObject = claims.get("roles");

        if (rolesObject instanceof List<?> rolesList) {
            List<String> stringRoles = new ArrayList<>();

            for (Object role : rolesList) {
                if (role instanceof String) {
                    stringRoles.add((String) role);
                } else {
                    logger.warn("Non-string role found in token: {}", role); // Логируем предупреждение
                    throw new InvalidJwtRolesException("Non-string role found in token"); // Пробрасываем исключение
                }
            }

            return stringRoles;
        } else {
            logger.error("'roles' claim is not a list."); // Логируем ошибку
            throw new InvalidJwtRolesException("'roles' claim is not a list"); // Пробрасываем исключение
        }
    }

    public boolean validateToken(String authToken) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        PublicKey publicKey = jwtConfig.publicKey();

        try {
            Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(authToken);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }


}