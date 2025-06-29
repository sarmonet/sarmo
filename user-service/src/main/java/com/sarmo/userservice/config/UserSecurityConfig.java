package com.sarmo.userservice.config;


import com.sarmo.userservice.jwt.JwtTokenDataExtractor;
import com.sarmo.userservice.security.filter.JwtTokenAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity; // Важно!
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class UserSecurityConfig {

    private final JwtTokenDataExtractor jwtTokenDataExtractor;

    public UserSecurityConfig(JwtTokenDataExtractor jwtTokenDataExtractor) {
        this.jwtTokenDataExtractor = jwtTokenDataExtractor;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/v1/user/**").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .anyRequest().denyAll()
                )
                .addFilterBefore(new JwtTokenAuthenticationFilter(jwtTokenDataExtractor), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}