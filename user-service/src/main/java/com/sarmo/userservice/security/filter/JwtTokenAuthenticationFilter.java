package com.sarmo.userservice.security.filter;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import com.sarmo.userservice.jwt.JwtTokenDataExtractor;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException; // Используем стандартное исключение
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;

public class JwtTokenAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenDataExtractor jwtTokenDataExtractor;
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenAuthenticationFilter.class);

    public JwtTokenAuthenticationFilter(JwtTokenDataExtractor jwtTokenDataExtractor) {
        this.jwtTokenDataExtractor = jwtTokenDataExtractor;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        logger.debug("Processing request in JwtTokenAuthenticationFilter");

        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            logger.debug("Authorization header not found or does not start with Bearer. Skipping JWT authentication.");
            filterChain.doFilter(request, response);
            return;
        }

        String token;
        try {
            token = jwtTokenDataExtractor.extractToken(authorizationHeader);
        } catch (IllegalArgumentException e) {
            logger.error("Error extracting token from header", e);
            // Если формат заголовка неверный, выбрасываем исключение аутентификации
            throw new BadCredentialsException("Invalid Authorization header format", e);
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
                    // Первый аргумент - principal (пользователь), второй - credentials (пароль, не нужен), третий - authorities (роли)
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(username, null, authorities);

                    // Помещаем объект аутентификации в SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    logger.debug("User '{}' successfully authenticated with roles: {}", username, roles);

                } else {
                    logger.warn("Username or roles not found in JWT claims for token: {}", token);
                    // Если в токене нет необходимых данных, считаем его некорректным
                    throw new BadCredentialsException("Invalid token claims: username or roles missing");
                }
            } else {
                logger.warn("JWT claims set is null after decoding for token: {}", token);
                // Если claimsSet null, токен невалиден
                throw new BadCredentialsException("Invalid token: claims set is null");
            }

        } catch (ParseException | JOSEException e) {
            logger.error("Error decoding or parsing JWT: {}", e.getMessage(), e);
            // При любых ошибках парсинга или криптографии (подпись, формат), выбрасываем исключение аутентификации
            throw new BadCredentialsException("Invalid or expired token", e);
        }

        // Передаем запрос дальше только если аутентификация прошла успешно или токена не было
        filterChain.doFilter(request, response);
    }
}