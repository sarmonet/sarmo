package com.sarmo.contentservice.service;

import com.sarmo.contentservice.entity.Content;
import com.sarmo.contentservice.repository.ContentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ContentService {

    private static final Logger logger = LoggerFactory.getLogger(ContentService.class);

    private final ContentRepository contentRepository;

    @Autowired
    public ContentService(ContentRepository contentRepository) {
        this.contentRepository = contentRepository;
    }

    public List<Content> getAllContents() {
        try {
            logger.info("Получение всех контентов");
            return contentRepository.findAll();
        } catch (Exception e) {
            logger.error("Ошибка при получении всех контентов: {}", e.getMessage());
            throw e;
        }
    }

    public Optional<Content> getContentById(String id) {
        try {
            logger.info("Получение контента с ID: {}", id);
            return contentRepository.findById(id);
        } catch (Exception e) {
            logger.error("Ошибка при получении контента с ID: {}: {}", id, e.getMessage());
            throw e;
        }
    }

    public Content createContent(Content content) {
        try {
            logger.info("Создание контента: {}", content);
            return contentRepository.save(content);
        } catch (Exception e) {
            logger.error("Ошибка при создании контента: {}", e.getMessage());
            throw e;
        }
    }

    public Content updateContent(String id, Content contentDetails) {
        try {
            logger.info("Обновление контента с ID: {}", id);
            Optional<Content> content = contentRepository.findById(id);
            if (content.isPresent()) {
                Content existingContent = content.get();
                existingContent.setContent(contentDetails.getContent());
                return contentRepository.save(existingContent);
            } else {
                logger.warn("Контент с ID: {} не найден", id);
                return null;
            }
        } catch (Exception e) {
            logger.error("Ошибка при обновлении контента с ID: {}: {}", id, e.getMessage());
            throw e;
        }
    }

    public void deleteContent(String id) {
        try {
            logger.info("Удаление контента с ID: {}", id);
            contentRepository.deleteById(id);
        } catch (Exception e) {
            logger.error("Ошибка при удалении контента с ID: {}: {}", id, e.getMessage());
            throw e;
        }
    }
}