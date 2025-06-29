package com.sarmo.contentservice.security; // Или другой подходящий пакет

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component // Отмечаем как компонент Spring
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationEntryPoint.class);
    private final ObjectMapper objectMapper = new ObjectMapper(); // Для формирования JSON ответа

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        logger.error("Unauthorized error: {}", authException.getMessage());

        // Устанавливаем статус 401 Unauthorized
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // Добавляем заголовок WWW-Authenticate согласно стандартам для 401
        response.setHeader("WWW-Authenticate", "Bearer realm=\"Service\""); // Можете изменить realm

        // Формируем тело ответа в формате JSON
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        errorDetails.put("error", "Unauthorized");
        errorDetails.put("message", "Authentication failed: " + authException.getMessage()); // Можно передать сообщение об ошибке
        errorDetails.put("path", request.getRequestURI());

        objectMapper.writeValue(response.getOutputStream(), errorDetails);
    }
}