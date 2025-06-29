package com.sarmo.listingservice.service;

import com.sarmo.listingservice.entity.Field;
import com.sarmo.listingservice.entity.InvestmentCategoryField;
import com.sarmo.listingservice.repository.InvestmentCategoryFieldRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class InvestmentCategoryFieldService {

    private static final Logger logger = LoggerFactory.getLogger(InvestmentCategoryFieldService.class);
    private static final Long DEFAULT_CATEGORY_ID = 0L; // ID for default fields

    private final MongoTemplate mongoTemplate;
    private final InvestmentCategoryFieldRepository investmentCategoryFieldRepository;

    public InvestmentCategoryFieldService(MongoTemplate mongoTemplate, InvestmentCategoryFieldRepository investmentCategoryFieldRepository) {
        this.mongoTemplate = mongoTemplate;
        this.investmentCategoryFieldRepository = investmentCategoryFieldRepository;
    }

    /**
     * Retrieves fields for an investment listing based on its category.
     * First searches for specific fields for the category; if not found, returns default fields.
     *
     * @param categoryId The ID of the investment listing category.
     * @return A list of fields for the given category, or default fields, or an empty list if nothing is found.
     */
    public InvestmentCategoryField getInvestmentListingFields(Long categoryId) {
        logger.info("Fetching fields for investment listing category with id: {}", categoryId);

        // 1. First, search for specific fields for this category
        Optional<InvestmentCategoryField> specificFields = investmentCategoryFieldRepository.findByCategoryId(categoryId);

        if (specificFields.isPresent()) {
            logger.debug("Found specific fields for category with id: {}", categoryId);
            return specificFields.get();
        } else {
            // 2. If specific fields are not found, search for default fields
            logger.debug("Specific fields not found for category id: {}, looking for default fields", categoryId);
            Optional<InvestmentCategoryField> defaultFields = investmentCategoryFieldRepository.findByCategoryId(DEFAULT_CATEGORY_ID);

            if (defaultFields.isPresent()) {
                logger.debug("Found default fields for investment listings");
                return defaultFields.get();
            } else {
                // 3. If neither specific nor default fields are found
                logger.warn("Neither specific nor default fields found for investment listing category id: {}", categoryId);
                throw new IllegalArgumentException("No fields found for investment listing category id: " + categoryId);
            }
        }
    }

    /**
     * Retrieves the field configuration for an investment listing by category ID (specific or default).
     * Returns an Optional to indicate that the configuration might be absent.
     *
     * @param categoryId The ID of the investment listing category (or 0L for defaults).
     * @return An Optional containing the InvestmentCategoryField if found.
     */
    public Optional<InvestmentCategoryField> getInvestmentCategoryField(Long categoryId) {
        logger.info("Fetching investment category field configuration with id: {}", categoryId);
        return investmentCategoryFieldRepository.findByCategoryId(categoryId);
    }


    /**
     * Creates or updates the field configuration for an investment listing.
     *
     * @param categoryId The ID of the category (or 0L for defaults).
     * @param fields The list of fields.
     * @return The saved InvestmentCategoryField configuration.
     */
    public InvestmentCategoryField createOrUpdateInvestmentCategoryFields(Long categoryId, List<Field> fields) {
        logger.info("Creating or updating investment category fields for id: {}", categoryId);

        Optional<InvestmentCategoryField> investmentCategoryFieldOptional = investmentCategoryFieldRepository.findByCategoryId(categoryId);
        InvestmentCategoryField investmentCategoryField;

        if (investmentCategoryFieldOptional.isPresent()) {
            logger.debug("Updating existing investment category fields for id: {}", categoryId);
            investmentCategoryField = investmentCategoryFieldOptional.get();
            investmentCategoryField.setFields(fields);
        } else {
            logger.debug("Creating new investment category fields for id: {}", categoryId);
            investmentCategoryField = new InvestmentCategoryField();
            investmentCategoryField.setCategoryId(categoryId);
            investmentCategoryField.setFields(fields);
        }

        InvestmentCategoryField savedInvestmentCategoryField = investmentCategoryFieldRepository.save(investmentCategoryField);
        logger.info("Saved investment category fields for id: {}", categoryId);
        return savedInvestmentCategoryField;
    }

    /**
     * Creates or updates an InvestmentCategoryField object directly.
     *
     * @param investmentCategoryField The InvestmentCategoryField object to save.
     * @return The saved InvestmentCategoryField object.
     */
    public InvestmentCategoryField createOrUpdateInvestmentCategoryField(InvestmentCategoryField investmentCategoryField) {
        logger.info("Creating or updating investment category field with id: {}", investmentCategoryField.getCategoryId());
        return investmentCategoryFieldRepository.save(investmentCategoryField);
    }


    /**
     * Deletes the field configuration for an investment listing by category ID.
     *
     * @param categoryId The ID of the category (or 0L for defaults).
     * @return true if deleted successfully, false otherwise.
     */
    public boolean deleteInvestmentCategoryFields(Long categoryId) {
        logger.info("Deleting investment category fields for id: {}", categoryId);

        Query query = new Query(Criteria.where("categoryId").is(categoryId));
        long deletedCount = mongoTemplate.remove(query, InvestmentCategoryField.class).getDeletedCount();

        if (deletedCount > 0) {
            logger.info("Deleted investment category fields for id: {}", categoryId);
            return true;
        } else {
            logger.warn("Investment category fields not found for id: {}", categoryId);
            return false;
        }
    }

    /**
     * Retrieves all field configurations for investment listings.
     *
     * @return A list of all InvestmentCategoryField configurations.
     */
    public List<InvestmentCategoryField> getAllInvestmentCategoryFields() {
        logger.info("Fetching all investment category fields");
        return investmentCategoryFieldRepository.findAll();
    }
}