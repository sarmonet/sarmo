package com.sarmo.listingservice.controller;

import com.sarmo.listingservice.entity.SubCategory;
import com.sarmo.listingservice.service.SubCategoryService;
import com.sarmo.listingservice.dto.TranslatedSubCategoryDto; // Импортируем новый DTO
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/v1/listing/subcategory")
public class SubCategoryController {

    private final SubCategoryService subCategoryService;
    private static final Logger logger = LoggerFactory.getLogger(SubCategoryController.class);

    public SubCategoryController(SubCategoryService subCategoryService) {
        this.subCategoryService = subCategoryService;
    }

    @GetMapping
    public ResponseEntity<List<TranslatedSubCategoryDto>> getAllSubCategories(
            @RequestParam(name = "lang", defaultValue = "ru") String languageCode) {
        logger.info("GET /api/v1/listing/subcategory - Received request to fetch all subcategories translated to: {}", languageCode);
        List<TranslatedSubCategoryDto> subCategories = subCategoryService.getAllSubCategories(languageCode);
        logger.info("GET /api/v1/listing/subcategory - Found {} translated subcategories for language: {}", subCategories.size(), languageCode);
        return ResponseEntity.ok(subCategories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TranslatedSubCategoryDto> getSubCategoryById(
            @PathVariable Long id,
            @RequestParam(name = "lang", defaultValue = "ru") String languageCode) {
        logger.info("GET /api/v1/listing/subcategory/{} - Received request to fetch subcategory with id: {} translated to: {}", id, languageCode);
        Optional<TranslatedSubCategoryDto> subCategory = subCategoryService.getSubCategoryById(id, languageCode);
        return subCategory.map(ResponseEntity::ok)
                .orElseGet(() -> {
                    logger.warn("GET /api/v1/listing/subcategory/{} - Translated subcategory not found for id: {} and lang: {}", id, id, languageCode);
                    return ResponseEntity.notFound().build();
                });
    }

    @GetMapping("/by-category/{categoryId}")
    public ResponseEntity<List<TranslatedSubCategoryDto>> getSubCategoriesByCategoryId(
            @PathVariable Long categoryId,
            @RequestParam(name = "lang", defaultValue = "ru") String languageCode) {
        logger.info("GET /api/v1/listing/subcategory/by-category/{} - Received request to fetch subcategories by category id: {} translated to: {}", categoryId, categoryId, languageCode);
        List<TranslatedSubCategoryDto> subCategories = subCategoryService.getSubCategoriesByCategoryId(categoryId, languageCode);
        logger.info("GET /api/v1/listing/subcategory/by-category/{} - Found {} translated subcategories for category id: {} and language: {}", categoryId, subCategories.size(), languageCode);
        return ResponseEntity.ok(subCategories);
    }

    // Методы POST, PUT, DELETE остаются без изменений
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SubCategory> createSubCategory(@RequestBody SubCategory subCategory) {
        logger.info("POST /api/v1/listing/subcategory - Received request to create subcategory with name: {}", subCategory.getName());
        SubCategory createdSubCategory = subCategoryService.createSubCategory(subCategory);
        logger.info("POST /api/v1/listing/subcategory - Subcategory created with id: {}", createdSubCategory.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSubCategory);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SubCategory> updateSubCategory(@PathVariable Long id, @RequestBody SubCategory updatedSubCategory) {
        logger.info("PUT /api/v1/listing/subcategory/{} - Received request to update subcategory with id", id);
        SubCategory subCategory = subCategoryService.updateSubCategory(id, updatedSubCategory);
        if (subCategory != null) {
            logger.info("PUT /api/v1/listing/subcategory/{} - Subcategory updated", id);
            return ResponseEntity.ok(subCategory);
        } else {
            logger.warn("PUT /api/v1/listing/subcategory/{} - Subcategory not found for update", id);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSubCategory(@PathVariable Long id) {
        logger.info("DELETE /api/v1/listing/subcategory/{} - Received request to delete subcategory with id", id);
        subCategoryService.deleteSubCategory(id);
        logger.info("DELETE /api/v1/listing/subcategory/{} - Subcategory deleted", id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralExceptions(Exception ex) {
        logger.error("An unexpected error occurred: {}", ex.getMessage(), ex);
        return ResponseEntity.internalServerError().body("An unexpected error occurred.");
    }
}