package com.sarmo.listingservice.controller;

import com.sarmo.listingservice.entity.Field;
import com.sarmo.listingservice.entity.InvestmentCategoryField;
import com.sarmo.listingservice.service.InvestmentCategoryFieldService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
// Указываем уникальный базовый путь для инвестиционных полей
@RequestMapping("/api/v1/listing/investment-category/fields")
public class InvestmentCategoryFieldController {

    private final InvestmentCategoryFieldService investmentCategoryFieldService;

    public InvestmentCategoryFieldController(InvestmentCategoryFieldService investmentCategoryFieldService) {
        this.investmentCategoryFieldService = investmentCategoryFieldService;
    }

    /**
     * GET /api/v1/listing/investment-category/fields/{categoryId} - Get fields for an investment listing category by category ID.
     * Retrieves specific fields for the category or falls back to default fields.
     * Accessible to everyone (if URL is permitted in SecurityConfig).
     *
     * @param categoryId The ID of the investment listing category. Use 0L for default fields.
     * @return ResponseEntity containing a list of Field objects.
     */
    @GetMapping("/{categoryId}")
    // @PreAuthorize("permitAll()") // Optional: Add if you want to explicitly mark public access here
    public ResponseEntity<InvestmentCategoryField> getInvestmentListingFields(@PathVariable Long categoryId) {
        // Этот метод сервиса уже содержит логику получения специфических или дефолтных полей
        InvestmentCategoryField investmentCategoryField = investmentCategoryFieldService.getInvestmentListingFields(categoryId);
        return ResponseEntity.ok(investmentCategoryField);
    }

    /**
     * GET /api/v1/listing/investment-category/fields/config/{categoryId} - Get the raw InvestmentCategoryField configuration by ID.
     * Useful for admin operations to see the exact stored config (specific or default).
     * Accessible to everyone (if URL is permitted).
     *
     * @param categoryId The ID of the investment listing category (or 0L for default).
     * @return ResponseEntity containing the InvestmentCategoryField object or 404 Not Found.
     */
    @GetMapping("/config/{categoryId}")
    // @PreAuthorize("permitAll()") // Optional
    public ResponseEntity<InvestmentCategoryField> getInvestmentCategoryFieldConfigById(@PathVariable Long categoryId) {
        Optional<InvestmentCategoryField> config = investmentCategoryFieldService.getInvestmentCategoryField(categoryId);
        return config.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }


    /**
     * POST /api/v1/listing/investment-category/fields - Create or update an investment category field configuration.
     * Can be used to create/update specific category fields or the default fields (if categoryId is 0L in the body).
     * Accessible only to Administrators.
     *
     * @param investmentCategoryField The InvestmentCategoryField object to create or update.
     * @return ResponseEntity containing the created or updated InvestmentCategoryField object.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')") // Requires ADMIN role
    public ResponseEntity<InvestmentCategoryField> createOrUpdateInvestmentCategoryField(@RequestBody InvestmentCategoryField investmentCategoryField) {
        // Сервис сам определяет, создавать или обновлять на основе categoryId в объекте
        InvestmentCategoryField savedConfig = investmentCategoryFieldService.createOrUpdateInvestmentCategoryField(investmentCategoryField);
        return ResponseEntity.ok(savedConfig);
    }

    /**
     * PUT /api/v1/listing/investment-category/fields/{categoryId} - Update fields for a specific investment listing category.
     * Accessible only to Administrators.
     *
     * @param categoryId The ID of the category to update.
     * @param fields The list of Field objects to set for this category.
     * @return ResponseEntity containing the updated InvestmentCategoryField configuration.
     */
    @PutMapping("/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')") // Requires ADMIN role
    public ResponseEntity<InvestmentCategoryField> updateInvestmentCategoryFields(@PathVariable Long categoryId, @RequestBody List<Field> fields) {
        // Этот метод сервиса обновляет поля для конкретного categoryId
        InvestmentCategoryField updatedConfig = investmentCategoryFieldService.createOrUpdateInvestmentCategoryFields(categoryId, fields);
        return ResponseEntity.ok(updatedConfig);
    }

    /**
     * DELETE /api/v1/listing/investment-category/fields/{categoryId} - Delete the field configuration for an investment listing category.
     * Accessible only to Administrators.
     *
     * @param categoryId The ID of the category configuration to delete (or 0L for default).
     * @return ResponseEntity with 204 No Content if successful, or 404 Not Found.
     */
    @DeleteMapping("/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')") // Requires ADMIN role
    public ResponseEntity<Void> deleteInvestmentCategoryField(@PathVariable Long categoryId) {
        boolean deleted = investmentCategoryFieldService.deleteInvestmentCategoryFields(categoryId);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * GET /api/v1/listing/investment-category/fields - Get all investment category field configurations.
     * Accessible to everyone (if URL is permitted).
     *
     * @return ResponseEntity containing a list of all InvestmentCategoryField configurations.
     */
    @GetMapping
    // @PreAuthorize("permitAll()") // Optional: Add if you want to explicitly mark public access here
    public ResponseEntity<List<InvestmentCategoryField>> getAllInvestmentCategoryFields() {
        List<InvestmentCategoryField> configurations = investmentCategoryFieldService.getAllInvestmentCategoryFields();
        return ResponseEntity.ok(configurations);
    }
}