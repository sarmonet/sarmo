package com.sarmo.listingservice.service;

import com.sarmo.listingservice.entity.Field; // Now Field is needed directly
import com.sarmo.listingservice.entity.ListingMongo;
import com.sarmo.listingservice.exception.FieldNotFoundException;
import com.sarmo.listingservice.exception.TypeMismatchException;
import com.sarmo.listingservice.exception.UnknownFieldException;
import com.sarmo.listingservice.repository.ListingMongoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collections; // Import for emptyMap/emptyList
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ListingMongoService {

    private final ListingMongoRepository listingMongoRepository;
    // Removed dependency on CategoryFieldService as the calling service will provide the fields
    // private final CategoryFieldService categoryFieldService;
    private static final Logger logger = LoggerFactory.getLogger(ListingMongoService.class);

    // Constructor without CategoryFieldService
    public ListingMongoService(ListingMongoRepository listingMongoRepository) {
        this.listingMongoRepository = listingMongoRepository;
    }

    public List<ListingMongo> getAllListings() {
        logger.info("Fetching all listings");
        return listingMongoRepository.findAll();
    }

    public Optional<ListingMongo> getListingById(String id) {
        logger.info("Fetching listing by id: {}", id);
        return listingMongoRepository.findById(id);
    }

    /**
     * Creates or updates a ListingMongo document.
     * Validation should be performed BEFORE calling this method, typically in ListingService.
     *
     * @param listingMongo The ListingMongo object to create or update.
     * @return The saved ListingMongo object.
     */
    public ListingMongo createListing(ListingMongo listingMongo) {
        // Validation should happen BEFORE calling this method in ListingService
        logger.info("Creating or updating listing: {}", listingMongo.getListingId());

        ListingMongo existingListing = listingMongoRepository.findByListingId(listingMongo.getListingId());

        if (existingListing != null) {
            listingMongo.setId(existingListing.getId()); // Preserve the id of the existing record
        }
        return listingMongoRepository.save(listingMongo);
    }

    /**
     * Updates listing fields in MongoDB based on the listingId from the relational database.
     * Validation of fields should be done BEFORE calling this method.
     *
     * @param listingId The ID of the listing from the relational database.
     * @param fields The map of dynamic fields to update.
     * @return The updated ListingMongo document, or null if the listing was not found in MongoDB.
     */
    public ListingMongo updateListingByListingId(Long listingId, Map<String, Object> fields) {
        logger.info("Updating listing fields by listing id: {}", listingId);
        ListingMongo existingListing = listingMongoRepository.findByListingId(listingId);
        if (existingListing != null) {
            existingListing.setFields(fields);
            return listingMongoRepository.save(existingListing);
        } else {
            logger.warn("Listing with listingId: {} not found for update in MongoDB", listingId);
            // Depending on logic, you might throw an exception or create a new record here
            return null; // Or throw an exception
        }
    }


    /**
     * Updates a ListingMongo document by its MongoDB document ID.
     * Validation should be performed BEFORE calling this method.
     *
     * @param id The MongoDB document ID.
     * @param updatedListingMongo The updated ListingMongo object.
     * @return The updated ListingMongo document, or null if the document was not found.
     */
    public ListingMongo updateListing(String id, ListingMongo updatedListingMongo) {
        // Validation should happen BEFORE calling this method
        logger.info("Updating listing with id: {}", id);
        Optional<ListingMongo> existingListing = listingMongoRepository.findById(id);
        if (existingListing.isPresent()) {
            updatedListingMongo.setId(id);
            return listingMongoRepository.save(updatedListingMongo);
        } else {
            logger.warn("Listing with id: {} not found for update", id);
            return null;
        }
    }

    public void deleteListing(String id) {
        logger.info("Deleting listing with id: {}", id);
        listingMongoRepository.deleteById(id);
    }

    /**
     * Deletes a ListingMongo document based on the listingId from the relational database.
     *
     * @param listingId The ID of the listing from the relational database.
     */
    public void deleteListingByListingId(Long listingId) {
        logger.info("Deleting listing by listingId: {}", listingId);
        ListingMongo existingListing = listingMongoRepository.findByListingId(listingId);
        if (existingListing != null) {
            listingMongoRepository.deleteById(existingListing.getId());
            logger.info("Deleted listing with listingId: {}", listingId);
        } else {
            logger.warn("Listing with listingId: {} not found for deletion in MongoDB", listingId);
        }
    }


    public List<ListingMongo> getListingsByCategoryId(Long categoryId) {
        logger.info("Fetching listings by category id: {}", categoryId);
        return listingMongoRepository.findByCategoryId(categoryId);
    }

    public ListingMongo getListingByListingId(Long listingId) {
        logger.info("Fetching listing by listing id: {}", listingId);
        return listingMongoRepository.findByListingId(listingId);
    }

    /**
     * Validates the fields map of a ListingMongo object against a provided list of expected fields.
     * This method performs the generic validation checks (required, type, unknown fields).
     * The caller is responsible for providing the correct list of expected fields based on the listing type (regular/investment).
     *
     * @param actualFields The map of dynamic fields from the ListingMongo object or DTO.
     * @param expectedFields The list of expected Field definitions (obtained from CategoryFieldService or InvestmentCategoryFieldService).
     * @throws FieldNotFoundException if a required field is missing or empty.
     * @throws TypeMismatchException if a field's type does not match the expected type.
     * @throws UnknownFieldException if an unexpected field is present.
     */
    public void validateListingFields(Map<String, Object> actualFields, List<Field> expectedFields) {
        logger.debug("Starting validation of listing fields.");

        // Check if actualFields or expectedFields are null and handle them as empty
        if (actualFields == null) {
            actualFields = Collections.emptyMap(); // Treat as having no fields to validate
            logger.debug("Actual fields map is null, using empty map for validation.");
        }
        if (expectedFields == null) {
            expectedFields = Collections.emptyList(); // No expected fields, validation is always successful (unless there are extra fields)
            logger.debug("Expected fields list is null, using empty list for validation.");
        }

        // 1. Check required fields and type matching for expected fields
        List<String> expectedFieldNames = expectedFields.stream().map(Field::getName).collect(Collectors.toList()); // Collect into a list for quick lookup of unknown fields

        for (Field expectedField : expectedFields) {
            String fieldName = expectedField.getName();
            Object actualValue = actualFields.get(fieldName);

            logger.debug("Validating field: '{}', expected type: '{}', required: {}", fieldName, expectedField.getType(), expectedField.getRequired());

            // Check for required fields
            if (expectedField.getRequired() != null && expectedField.getRequired()) { // Added null check for safety
                // A required field must be present and not an empty string (if String type)
                if (actualValue == null || (actualValue instanceof String && ((String) actualValue).trim().isEmpty())) {
                    logger.error("Required field '{}' is missing or empty.", fieldName);
                    throw new FieldNotFoundException(fieldName);
                }
            }

            // Check type only if the value is present
            if (actualValue != null) {
                if (!validateType(actualValue, expectedField.getType())) {
                    logger.error("Type mismatch for field '{}'. Expected type: '{}', Actual value class: '{}'", fieldName, expectedField.getType(), actualValue.getClass().getSimpleName());
                    throw new TypeMismatchException(fieldName, expectedField.getType(), actualValue);
                }
            }
        }

        // 2. Check for unknown fields present in actualFields
        for (String actualFieldName : actualFields.keySet()) {
            if (!expectedFieldNames.contains(actualFieldName)) {
                logger.error("Unknown field '{}' found.", actualFieldName);
                throw new UnknownFieldException(actualFieldName);
            }
        }

        logger.debug("Listing fields validated successfully.");
    }

    // The validateType method remains the same functionally, just translated
    private boolean validateType(Object value, String expectedType) {
        logger.debug("Validating type. Expected: {}, Actual value class: {}", expectedType, value != null ? value.getClass().getSimpleName() : "null"); // Added null check
        if (expectedType == null) { // If the expected type is not specified, is any type valid? Or is it an error?
            logger.warn("Expected type is null for validation.");
            return false; // Or return true, depending on requirements
        }

        // Compare class names or use explicit checks
        if (expectedType.equalsIgnoreCase("String")) {
            return value instanceof String;
        } else if (expectedType.equalsIgnoreCase("File")) {
            // In your logic, does Field.type "File" map to String for storing URL/path? Assuming yes.
            return value instanceof String;
        } else if (expectedType.equalsIgnoreCase("Integer")) {
            return value instanceof Integer;
        } else if (expectedType.equalsIgnoreCase("Boolean")) {
            return value instanceof Boolean;
        } else if (expectedType.equalsIgnoreCase("Double")) {
            return value instanceof Double;
        } else if (expectedType.equalsIgnoreCase("Long")) {
            return value instanceof Long;
        } else if (expectedType.equalsIgnoreCase("List")) {
            return value instanceof List;
        } else if (expectedType.equalsIgnoreCase("Map")) {
            return value instanceof Map;
        }
        logger.warn("Unknown expected field type specified in configuration: {}", expectedType);
        return false; // Unknown expected type
    }
}