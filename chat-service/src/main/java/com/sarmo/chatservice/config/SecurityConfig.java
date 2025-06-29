package com.sarmo.chatservice.config;

import com.sarmo.chatservice.jwt.JwtTokenDataExtractor;
import com.sarmo.chatservice.security.filter.JwtTokenAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtTokenDataExtractor jwtTokenDataExtractor;

    public SecurityConfig(JwtTokenDataExtractor jwtTokenDataExtractor) {
        this.jwtTokenDataExtractor = jwtTokenDataExtractor;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/ws/**").permitAll()
                        .requestMatchers("/api/v1/chat/**").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .anyRequest().denyAll()
                )
                .addFilterBefore(new JwtTokenAuthenticationFilter(jwtTokenDataExtractor), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}