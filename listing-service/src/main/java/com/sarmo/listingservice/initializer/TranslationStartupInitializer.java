package com.sarmo.listingservice.initializer; // Создайте новый пакет для инициализаторов

import com.sarmo.listingservice.entity.Category;
import com.sarmo.listingservice.entity.SubCategory;
import com.sarmo.listingservice.service.CategoryService;
import com.sarmo.listingservice.service.SubCategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional; // Важно для корректной работы ensureTranslationsFor*

import java.util.List;

@Component
public class TranslationStartupInitializer {

    private static final Logger logger = LoggerFactory.getLogger(TranslationStartupInitializer.class);

    private final CategoryService categoryService;
    private final SubCategoryService subCategoryService;

    public TranslationStartupInitializer(CategoryService categoryService, SubCategoryService subCategoryService) {
        this.categoryService = categoryService;
        this.subCategoryService = subCategoryService;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void onApplicationReady() {
        logger.info("Starting translation check and generation at application startup...");

        // Обработка категорий
        List<Category> categories = categoryService.findAllCategories();
        logger.info("Found {} categories for translation check.", categories.size());
        for (Category category : categories) {
            try {
                // Вызываем метод сервиса, который обеспечит переводы для этой категории
                categoryService.ensureTranslationsForCategory(category);
            } catch (Exception e) {
                logger.error("Error during startup translation for category ID {}: {}", category.getId(), e.getMessage(), e);
            }
        }
        logger.info("Finished category translation check.");

        // Обработка подкатегорий
        List<SubCategory> subCategories = subCategoryService.findAllSubCategories();
        logger.info("Found {} subcategories for translation check.", subCategories.size());
        for (SubCategory subCategory : subCategories) {
            try {
                // Вызываем метод сервиса, который обеспечит переводы для этой подкатегории
                subCategoryService.ensureTranslationsForSubCategory(subCategory, subCategory.getName());
            } catch (Exception e) {
                logger.error("Error during startup translation for subcategory ID {}: {}", subCategory.getId(), e.getMessage(), e);
            }
        }
        logger.info("Finished subcategory translation check.");
        logger.info("Translation startup initializer completed.");
    }
}