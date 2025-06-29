package com.sarmo.contentservice.config;

import com.sarmo.contentservice.jwt.JwtTokenDataExtractor;
import com.sarmo.contentservice.security.filter.JwtTokenAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
                        .requestMatchers(HttpMethod.GET, "/api/v1/content/news").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/content/news/{id}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/content/full/news/{id}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/content/random/news").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/content/random/article").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/v1/content/news/comment").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/content/news/comment/{id}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/content/news/{newsId}/comment").permitAll()

                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtTokenAuthenticationFilter(jwtTokenDataExtractor), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}