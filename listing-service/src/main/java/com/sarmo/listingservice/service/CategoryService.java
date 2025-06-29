package com.sarmo.listingservice.service;

import com.sarmo.listingservice.dto.CategoryWithFieldsDto;
import com.sarmo.listingservice.dto.CreateCategoryRequest;
import com.sarmo.listingservice.dto.TranslatedCategoryDto;
import com.sarmo.listingservice.entity.Category;
import com.sarmo.listingservice.entity.CategoryField;
import com.sarmo.listingservice.entity.CategoryTranslation;
import com.sarmo.listingservice.entity.Field;
import com.sarmo.listingservice.repository.CategoryRepository;
import com.sarmo.listingservice.repository.CategoryTranslationRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);

    private final CategoryRepository categoryRepository;
    private final CategoryFieldService categoryFieldService;
    private final MongoTemplate mongoTemplate;
    private final TranslationQueueService translationQueueService;
    private final CategoryTranslationRepository categoryTranslationRepository;
    private final TranslationService translationService;

    private static final String SOURCE_LANGUAGE = "ru";
    // Определяем языки, которые будут переводиться СИНХРОННО (сразу при создании/обновлении категории)
    private static final List<String> SYNCHRONOUS_TRANSLATION_LANGUAGES = List.of("en");
    // ALL_SUPPORTED_LANGUAGES теперь управляется TranslationQueueService через @Value,
    // поэтому этот список здесь не нужен или используется только для отображения/внутренних целей.
    // Если вы хотите, чтобы CategoryService знал все языки, то можете оставить, но он не будет
    // использоваться для выбора языков для асинхронной очереди.

    public CategoryService(CategoryRepository categoryRepository,
                           CategoryFieldService categoryFieldService,
                           MongoTemplate mongoTemplate,
                           TranslationQueueService translationQueueService,
                           CategoryTranslationRepository categoryTranslationRepository,
                           TranslationService translationService) {
        this.categoryRepository = categoryRepository;
        this.categoryFieldService = categoryFieldService;
        this.mongoTemplate = mongoTemplate;
        this.translationQueueService = translationQueueService;
        this.categoryTranslationRepository = categoryTranslationRepository;
        this.translationService = translationService;
    }

    public List<TranslatedCategoryDto> getAllCategories(String languageCode) {
        logger.info("Fetching all categories translated to: {}", languageCode);
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(category -> {
                    // Используем findByCategoryAndLanguageCode - более JPA-идиоматично
                    String translatedName = categoryTranslationRepository.findByCategoryAndLanguageCode(category, languageCode)
                            .map(CategoryTranslation::getTranslatedName)
                            .orElse(category.getName());
                    return new TranslatedCategoryDto(category.getId(), translatedName, category.getImageUrl());
                })
                .collect(Collectors.toList());
    }

    public Optional<TranslatedCategoryDto> getCategoryById(Long id, String languageCode) {
        logger.info("Fetching category with id: {} translated to: {}", id, languageCode);
        return categoryRepository.findById(id)
                .map(category -> {
                    // Используем findByCategoryAndLanguageCode - более JPA-идиоматично
                    String translatedName = categoryTranslationRepository.findByCategoryAndLanguageCode(category, languageCode)
                            .map(CategoryTranslation::getTranslatedName)
                            .orElse(category.getName());
                    return new TranslatedCategoryDto(category.getId(), translatedName, category.getImageUrl());
                });
    }

    public List<CategoryWithFieldsDto> getAllCategoriesWithFields() {
        logger.info("Fetching all categories with fields");
        List<Category> categories = categoryRepository.findAll();
        logger.debug("Fetched {} categories", categories.size());

        return categories.stream()
                .map(category -> {
                    CategoryField categoryField = mongoTemplate.findOne(
                            Query.query(Criteria.where("categoryId").is(category.getId())),
                            CategoryField.class
                    );

                    // Убедитесь, что конструктор CategoryWithFieldsDto принимает List<Field>
                    if (categoryField != null && categoryField.getFields() != null) {
                        return new CategoryWithFieldsDto(category, categoryField.getFields());
                    } else {
                        return new CategoryWithFieldsDto(category, Collections.emptyList());
                    }
                })
                .collect(Collectors.toList());
    }

    public List<CategoryWithFieldsDto> getAllCategoriesWithFields(Boolean filterable) {
        logger.info("Fetching all categories with fields, filterable: {}", filterable);
        List<Category> categories = categoryRepository.findAll();
        logger.debug("Fetched {} categories", categories.size());

        return categories.stream()
                .map(category -> {
                    CategoryField categoryField = mongoTemplate.findOne(
                            Query.query(Criteria.where("categoryId").is(category.getId())),
                            CategoryField.class
                    );

                    if (categoryField != null && categoryField.getFields() != null) {
                        List<Field> filteredFields = categoryField.getFields().stream()
                                .filter(field -> field.getFilterable() == filterable)
                                .collect(Collectors.toList());

                        return new CategoryWithFieldsDto(category, filteredFields);
                    } else {
                        return new CategoryWithFieldsDto(category, Collections.emptyList());
                    }
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public Category createCategory(Category category) {
        logger.info("Creating category with name: {}", category.getName());
        Category savedCategory = categoryRepository.save(category);
        logger.debug("Created category with id: {}", savedCategory.getId());

        // Запуск перевода
        ensureTranslationsForCategory(savedCategory);

        return savedCategory;
    }

    @Transactional
    public Category createCategoryWithFields(CreateCategoryRequest request) {
        logger.info("Creating category with name: {}", request.getName());
        // Убедитесь, что конструктор Category принимает imageUrl или установите его
        Category newCategory = new Category();
        newCategory.setName(request.getName());
        Category createdCategory = categoryRepository.save(newCategory);
        logger.debug("Created category with id: {}", createdCategory.getId());

        categoryFieldService.createOrUpdateCategoryFields(createdCategory.getId(), request.getFields());
        logger.info("Category fields updated for category id: {}", createdCategory.getId());

        // Запуск перевода
        ensureTranslationsForCategory(createdCategory);

        return createdCategory;
    }

    @Transactional
    public Category updateCategory(Long id, Category categoryDetails) {
        logger.info("Updating category with id: {}", id);
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id " + id));
        String oldName = existingCategory.getName();

        existingCategory.setName(categoryDetails.getName());
        existingCategory.setImageUrl(categoryDetails.getImageUrl()); // Обновляем также imageUrl

        Category updatedCategory = categoryRepository.save(existingCategory);
        logger.debug("Updated category with id: {}", updatedCategory.getId());

        if (!oldName.equals(updatedCategory.getName())) {
            logger.info("Category name changed from '{}' to '{}'. Triggering translation update.", oldName, updatedCategory.getName());
            // Если имя изменилось, запускаем весь процесс перевода заново.
            // TranslationQueueService.saveOrUpdateCategoryTranslation позаботится об обновлении/сохранении.
            // Удаление всех старых переводов здесь может быть избыточным, но если вы хотите
            // гарантировать пересоздание всех записей, можете его оставить:
            // categoryTranslationRepository.deleteByCategory(updatedCategory);
            ensureTranslationsForCategory(updatedCategory); // Перезапускаем процесс перевода
        }
        return updatedCategory;
    }

    @Transactional
    public void deleteCategory(Long id) {
        logger.info("Deleting category with id: {}", id);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id " + id));

        // Удаляем переводы категории
        categoryTranslationRepository.deleteByCategory(category);
        logger.debug("Deleted translations for category with id: {}", id);

        // Удаляем связанные CategoryField из MongoDB
        Query query = Query.query(Criteria.where("categoryId").is(id));
        mongoTemplate.remove(query, CategoryField.class);
        logger.debug("Deleted category fields for category with id: {}", id);

        // Удаляем саму категорию
        categoryRepository.delete(category);
        logger.debug("Deleted category with id: {}", id);
    }

    @Transactional
    public Optional<Category> updateCategoryImage(Long categoryId, String newImageUrl) {
        logger.info("Updating image URL for category ID: {}", categoryId);
        return categoryRepository.findById(categoryId)
                .map(category -> {
                    logger.debug("Found category with ID: {}. Updating image URL to: {}", categoryId, newImageUrl);
                    category.setImageUrl(newImageUrl);
                    try {
                        Category savedCategory = categoryRepository.save(category);
                        logger.info("Successfully updated image URL for category ID: {}", categoryId);
                        return Optional.of(savedCategory);
                    } catch (Exception e) {
                        logger.error("Error saving updated image URL for category ID: {}: {}", categoryId, e.getMessage(), e);
                        throw new RuntimeException("Failed to update category image URL", e);
                    }
                })
                .orElseGet(() -> {
                    logger.warn("Category with ID {} not found for image URL update.", categoryId);
                    return Optional.empty();
                });
    }

    /**
     * Обеспечивает наличие переводов для категории на все поддерживаемые языки.
     * Сначала пытается перевести на SYNCHRONOUS_TRANSLATION_LANGUAGES синхронно, остальные - асинхронно через очередь.
     *
     * @param category Категория, для которой нужно обеспечить переводы.
     */
    @Transactional
    public void ensureTranslationsForCategory(Category category) {
        String originalName = category.getName();
        Long categoryId = category.getId();
        logger.info("Ensuring translations exist for category '{}' (ID: {})", originalName, categoryId);

        boolean synchronousTranslationFailed = false;

        // 1. Синхронный перевод для SYNCHRONOUS_TRANSLATION_LANGUAGES
        for (String langCode : SYNCHRONOUS_TRANSLATION_LANGUAGES) {
            try {
                // Используем findByCategoryAndLanguageCode
                Optional<CategoryTranslation> existingTranslation = categoryTranslationRepository.findByCategoryAndLanguageCode(category, langCode);
                String translatedName = translationService.translate(originalName, SOURCE_LANGUAGE, langCode);

                if (existingTranslation.isPresent()) {
                    CategoryTranslation translation = existingTranslation.get();
                    if (!translation.getTranslatedName().equals(translatedName)) {
                        translation.setTranslatedName(translatedName);
                        categoryTranslationRepository.save(translation);
                        logger.info("Updated immediate translation for category '{}' (ID: {}) to {}: '{}'", originalName, categoryId, langCode, translatedName);
                    } else {
                        logger.debug("Immediate translation for category '{}' (ID: {}) to {} is already up to date.", originalName, categoryId, langCode);
                    }
                } else {
                    CategoryTranslation translation = new CategoryTranslation(category, langCode, translatedName);
                    categoryTranslationRepository.save(translation);
                    logger.info("Saved immediate translation for category '{}' (ID: {}) to {}: '{}'", originalName, categoryId, langCode, translatedName);
                }
            } catch (Exception e) {
                logger.error("Error immediately translating category '{}' (ID: {}) to {}: {}. Will queue for async translation.", originalName, categoryId, langCode, e.getMessage(), e);
                synchronousTranslationFailed = true; // Отмечаем, что синхронный перевод не удался
            }
        }

        translationQueueService.queueCategoryTranslation(categoryId);
        if (synchronousTranslationFailed) {
            logger.info("Asynchronous translation initiated for category '{}' (ID: {}) due to previous synchronous translation failure.", originalName, categoryId);
        } else {
            logger.info("Asynchronous translation initiated for category '{}' (ID: {}) for all remaining supported languages.", originalName, categoryId);
        }
    }


    // Метод для получения всех категорий (для StartupInitializer)
    public List<Category> findAllCategories() {
        return categoryRepository.findAll();
    }
}