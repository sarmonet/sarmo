package com.sarmo.listingservice.controller;

import com.sarmo.listingservice.entity.CategoryField;
import com.sarmo.listingservice.service.CategoryFieldService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import org.springframework.security.access.prepost.PreAuthorize; // Import for security annotations

@RestController
@RequestMapping("/api/v1/listing/category/fields")
public class CategoryFieldController {

    private final CategoryFieldService categoryFieldService;

    public CategoryFieldController(CategoryFieldService categoryFieldService) {
        this.categoryFieldService = categoryFieldService;
    }

    // GET /api/v1/listing/category/fields/{categoryId} - Get category fields by category ID
    // Accessible to everyone (if URL /api/v1/listing/** or specific path is permitted in SecurityConfig)
    @GetMapping("/{categoryId}")
    // @PreAuthorize("permitAll()") // Optional: Add if you want to explicitly mark public access here
    public ResponseEntity<CategoryField> getCategoryFieldById(@PathVariable Long categoryId) {
        // Logging can be added here if needed, similar to other controllers
        Optional<CategoryField> categoryField = categoryFieldService.getCategoryFields(categoryId);
        return ResponseEntity.ok(categoryField.orElseGet(CategoryField::new));
    }

    // POST /api/v1/listing/category/fields - Create category fields
    // Accessible only to Administrators
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')") // Requires ADMIN role
    public ResponseEntity<CategoryField> createCategoryField(@RequestBody CategoryField categoryField) {
        // Logging can be added here
        CategoryField createdCategoryField = categoryFieldService.createOrUpdateCategoryField(categoryField);
        return ResponseEntity.ok(createdCategoryField);
    }

    // PUT /api/v1/listing/category/fields/{categoryId} - Update category fields
    // Accessible only to Administrators
    @PutMapping("/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')") // Requires ADMIN role
    public ResponseEntity<CategoryField> updateCategoryField(@PathVariable Long categoryId, @RequestBody List<com.sarmo.listingservice.entity.Field> fields) {
        // Logging can be added here
        CategoryField updatedCategoryField = categoryFieldService.createOrUpdateCategoryFields(categoryId, fields);
        return ResponseEntity.ok(updatedCategoryField);
    }

    // DELETE /api/v1/listing/category/fields/{categoryId} - Delete category fields
    // Accessible only to Administrators
    @DeleteMapping("/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')") // Requires ADMIN role
    public ResponseEntity<Void> deleteCategoryField(@PathVariable Long categoryId) {
        // Logging can be added here
        boolean deleted = categoryFieldService.deleteCategoryFields(categoryId);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            // Consider logging a warning here if not found
            return ResponseEntity.notFound().build();
        }
    }

    // GET /api/v1/listing/category/fields - Get all category fields
    // Accessible to everyone (if URL is permitted)
    @GetMapping
    // @PreAuthorize("permitAll()") // Optional: Add if you want to explicitly mark public access here
    public ResponseEntity<List<CategoryField>> getAllCategoryFields() {
        // Logging can be added here
        List<CategoryField> categoryFields = categoryFieldService.getAllCategoryFields();
        return ResponseEntity.ok(categoryFields);
    }
}