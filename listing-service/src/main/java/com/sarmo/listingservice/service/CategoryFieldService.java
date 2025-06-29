package com.sarmo.listingservice.service;

import com.sarmo.listingservice.entity.CategoryField;
import com.sarmo.listingservice.entity.Field;
import com.sarmo.listingservice.repository.CategoryFieldRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryFieldService {

    private static final Logger logger = LoggerFactory.getLogger(CategoryFieldService.class);
    private final MongoTemplate mongoTemplate;
    private final CategoryFieldRepository categoryFieldRepository;

    public CategoryFieldService(MongoTemplate mongoTemplate, CategoryFieldRepository categoryFieldRepository) {
        this.mongoTemplate = mongoTemplate;
        this.categoryFieldRepository = categoryFieldRepository;
    }

    // Получить специфичные поля для категории по ее ID
    public Optional<CategoryField> getCategoryFields(Long categoryId) {
        logger.info("Fetching fields for category with id: {}", categoryId);
        CategoryField categoryField = categoryFieldRepository.findByCategoryId(categoryId);
        return Optional.ofNullable(categoryField);
    }

    // Создать или обновить поля для категории
    public CategoryField createOrUpdateCategoryFields(Long categoryId, List<Field> fields) {
        logger.info("Creating or updating fields for category with id: {}", categoryId);

        CategoryField categoryField = categoryFieldRepository.findByCategoryId(categoryId);

        if (categoryField != null) {
            logger.debug("Updating existing fields for category with id: {}", categoryId);
            categoryField.setFields(fields);
        } else {
            logger.debug("Creating new fields for category with id: {}", categoryId);
            categoryField = new CategoryField();
            categoryField.setCategoryId(categoryId);
            categoryField.setFields(fields);
        }

        CategoryField savedCategoryField = categoryFieldRepository.save(categoryField);
        logger.info("Saved fields for category with id: {}", categoryId);
        return savedCategoryField;
    }

    // Удалить поля для категории
    public boolean deleteCategoryFields(Long categoryId) {
        logger.info("Deleting fields for category with id: {}", categoryId);

        Query query = new Query(Criteria.where("categoryId").is(categoryId));
        long deletedCount = mongoTemplate.remove(query, CategoryField.class).getDeletedCount();

        if (deletedCount > 0) {
            logger.info("Deleted fields for category with id: {}", categoryId);
            return true;
        } else {
            logger.warn("Fields not found for category with id: {}", categoryId);
            return false;
        }
    }

    // Создать или обновить CategoryField
    public CategoryField createOrUpdateCategoryField(CategoryField categoryField) {
        logger.info("Creating or updating category field with id: {}", categoryField.getCategoryId());
        return categoryFieldRepository.save(categoryField);
    }

    // Получить все CategoryField
    public List<CategoryField> getAllCategoryFields() {
        logger.info("Fetching all category fields");
        return categoryFieldRepository.findAll();
    }
}