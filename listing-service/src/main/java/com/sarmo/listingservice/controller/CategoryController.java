package com.sarmo.listingservice.controller;

import com.sarmo.listingservice.dto.CategoryWithFieldsDto;
import com.sarmo.listingservice.dto.CreateCategoryRequest;
import com.sarmo.listingservice.dto.TranslatedCategoryDto; // Импортируем новый DTO
import com.sarmo.listingservice.entity.Category;
import com.sarmo.listingservice.service.CategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/v1/listing/category")
public class CategoryController {

    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // ИЗМЕНЕН: Теперь это основной метод для получения всех категорий, всегда возвращает TranslatedCategoryDto
    @GetMapping
    public ResponseEntity<List<TranslatedCategoryDto>> getAllCategories(
            @RequestParam(name = "lang", defaultValue = "ru") String languageCode) { // Параметр lang обязателен
        logger.info("GET /api/v1/listing/category - Received request to fetch all categories translated to: {}", languageCode);
        List<TranslatedCategoryDto> categories = categoryService.getAllCategories(languageCode);
        logger.info("GET /api/v1/listing/category - Found {} translated categories for language: {}", categories.size(), languageCode);
        return ResponseEntity.ok(categories);
    }

    // ИЗМЕНЕН: Теперь это основной метод для получения категории по ID, всегда возвращает TranslatedCategoryDto
    @GetMapping("/{id}")
    public ResponseEntity<TranslatedCategoryDto> getCategoryById(
            @PathVariable Long id,
            @RequestParam(name = "lang", defaultValue = "ru") String languageCode) { // Параметр lang обязателен
        logger.info("GET /api/v1/listing/category/{} - Received request to fetch category with id: {} translated to: {}", id, languageCode);
        Optional<TranslatedCategoryDto> category = categoryService.getCategoryById(id, languageCode);
        return category.map(ResponseEntity::ok)
                .orElseGet(() -> {
                    logger.warn("GET /api/v1/listing/category/{} - Translated category not found for id: {} and lang: {}", id, id, languageCode);
                    return ResponseEntity.notFound().build();
                });
    }

    // Методы с полями (CategoryWithFieldsDto) пока остаются без изменений,
    // так как они возвращают DTO, включающий поля, а не только имя категории.
    // Если вам нужно, чтобы поля тоже были переведены, потребуется более сложная логика.
    @GetMapping("/with-fields")
    public ResponseEntity<List<CategoryWithFieldsDto>> getAllCategoriesWithFields() {
        logger.info("GET /api/v1/listing/category/with-fields - Received request to fetch all categories with fields");
        List<CategoryWithFieldsDto> categories = categoryService.getAllCategoriesWithFields();
        logger.info("GET /api/v1/listing/category/with-fields - Found {} categories with fields", categories.size());
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/with-fields-filtered")
    public ResponseEntity<List<CategoryWithFieldsDto>> getAllCategoriesWithFields(@RequestParam(required = false) Boolean filterable) {
        logger.info("GET /api/v1/listing/category/with-fields-filtered - Received request to fetch all categories with fields (filterable: {})", filterable);
        List<CategoryWithFieldsDto> categories = categoryService.getAllCategoriesWithFields(filterable);
        logger.info("GET /api/v1/listing/category/with-fields-filtered - Found {} filtered categories with fields", categories.size());
        return ResponseEntity.ok(categories);
    }

    // Методы POST, PUT, DELETE остаются без изменений, так как они работают с сущностями или DTO для создания/обновления.
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        logger.info("POST /api/v1/listing/category - Received request to create category with name: {}", category.getName());
        Category createdCategory = categoryService.createCategory(category);
        logger.info("POST /api/v1/listing/category - Category created with id: {}", createdCategory.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);
    }

    @PostMapping("/with-fields")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Category> createCategoryWithFields(@RequestBody CreateCategoryRequest request) {
        logger.info("POST /api/v1/listing/category/with-fields - Received request to create category with name: {}", request.getName());
        Category createdCategory = categoryService.createCategoryWithFields(request);
        logger.info("POST /api/v1/listing/category/with-fields - Category created with id: {}", createdCategory.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @RequestBody Category categoryDetails) {
        logger.info("PUT /api/v1/listing/category/{} - Received request to update category with id", id);
        try {
            Category updatedCategory = categoryService.updateCategory(id, categoryDetails);
            logger.info("PUT /api/v1/listing/category/{} - Category updated", id);
            return ResponseEntity.ok(updatedCategory);
        } catch (RuntimeException e) {
            logger.error("PUT /api/v1/listing/category/{} - Category not found or error updating", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        logger.info("DELETE /api/v1/listing/category/{} - Received request to delete category with id", id);
        try {
            categoryService.deleteCategory(id);
            logger.info("DELETE /api/v1/listing/category/{} - Category deleted", id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            logger.error("DELETE /api/v1/listing/category/{} - Category not found or error deleting", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PatchMapping("/{id}/image")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Category> updateCategoryImage(@PathVariable Long id, @RequestBody String imageUrl) {
        logger.info("PATCH /api/v1/listing/category/{}/image - Received request to update image for category id", id);
        try {
            Optional<Category> updatedCategoryOptional = categoryService.updateCategoryImage(id, imageUrl);

            return updatedCategoryOptional
                    .map(updatedCategory -> {
                        logger.info("PATCH /api/v1/listing/category/{}/image - Category image updated", id);
                        return new ResponseEntity<>(updatedCategory, HttpStatus.OK);
                    })
                    .orElseGet(() -> {
                        logger.warn("PATCH /api/v1/listing/category/{}/image - Category not found for image update", id);
                        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                    });

        } catch (RuntimeException e) {
            logger.error("PATCH /api/v1/listing/category/{}/image - Error updating category image", id, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}