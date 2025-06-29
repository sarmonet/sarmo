package com.sarmo.chatservice.security.websocket;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import com.sarmo.chatservice.jwt.JwtTokenDataExtractor; // Используем ваш существующий экстрактор
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.BadCredentialsException; // Можно использовать существующий BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;


import java.text.ParseException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class WebSocketAuthChannelInterceptor implements ChannelInterceptor {

    private final JwtTokenDataExtractor jwtTokenDataExtractor;
    private static final Logger logger = LoggerFactory.getLogger(WebSocketAuthChannelInterceptor.class);

    public WebSocketAuthChannelInterceptor(JwtTokenDataExtractor jwtTokenDataExtractor) {
        this.jwtTokenDataExtractor = jwtTokenDataExtractor;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        // Мы интересуемся только фреймами CONNECT, так как именно там происходит аутентификация
        if (StompCommand.CONNECT.equals(Objects.requireNonNull(accessor).getCommand())) {
            String authorizationHeader = accessor.getFirstNativeHeader("Authorization");

            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                logger.debug("Authorization header not found or does not start with Bearer in WebSocket CONNECT frame. Denying connection.");
                // Для WebSocket-соединений при отсутствии токена или некорректном формате
                // лучше сразу бросить исключение, чтобы закрыть соединение
                throw new BadCredentialsException("Missing or invalid Authorization header for WebSocket connection.");
            }

            String token;
            try {
                token = jwtTokenDataExtractor.extractToken(authorizationHeader);
            } catch (IllegalArgumentException e) {
                logger.error("Error extracting token from WebSocket Authorization header", e);
                throw new BadCredentialsException("Invalid WebSocket Authorization header format", e);
            }

            try {
                // Используем ваш JwtTokenDataExtractor для получения клеймов
                JWTClaimsSet claimsSet = jwtTokenDataExtractor.getAllClaims(token);

                if (claimsSet != null) {
                    String username = claimsSet.getSubject(); // Предполагаем, что subject - это имя пользователя (или ID)
                    List<String> roles = jwtTokenDataExtractor.getRoles(token); // Получаем роли с помощью вашего метода

                    if (username != null && roles != null) {
                        List<SimpleGrantedAuthority> authorities = roles.stream()
                                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                                .collect(Collectors.toList());

                        // Создаем объект аутентификации
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(username, null, authorities);

                        accessor.setUser(authentication);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        logger.debug("WebSocket user '{}' successfully authenticated with roles: {}", username, roles);

                    } else {
                        logger.warn("Username or roles not found in JWT claims for WebSocket token: {}", token);
                        throw new BadCredentialsException("Invalid WebSocket token claims: username or roles missing");
                    }
                } else {
                    logger.warn("JWT claims set is null after decoding for WebSocket token: {}", token);
                    throw new BadCredentialsException("Invalid WebSocket token: claims set is null");
                }

            } catch (ParseException | JOSEException e) {
                logger.error("Error decoding or parsing WebSocket JWT: {}", e.getMessage(), e);
                throw new BadCredentialsException("Invalid or expired WebSocket token", e);
            }
        }
        return message;
    }
}