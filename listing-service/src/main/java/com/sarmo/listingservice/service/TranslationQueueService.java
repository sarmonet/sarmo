package com.sarmo.listingservice.service;

import com.sarmo.listingservice.entity.Category;
import com.sarmo.listingservice.entity.CategoryTranslation;
import com.sarmo.listingservice.entity.SubCategory;
import com.sarmo.listingservice.entity.SubCategoryTranslation;
import com.sarmo.listingservice.repository.CategoryRepository;
import com.sarmo.listingservice.repository.CategoryTranslationRepository;
import com.sarmo.listingservice.repository.SubCategoryRepository;
import com.sarmo.listingservice.repository.SubCategoryTranslationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value; // Для получения языков из конфига
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Для транзакций

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class TranslationQueueService {

    private static final Logger logger = LoggerFactory.getLogger(TranslationQueueService.class);

    private final TranslationService translationService;
    private final CategoryRepository categoryRepository;
    private final CategoryTranslationRepository categoryTranslationRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final SubCategoryTranslationRepository subCategoryTranslationRepository;

    // Языки можно получить из конфига, чтобы не дублировать их
    @Value("${application.translation.source-language:ru}") // Дефолтное значение "ru"
    private String sourceLanguage;

    @Value("${application.translation.target-languages:en,uz}") // Дефолтные значения "en,uz"
    private List<String> targetLanguages;

    public TranslationQueueService(TranslationService translationService, CategoryRepository categoryRepository, CategoryTranslationRepository categoryTranslationRepository, SubCategoryRepository subCategoryRepository, SubCategoryTranslationRepository subCategoryTranslationRepository) {
        this.translationService = translationService;
        this.categoryRepository = categoryRepository;
        this.categoryTranslationRepository = categoryTranslationRepository;
        this.subCategoryRepository = subCategoryRepository;
        this.subCategoryTranslationRepository = subCategoryTranslationRepository;
    }


    /**
     * Ставит в очередь асинхронный перевод категории.
     * Запускается только для языков, которые не были переведены синхронно, или для обновления.
     *
     * @param categoryId ID категории для перевода.
     */
    @Async // Аннотация @Async требует @EnableAsync в основном классе приложения
    @Transactional // Оборачиваем метод в транзакцию
    public void queueCategoryTranslation(Long categoryId) {
        logger.info("Queuing async translation for category ID: {}", categoryId);
        categoryRepository.findById(categoryId).ifPresentOrElse(category -> {
            String originalName = category.getName();

            // Получаем только те языки, которые еще не переведены, или для которых нужно обновить перевод
            // Это общая логика, но в реальном сценарии вы можете передавать specificTargetLanguages
            // из CategoryService, если там уже был синхронный перевод.

            // Здесь мы используем translateBulk для всех целевых языков,
            // а логика saveOrUpdateTranslation позаботится о сохранении/обновлении.
            Map<String, String> translations = translationService.translateBulk(originalName, sourceLanguage, targetLanguages);

            translations.forEach((langCode, translatedName) -> {
                saveOrUpdateCategoryTranslation(category, langCode, translatedName);
            });
            logger.info("Finished async translation processing for category ID: {}", categoryId);
        }, () -> {
            logger.warn("Category with ID {} not found for async translation.", categoryId);
        });
    }

    /**
     * Вспомогательный метод для сохранения или обновления перевода категории.
     */
    private void saveOrUpdateCategoryTranslation(Category category, String languageCode, String translatedName) {
        Optional<CategoryTranslation> existingTranslation = categoryTranslationRepository.findByCategoryAndLanguageCode(category, languageCode);
        if (existingTranslation.isPresent()) {
            CategoryTranslation translation = existingTranslation.get();
            if (!translation.getTranslatedName().equals(translatedName)) { // Обновляем только если перевод изменился
                translation.setTranslatedName(translatedName);
                categoryTranslationRepository.save(translation);
                logger.info("Updated async translation for Category '{}' (ID: {}) to {}: '{}'", category.getName(), category.getId(), languageCode, translatedName);
            } else {
                logger.debug("Translation for Category '{}' (ID: {}) to {} is already up to date.", category.getName(), category.getId(), languageCode);
            }
        } else {
            CategoryTranslation newTranslation = new CategoryTranslation(category, languageCode, translatedName);
            categoryTranslationRepository.save(newTranslation);
            logger.info("Saved new async translation for Category '{}' (ID: {}) to {}: '{}'", category.getName(), category.getId(), languageCode, translatedName);
        }
    }


    /**
     * Ставит в очередь асинхронный перевод подкатегории.
     *
     * @param subCategoryId ID подкатегории для перевода.
     */
    @Async
    @Transactional
    public void queueSubCategoryTranslation(Long subCategoryId) {
        logger.info("Queuing async translation for sub-category ID: {}", subCategoryId);
        subCategoryRepository.findById(subCategoryId).ifPresentOrElse(subCategory -> {
            String originalName = subCategory.getName();
            Map<String, String> translations = translationService.translateBulk(originalName, sourceLanguage, targetLanguages);

            translations.forEach((langCode, translatedName) -> {
                saveOrUpdateSubCategoryTranslation(subCategory, langCode, translatedName);
            });
            logger.info("Finished async translation processing for sub-category ID: {}", subCategoryId);
        }, () -> {
            logger.warn("SubCategory with ID {} not found for async translation.", subCategoryId);
        });
    }

    /**
     * Вспомогательный метод для сохранения или обновления перевода подкатегории.
     */
    private void saveOrUpdateSubCategoryTranslation(SubCategory subCategory, String languageCode, String translatedName) {
        Optional<SubCategoryTranslation> existingTranslation = subCategoryTranslationRepository.findBySubCategoryAndLanguageCode(subCategory, languageCode);
        if (existingTranslation.isPresent()) {
            SubCategoryTranslation translation = existingTranslation.get();
            if (!translation.getTranslatedName().equals(translatedName)) {
                translation.setTranslatedName(translatedName);
                subCategoryTranslationRepository.save(translation);
                logger.info("Updated async translation for SubCategory '{}' (ID: {}) to {}: '{}'", subCategory.getName(), subCategory.getId(), languageCode, translatedName);
            } else {
                logger.debug("Translation for SubCategory '{}' (ID: {}) to {} is already up to date.", subCategory.getName(), subCategory.getId(), languageCode);
            }
        } else {
            SubCategoryTranslation newTranslation = new SubCategoryTranslation(subCategory, languageCode, translatedName);
            subCategoryTranslationRepository.save(newTranslation);
            logger.info("Saved new async translation for SubCategory '{}' (ID: {}) to {}: '{}'", subCategory.getName(), subCategory.getId(), languageCode, translatedName);
        }
    }
}