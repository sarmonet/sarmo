package com.sarmo.listingservice.service;

import com.google.cloud.translate.v3.*;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TranslationService {

    private static final Logger logger = LoggerFactory.getLogger(TranslationService.class);

    private final String projectId;
    private TranslationServiceClient client;
    private boolean isInitialized = false;

    public TranslationService(
            @Value("${google.cloud.project.id}") String projectId,
            @Value("${google.cloud.credentials.location}") Resource credentialsLocation) {
        this.projectId = projectId;

        try {
            InputStream credentialsStream = credentialsLocation.getInputStream();
            GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsStream);
            CredentialsProvider credentialsProvider = FixedCredentialsProvider.create(credentials);

            this.client = TranslationServiceClient.create(TranslationServiceSettings.newBuilder()
                    .setCredentialsProvider(credentialsProvider)
                    .build());
            this.isInitialized = true; // Успешная инициализация
            logger.info("Google Cloud Translation Client initialized successfully for project: {} using credentials from: {}", projectId, credentialsLocation.getDescription());
        } catch (IOException e) {
            logger.error("Failed to initialize Google Cloud Translation Client or load credentials from {}: {}. Translation functionality will be disabled.", credentialsLocation.getDescription(), e.getMessage(), e);
            this.client = null; // Устанавливаем клиент в null при ошибке
            this.isInitialized = false;
            // Здесь мы не бросаем RuntimeException, чтобы сервис мог стартовать,
            // но функция перевода будет отключена.
        }
    }

    /**
     * Переводит один текстовый фрагмент с исходного языка на целевой, используя Google Cloud Translation API.
     *
     * @param text               Текст для перевода.
     * @param sourceLanguageCode Код исходного языка (например, "ru").
     * @param targetLanguageCode Код целевого языка (например, "en").
     * @return Переведенный текст или исходный текст в случае ошибки/неинициализации.
     */
    public String translate(String text, String sourceLanguageCode, String targetLanguageCode) {
        if (!isInitialized || client == null) {
            logger.error("TranslationService is not initialized or client is null. Cannot translate text: '{}' from {} to {}", text, sourceLanguageCode, targetLanguageCode);
            return text; // Возвращаем исходный текст, если клиент не инициализирован
        }
        if (text == null || text.trim().isEmpty()) {
            return text;
        }

        LocationName parent = LocationName.of(projectId, "global");

        TranslateTextRequest request = TranslateTextRequest.newBuilder()
                .setParent(parent.toString())
                .setMimeType("text/plain")
                .setSourceLanguageCode(sourceLanguageCode)
                .setTargetLanguageCode(targetLanguageCode)
                .addContents(text)
                .build();

        try {
            TranslateTextResponse response = client.translateText(request);
            if (!response.getTranslationsList().isEmpty()) {
                String translatedText = response.getTranslationsList().get(0).getTranslatedText();
                logger.debug("Translated '{}' from {} to {}: {}", text, sourceLanguageCode, targetLanguageCode, translatedText);
                return translatedText;
            }
        } catch (Exception e) {
            logger.error("Error translating text '{}' from {} to {}: {}", text, sourceLanguageCode, targetLanguageCode, e.getMessage(), e);
        }
        return text;
    }

    /**
     * Переводит один текстовый фрагмент на несколько целевых языков, используя Google Cloud Translation API.
     *
     * @param text               Текст для перевода.
     * @param sourceLanguageCode Код исходного языка (например, "ru").
     * @param targetLanguageCodes Список кодов целевых языков (например, ["en", "fr", "de"]).
     * @return Карта, где ключ - это код языка, а значение - переведенный текст.
     */
    public Map<String, String> translateBulk(String text, String sourceLanguageCode, List<String> targetLanguageCodes) {
        if (!isInitialized || client == null) {
            logger.error("TranslationService is not initialized or client is null. Cannot translate bulk text: '{}' from {} to {}", text, sourceLanguageCode, targetLanguageCodes);
            return Collections.emptyMap();
        }
        if (text == null || text.trim().isEmpty() || targetLanguageCodes == null || targetLanguageCodes.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, String> translations = new ConcurrentHashMap<>();

        LocationName parent = LocationName.of(projectId, "global");

        for (String targetLang : targetLanguageCodes) {
            TranslateTextRequest request = TranslateTextRequest.newBuilder()
                    .setParent(parent.toString())
                    .setMimeType("text/plain")
                    .setSourceLanguageCode(sourceLanguageCode)
                    .setTargetLanguageCode(targetLang)
                    .addContents(text)
                    .build();
            try {
                TranslateTextResponse response = client.translateText(request);
                if (!response.getTranslationsList().isEmpty()) {
                    translations.put(targetLang, response.getTranslationsList().get(0).getTranslatedText());
                }
            } catch (Exception e) {
                logger.error("Error translating text '{}' from {} to {}: {}", text, sourceLanguageCode, targetLang, e.getMessage(), e);
            }
        }
        return translations;
    }

    @PreDestroy
    public void cleanup() {
        if (client != null) {
            client.close();
            logger.info("Google Cloud Translation Client closed.");
        }
    }
}