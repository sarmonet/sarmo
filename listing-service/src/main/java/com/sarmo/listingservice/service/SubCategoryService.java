package com.sarmo.listingservice.service;

import com.sarmo.listingservice.entity.Category;
import com.sarmo.listingservice.entity.SubCategory;
import com.sarmo.listingservice.entity.SubCategoryTranslation;
import com.sarmo.listingservice.repository.CategoryRepository;
import com.sarmo.listingservice.repository.SubCategoryRepository;
import com.sarmo.listingservice.repository.SubCategoryTranslationRepository;
import com.sarmo.listingservice.dto.TranslatedSubCategoryDto;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SubCategoryService {

    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final TranslationQueueService translationQueueService;
    private final SubCategoryTranslationRepository subCategoryTranslationRepository;
    private final TranslationService translationService;

    private static final Logger logger = LoggerFactory.getLogger(SubCategoryService.class);

    private static final String SOURCE_LANGUAGE = "ru";
    private static final List<String> CRITICAL_LANGUAGES = List.of("en");
    private static final List<String> ALL_SUPPORTED_LANGUAGES = Arrays.asList("en", "uz");

    public SubCategoryService(CategoryRepository categoryRepository,
                              SubCategoryRepository subCategoryRepository,
                              TranslationQueueService translationQueueService,
                              SubCategoryTranslationRepository subCategoryTranslationRepository,
                              TranslationService translationService) {
        this.categoryRepository = categoryRepository;
        this.subCategoryRepository = subCategoryRepository;
        this.translationQueueService = translationQueueService;
        this.subCategoryTranslationRepository = subCategoryTranslationRepository;
        this.translationService = translationService;
    }

    public List<TranslatedSubCategoryDto> getAllSubCategories(String languageCode) {
        logger.info("Fetching all subcategories translated to: {}", languageCode);
        List<SubCategory> subCategories = subCategoryRepository.findAll();
        return subCategories.stream()
                .map(subCategory -> {
                    String translatedSubCategoryName = subCategoryTranslationRepository.findBySubCategoryIdAndLanguageCode(subCategory.getId(), languageCode)
                            .map(SubCategoryTranslation::getTranslatedName)
                            .orElse(subCategory.getName());

                    String translatedCategoryName = null;
                    if (subCategory.getCategory() != null) {
                        translatedCategoryName = translationService.translate(
                                subCategory.getCategory().getName(), SOURCE_LANGUAGE, languageCode
                        );
                    }

                    return new TranslatedSubCategoryDto(
                            subCategory.getId(),
                            translatedSubCategoryName,
                            subCategory.getCategory() != null ? subCategory.getCategory().getId() : null,
                            translatedCategoryName
                    );
                })
                .collect(Collectors.toList());
    }

    public Optional<TranslatedSubCategoryDto> getSubCategoryById(Long id, String languageCode) {
        logger.info("Fetching subcategory with id: {} translated to: {}", id, languageCode);
        return subCategoryRepository.findById(id)
                .map(subCategory -> {
                    String translatedSubCategoryName = subCategoryTranslationRepository.findBySubCategoryIdAndLanguageCode(subCategory.getId(), languageCode)
                            .map(SubCategoryTranslation::getTranslatedName)
                            .orElse(subCategory.getName());

                    String translatedCategoryName = null;
                    if (subCategory.getCategory() != null) {
                        translatedCategoryName = translationService.translate(
                                subCategory.getCategory().getName(), SOURCE_LANGUAGE, languageCode
                        );
                    }

                    return new TranslatedSubCategoryDto(
                            subCategory.getId(),
                            translatedSubCategoryName,
                            subCategory.getCategory() != null ? subCategory.getCategory().getId() : null,
                            translatedCategoryName
                    );
                });
    }

    public List<TranslatedSubCategoryDto> getSubCategoriesByCategoryId(Long categoryId, String languageCode) {
        logger.info("Fetching subcategories by category id: {} translated to: {}", categoryId, languageCode);
        Optional<Category> categoryOpt = categoryRepository.findById(categoryId);

        if (categoryOpt.isEmpty()) {
            logger.warn("Category with id: {} not found for translated subcategories", categoryId);
            return Collections.emptyList();
        }

        Category category = categoryOpt.get();
        String translatedParentCategoryName = translationService.translate(
                category.getName(), SOURCE_LANGUAGE, languageCode
        );

        List<SubCategory> subCategories = subCategoryRepository.findByCategory(category);

        return subCategories.stream()
                .map(subCategory -> {
                    String translatedSubCategoryName = subCategoryTranslationRepository.findBySubCategoryIdAndLanguageCode(subCategory.getId(), languageCode)
                            .map(SubCategoryTranslation::getTranslatedName)
                            .orElse(subCategory.getName());
                    return new TranslatedSubCategoryDto(
                            subCategory.getId(),
                            translatedSubCategoryName,
                            subCategory.getCategory().getId(),
                            translatedParentCategoryName
                    );
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public SubCategory createSubCategory(SubCategory subCategory) {
        logger.info("Creating subcategory: {}", subCategory.getName());
        SubCategory savedSubCategory = subCategoryRepository.save(subCategory);
        logger.debug("Created subcategory with id: {}", savedSubCategory.getId());

        // Запуск перевода для всех поддерживаемых языков
        ensureTranslationsForSubCategory(savedSubCategory, subCategory.getName());

        return savedSubCategory;
    }

    @Transactional
    public SubCategory updateSubCategory(Long id, SubCategory updatedSubCategory) {
        logger.info("Updating subcategory with id: {}", id);
        Optional<SubCategory> existingSubCategoryOpt = subCategoryRepository.findById(id);

        if (existingSubCategoryOpt.isPresent()) {
            SubCategory existingSubCategory = existingSubCategoryOpt.get();
            String oldName = existingSubCategory.getName();

            existingSubCategory.setName(updatedSubCategory.getName());
            if (updatedSubCategory.getCategory() != null) {
                existingSubCategory.setCategory(updatedSubCategory.getCategory());
            }

            SubCategory savedSubCategory = subCategoryRepository.save(existingSubCategory);
            logger.debug("Updated subcategory with id: {}", savedSubCategory.getId());

            if (!oldName.equals(savedSubCategory.getName())) {
                logger.info("SubCategory name changed from '{}' to '{}'. Updating translations.", oldName, savedSubCategory.getName());

                subCategoryTranslationRepository.deleteBySubCategory(savedSubCategory); // Удаляем старые переводы
                // Запуск перевода для всех поддерживаемых языков
                ensureTranslationsForSubCategory(savedSubCategory, savedSubCategory.getName());
            }
            return savedSubCategory;
        } else {
            logger.warn("Subcategory with id: {} not found for update", id);
            return null;
        }
    }

    @Transactional
    public void deleteSubCategory(Long id) {
        logger.info("Deleting subcategory with id: {}", id);
        Optional<SubCategory> subCategoryOpt = subCategoryRepository.findById(id);

        if (subCategoryOpt.isPresent()) {
            SubCategory subCategory = subCategoryOpt.get();
            subCategoryTranslationRepository.deleteBySubCategory(subCategory);
            logger.debug("Deleted translations for subcategory with id: {}", id);
            subCategoryRepository.delete(subCategory);
            logger.debug("Deleted subcategory with id: {}", id);
        } else {
            logger.warn("Subcategory with id: {} not found for deletion", id);
        }
    }

    @Transactional
    public void ensureTranslationsForSubCategory(SubCategory subCategory, String originalName) {
        logger.info("Ensuring translations exist for subcategory '{}' (ID: {})", originalName, subCategory.getId());

        // Переводим на критические языки синхронно
        for (String langCode : CRITICAL_LANGUAGES) {
            Optional<SubCategoryTranslation> existingTranslation = subCategoryTranslationRepository.findBySubCategoryIdAndLanguageCode(subCategory.getId(), langCode);
            if (existingTranslation.isEmpty()) {
                try {
                    String translatedName = translationService.translate(originalName, SOURCE_LANGUAGE, langCode);
                    SubCategoryTranslation translation = new SubCategoryTranslation(subCategory, langCode, translatedName);
                    subCategoryTranslationRepository.save(translation);
                    logger.info("SubCategory '{}' immediately translated to {}: {}", originalName, langCode, translatedName);
                } catch (Exception e) {
                    logger.error("Error immediately translating subcategory '{}' (ID: {}) to {}: {}", originalName, subCategory.getId(), langCode, e.getMessage());
                }
            } else {
                logger.debug("Translation for subcategory '{}' (ID: {}) to {} already exists.", originalName, subCategory.getId(), langCode);
            }
        }

        // Ставим в очередь на перевод для некритических языков
        List<String> languagesForAsyncTranslation = ALL_SUPPORTED_LANGUAGES.stream()
                .filter(lang -> !CRITICAL_LANGUAGES.contains(lang) && !lang.equals(SOURCE_LANGUAGE)) // Исключаем исходный язык
                .toList();

        for (String langCode : languagesForAsyncTranslation) {
            Optional<SubCategoryTranslation> existingTranslation = subCategoryTranslationRepository.findBySubCategoryIdAndLanguageCode(subCategory.getId(), langCode);
            if (existingTranslation.isEmpty()) {
                translationQueueService.queueSubCategoryTranslation(subCategory.getId());
                logger.info("Queued async translation for subcategory '{}' (ID: {}) to language: {}", originalName, subCategory.getId(), langCode);
            } else {
                logger.debug("Translation for subcategory '{}' (ID: {}) to {} already exists (async check).", originalName, subCategory.getId(), langCode);
            }
        }
    }

    // Метод для получения всех подкатегорий (для StartupInitializer)
    public List<SubCategory> findAllSubCategories() {
        return subCategoryRepository.findAll();
    }
}