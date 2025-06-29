package com.sarmo.chatservice.config;

import com.sarmo.chatservice.jwt.JwtTokenDataExtractor;
import com.sarmo.chatservice.security.websocket.WebSocketAuthChannelInterceptor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.SpringAuthorizationEventPublisher;
import org.springframework.security.messaging.access.intercept.AuthorizationChannelInterceptor;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;
import org.springframework.security.messaging.context.AuthenticationPrincipalArgumentResolver;
import org.springframework.security.messaging.context.SecurityContextChannelInterceptor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtTokenDataExtractor jwtTokenDataExtractor;
    private final ApplicationContext applicationContext;

    public WebSocketConfig(JwtTokenDataExtractor jwtTokenDataExtractor, ApplicationContext applicationContext) {
        this.jwtTokenDataExtractor = jwtTokenDataExtractor;
        this.applicationContext = applicationContext;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue", "/user");
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new WebSocketAuthChannelInterceptor(jwtTokenDataExtractor));
        registration.interceptors(new SecurityContextChannelInterceptor());
        AuthorizationChannelInterceptor authz = new AuthorizationChannelInterceptor(messageAuthorizationManager());
        authz.setAuthorizationEventPublisher(new SpringAuthorizationEventPublisher(applicationContext));
        registration.interceptors(authz);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new AuthenticationPrincipalArgumentResolver());
    }

    @Bean
    public AuthorizationManager<Message<?>> messageAuthorizationManager() {
        return MessageMatcherDelegatingAuthorizationManager.builder()
                .simpTypeMatchers(SimpMessageType.CONNECT, SimpMessageType.HEARTBEAT).permitAll()
                .simpDestMatchers("/app/**").authenticated()
                .simpSubscribeDestMatchers("/user/queue/**", "/topic/**").authenticated()
                .anyMessage().denyAll()
                .build();
    }
}