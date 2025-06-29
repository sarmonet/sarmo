package com.sarmo.authservice.config;


import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

@Configuration
public class JwtConfig {
    private final ResourceLoader resourceLoader;

    @Value("${jwt.public.key.path}")
    private String publicKeyPath;

    @Value("${jwt.private.key.path}")
    private String privateKeyPath;

    @Value("${jwt.access.token.expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh.token.expiration}")
    private long refreshTokenExpiration;

    public JwtConfig(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Bean
    public RSAKey rsaKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        RSAPublicKey publicKey = (RSAPublicKey) publicKey();
        String kid = UUID.randomUUID().toString();
        return new RSAKey.Builder(publicKey)
                .keyID(kid)
                .keyUse(KeyUse.SIGNATURE)
                .algorithm(JWSAlgorithm.RS256)
                .build();
    }

    @Bean
    public PublicKey publicKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        // 1. Чтение содержимого файла публичного ключа
//        String publicKeyContent = new String(Files.readAllBytes(Paths.get(publicKeyPath)));
        Resource resource = resourceLoader.getResource(publicKeyPath);
        String publicKeyContent = new String(resource.getInputStream().readAllBytes());

        // 2. Очистка содержимого от заголовков PEM и символов новой строки
        publicKeyContent = publicKeyContent
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", ""); // Удаление всех пробельных символов

        // 3. Декодирование содержимого из Base64
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyContent);

        // 4. Создание PublicKey из декодированных данных
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        return keyFactory.generatePublic(keySpec);
    }


    @Bean
    public PrivateKey privateKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
//        String privateKeyContent = new String(Files.readAllBytes(Paths.get(privateKeyPath)));

        Resource resource = resourceLoader.getResource(privateKeyPath);
        String privateKeyContent = new String(resource.getInputStream().readAllBytes());

        System.out.println("Private key content: " + privateKeyContent);

        privateKeyContent = privateKeyContent.replaceAll("\\n", "").replace("-----BEGIN PRIVATE KEY-----", "").replace("-----END PRIVATE KEY-----", "");
        privateKeyContent = privateKeyContent.replaceAll("[^A-Za-z0-9+/=]", ""); // Дополнительная очистка
        System.out.println("Cleaned private key content: " + privateKeyContent);

        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyContent);
        System.out.println("Private key bytes length: " + privateKeyBytes.length);

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }
    public long getAccessTokenExpiration() {
        return accessTokenExpiration;
    }

    public long getRefreshTokenExpiration() {
        return refreshTokenExpiration;
    }
}