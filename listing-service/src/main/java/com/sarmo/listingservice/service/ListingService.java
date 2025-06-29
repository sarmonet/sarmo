package com.sarmo.listingservice.service;

import com.sarmo.listingservice.dto.CreateListingDto;
import com.sarmo.listingservice.dto.ListingDto;
import com.sarmo.listingservice.dto.ListingFullInfoDto;
import com.sarmo.listingservice.dto.UpdateListingDto;
import com.sarmo.listingservice.entity.*;
import com.sarmo.listingservice.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ListingService {

    private static final Logger logger = LoggerFactory.getLogger(ListingService.class);
    private final ListingRepository listingRepository;
    private final ListingMongoService listingMongoService;
    private final SubCategoryRepository subCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final ListingRandomService listingRandomService;
    private final CommentService commentService;
    private final UserRepository userRepository;
    private final ListingPackagingDetailsRepository listingPackagingDetailsRepository;
    private final CategoryFieldService categoryFieldService;
    private final InvestmentCategoryFieldService investmentCategoryFieldService;


    public ListingService(ListingRepository listingRepository,
                          ListingMongoService listingMongoService,
                          SubCategoryRepository subCategoryRepository,
                          CategoryRepository categoryRepository,
                          ListingRandomService listingRandomService,
                          CommentService commentService,
                          UserRepository userRepository,
                          ListingPackagingDetailsRepository listingPackagingDetailsRepository,
                          CategoryFieldService categoryFieldService,
                          InvestmentCategoryFieldService investmentCategoryFieldService) {
        this.listingRepository = listingRepository;
        this.listingMongoService = listingMongoService;
        this.subCategoryRepository = subCategoryRepository;
        this.categoryRepository = categoryRepository;
        this.listingRandomService = listingRandomService;
        this.commentService = commentService;
        this.userRepository = userRepository;
        this.listingPackagingDetailsRepository = listingPackagingDetailsRepository;
        this.categoryFieldService = categoryFieldService;
        this.investmentCategoryFieldService = investmentCategoryFieldService;
    }


    // Logging and handling for missing listings
    private Listing findListingOrThrow(Long id) {
        return listingRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Listing not found with id {}", id);
                    return new RuntimeException("Listing not found with id " + id);
                });
    }

    /**
     * Checks if a user is the owner of a listing.
     *
     * @param listingId The ID of the listing.
     * @param userIdString The ID of the user as a String (e.g., from authentication token).
     * @return true if the user is the owner, false otherwise.
     */
    public boolean isOwner(Long listingId, String userIdString) {
        logger.debug("Checking ownership for listing {} by user (string) {}", listingId, userIdString);

        if (userIdString == null) {
            logger.debug("userIdString is null, cannot check ownership.");
            return false; // Cannot be owner if user ID is unknown
        }

        Optional<Listing> listingOptional = listingRepository.findById(listingId);

        if (listingOptional.isPresent()) {
            Listing listing = listingOptional.get();

            if (listing.getUser() == null || listing.getUser().getId() == null) {
                logger.warn("Listing {} found but has no owner assigned.", listingId);
                return false; // Listing has no owner, user cannot be the owner
            }

            Long ownerIdLong = listing.getUser().getId();
            boolean isActualOwner = String.valueOf(ownerIdLong).equals(userIdString);

            if (isActualOwner) {
                logger.debug("User (string) {} IS owner of listing {}", userIdString, listingId);
            } else {
                logger.debug("User (string) {} is NOT owner of listing {}. Actual owner ID: {}", userIdString, listingId, ownerIdLong);
            }

            return isActualOwner;

        } else {
            logger.warn("Listing with id {} not found during ownership check", listingId);
            return false;
        }
    }

    /**
     * Retrieves all listings with pagination and sorting.
     *
     * @param page The page number (0-indexed).
     * @param size The number of items per page.
     * @param sortBy The field to sort by.
     * @param sortOrder The sort order ("asc" or "desc").
     * @return A page of ListingDto objects.
     */
    public Page<ListingDto> getAllListings(int page, int size, String sortBy, String sortOrder) {
        logger.info("Retrieving all listings with pagination. Page: {}, Size: {}, Sort by: {}, Order: {}", page, size, sortBy, sortOrder);

        Sort.Direction direction = sortOrder != null && sortOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;

        String actualSortBy = mapSortByField(sortBy);

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, actualSortBy));

        Page<Listing> listingsPage = listingRepository.findAll(pageable);

        return listingsPage.map(this::convertToDto);
    }


    /**
     * Retrieves a listing by its ID.
     *
     * @param id The ID of the listing.
     * @return An Optional containing the Listing entity if found.
     */
    public Optional<Listing> getListingById(Long id) {
        logger.info("Fetching listing with id {}", id);
        Optional<Listing> listing = listingRepository.findById(id);
        if (listing.isPresent()) {
            logger.info("Found listing with id {}", id);
        } else {
            logger.warn("Listing with id {} not found", id);
        }
        return listing;
    }

    /**
     * Retrieves a listing by its ID and verifies ownership by the specified user.
     *
     * @param listingId The ID of the listing.
     * @param userId The ID of the user.
     * @return The Listing entity if found and owned by the user.
     * @throws AccessDeniedException if the listing does not belong to the user.
     */
    public Listing getListingByIdAndOwner(Long listingId, Long userId) throws AccessDeniedException {
        logger.info("Getting listing with id {} for user with id {}", listingId, userId);
        Listing listing = findListingOrThrow(listingId);

        if (!listing.getUser().getId().equals(userId)) {
            logger.warn("User {} attempted to access listing {} that belongs to user {}", userId, listingId, listing.getUser().getId());
            throw new AccessDeniedException("Listing with id " + listingId + " does not belong to user " + userId);
        }

        return listing;
    }

    /**
     * Retrieves all listings created by a specific user.
     *
     * @param userId The ID of the user.
     * @return A list of Listing entities.
     */
    public List<Listing> getAllListingsByUserId(Long userId) {
        logger.info("Getting all listings for user with id {}", userId);
        return listingRepository.findByUserId(userId);
    }

    /**
     * Retrieves a paginated list of all inactive listings.
     *
     * @param page      The page number (0-indexed).
     * @param size      The number of items per page.
     * @param sortBy    The field to sort by (e.g., "createdAt", "id", "title").
     * @param sortOrder The sort order ("asc" or "desc").
     * @return A page of ListingDto objects.
     */
    public Page<ListingDto> getAllInactiveListings(int page, int size, String sortBy, String sortOrder) {
        logger.info("Retrieving all inactive listings. Page: {}, Size: {}, Sort by: {}, Order: {}", page, size, sortBy, sortOrder);

        Sort.Direction direction = sortOrder != null && sortOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        String actualSortBy = mapSortByField(sortBy); // Helper method for mapping sort fields
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, actualSortBy));

        Page<Listing> inactiveListingsPage = listingRepository.findByIsActiveFalse(pageable);

        // Convert Page<Listing> to Page<ListingDto>
        return inactiveListingsPage.map(this::convertToDto);
    }

    /**
     * Retrieves a paginated list of all active listings.
     *
     * @param page      The page number (0-indexed).
     * @param size      The number of items per page.
     * @param sortBy    The field to sort by (e.g., "createdAt", "id", "title").
     * @param sortOrder The sort order ("asc" or "desc").
     * @return A page of ListingDto objects.
     */
    public Page<ListingDto> getAllActiveListings(int page, int size, String sortBy, String sortOrder) {
        logger.info("Retrieving all active listings. Page: {}, Size: {}, Sort by: {}, Order: {}", page, size, sortBy, sortOrder);

        Sort.Direction direction = sortOrder != null && sortOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        String actualSortBy = mapSortByField(sortBy);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, actualSortBy));

        Page<Listing> activeListingsPage = listingRepository.findByIsActiveTrue(pageable);

        return activeListingsPage.map(this::convertToDto);
    }

    /**
     * Retrieves a paginated list of all rejected listings.
     *
     * @param page      The page number (0-indexed).
     * @param size      The number of items per page.
     * @param sortBy    The field to sort by (e.g., "createdAt", "id", "title").
     * @param sortOrder The sort order ("asc" or "desc").
     * @return A page of ListingDto objects.
     */
    public Page<ListingDto> getAllRejectedListings(int page, int size, String sortBy, String sortOrder) {
        logger.info("Retrieving all rejected listings. Page: {}, Size: {}, Sort by: {}, Order: {}", page, size, sortBy, sortOrder);

        Sort.Direction direction = sortOrder != null && sortOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        String actualSortBy = mapSortByField(sortBy);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, actualSortBy));

        Page<Listing> rejectedListingsPage = listingRepository.findByIsRejectedTrue(pageable);

        return rejectedListingsPage.map(this::convertToDto);
    }

    /**
     * Helper method for mapping sort fields from the request (DTO) to entity fields.
     * This helps to avoid exposing internal DB field names and provides flexibility.
     * @param sortBy The string from the request, indicating the field to sort by.
     * @return The actual entity field name for sorting.
     */
    private String mapSortByField(String sortBy) {
        if (sortBy == null || sortBy.trim().isEmpty()) {
            return "createdat";
        }
        return switch (sortBy.toLowerCase()) {
            case "id" -> "id";
            case "createdat" -> "createdAt";
            case "price" -> "price";
            case "title" -> "title";
            case "viewcount" -> "viewCount";
            default -> {
                logger.warn("Unrecognized sort by field: {}. Defaulting to 'id'.", sortBy);
                yield "id";
            }
        };
    }

    /**
     * Retrieves all inactive listings created by a specific user.
     *
     * @param userId The ID of the user.
     * @return A list of Listing entities.
     */
    public List<Listing> getAllInactiveListingsByUserId(Long userId) {
        logger.info("Getting all inactive listings for user with id {}", userId);
        return listingRepository.findByUserIdAndIsActiveFalse(userId);
    }

    /**
     * Creates a new listing (basic version without DTO or dynamic fields handling).
     *
     * @param listing The Listing entity to create.
     * @return The created Listing entity.
     */
    public Listing createListing(Listing listing) {
        logger.info("Creating new listing with title: {}", listing.getTitle());
        Listing createdListing = listingRepository.save(listing);
        logger.info("Created listing with id {}", createdListing.getId());
        return createdListing;
    }

    private void verifyListingOwnership(Long userId, Listing listing) throws AccessDeniedException {
        if (!listing.getUser().getId().equals(userId)) {
            logger.warn("User {} attempted to update listing {} that belongs to user {}", userId, listing.getId(), listing.getUser().getId());
            throw new AccessDeniedException("Listing with id " + listing.getId() + " does not belong to user " + userId);
        }
    }

// Method to update listing with user ID check
    /**
     * Updates a listing owned by a specific user with details from a DTO.
     * Includes validation and update of dynamic fields in MongoDB.
     * Returns the full listing information after the update.
     *
     * @param userId The ID of the user performing the update.
     * @param id The ID of the listing to update.
     * @param listingDetailsDto The DTO containing updated listing details and dynamic fields.
     * @return The updated ListingFullInfoDto.
     * @throws AccessDeniedException if the listing does not belong to the user.
     * @throws RuntimeException for other unexpected errors.
     */
    public ListingFullInfoDto updateListing(Long userId, Long id, UpdateListingDto listingDetailsDto) throws AccessDeniedException {
        logger.info("Attempting to update listing with id {} for user {}", id, userId);
        Listing existingListing = findListingOrThrow(id);
        verifyListingOwnership(userId, existingListing);

        // Update standard fields in the relational DB
        updateListingDetails(existingListing, listingDetailsDto);
        Listing updatedListing = listingRepository.save(existingListing);
        logger.info("Updated listing with id {} for user {}", updatedListing.getId(), userId);

        // Handle dynamic fields update in MongoDB if they are present in UpdateListingDto
        Map<String, Object> updatedDynamicFields = null; // Variable to hold the dynamic fields for the final DTO

        if (listingDetailsDto.getFields() != null) { // Check if the fields map is present in the DTO
            logger.debug("Dynamic fields found in UpdateListingDto for listing id: {}, initiating validation and update in MongoDB", updatedListing.getId());
            List<Field> expectedFields = getExpectedFieldsForListing(updatedListing);
            listingMongoService.validateListingFields(listingDetailsDto.getFields(), expectedFields);
            // Update in MongoDB. listingMongoService.updateListingByListingId will return the updated document or null.
            ListingMongo updatedListingMongo = listingMongoService.updateListingByListingId(updatedListing.getId(), listingDetailsDto.getFields());
            logger.debug("Dynamic fields updated in MongoDB for listing id: {}", updatedListing.getId());
            if (updatedListingMongo != null) {
                updatedDynamicFields = updatedListingMongo.getFields();
            } else {
                // If the MongoDB document was not found for update (rare case after creation),
                // but DTO contained fields, use fields from DTO for the response DTO.
                updatedDynamicFields = listingDetailsDto.getFields();
                logger.warn("ListingMongo document not found for update for listing id: {}, but dynamic fields were in DTO. Using DTO fields for response DTO.", updatedListing.getId());
            }
        } else {
            // If dynamic fields were not passed in the DTO, get them from MongoDB for the DTO response
            logger.debug("No dynamic fields in UpdateListingDto for listing id: {}, fetching existing from MongoDB for Full DTO", updatedListing.getId());
            ListingMongo existingListingMongo = listingMongoService.getListingByListingId(updatedListing.getId());
            if (existingListingMongo != null) {
                updatedDynamicFields = existingListingMongo.getFields();
            } else {
                updatedDynamicFields = Collections.emptyMap(); // If the MongoDB document does not exist at all
                logger.debug("No existing ListingMongo document found for listing id: {}", updatedListing.getId());
            }
        }

        // Ensure updatedDynamicFields is not null before use
        if (updatedDynamicFields == null) {
            updatedDynamicFields = Collections.emptyMap();
        }

        // Assemble and return ListingFullInfoDto
        // Use the existing or adapted helper method to build the DTO
        ListingFullInfoDto fullListingDto = convertToFullInfoDto(updatedListing, updatedDynamicFields);

        // Add other data usually included in Full DTO (comments, rating, related listings)
        try {
            fullListingDto.setComments(commentService.getCommentsForListing(updatedListing.getId()));
            fullListingDto.setAverageRating(updatedListing.getAverageRating());
            fullListingDto.setTotalRatings(updatedListing.getTotalRatings());
            fullListingDto.setSimilarListings(listingRandomService.getRandomListingsByCategoryAndSubcategory(updatedListing.getCategory().getId(), updatedListing.getSubCategory().getId(), 4));

        } catch (Exception e) {
            // Handle errors fetching additional data
            logger.error("Failed to fetch additional data (comments, rating, related) for full DTO of listing id {}: {}", updatedListing.getId(), e.getMessage(), e);
            // Decide how to handle: ignore, log, throw exception.
            // For simplicity, currently just log and return DTO without this data if fetch fails.
        }

        logger.info("Successfully assembled ListingFullInfoDto after updating listing id {}", updatedListing.getId());
        return fullListingDto; // Return the assembled DTO
    }


// Method to update listing without user ID check (for Admin)
    /**
     * Updates a listing with details from a DTO (without owner check).
     * Includes validation and update of dynamic fields in MongoDB.
     * Returns the full listing information after the update.
     *
     * @param id The ID of the listing to update.
     * @param listingDetailsDto The DTO containing updated listing details and dynamic fields.
     * @return The updated ListingFullInfoDto.
     * @throws RuntimeException for other unexpected errors.
     */
    public ListingFullInfoDto updateListing(Long id, UpdateListingDto listingDetailsDto) { // Changed return type
        logger.info("Attempting to update listing with id {}", id);
        Listing existingListing = findListingOrThrow(id);
        // No ownership check here

        updateListingDetails(existingListing, listingDetailsDto);
        Listing updatedListing = listingRepository.save(existingListing);
        logger.info("Updated listing with id {}", updatedListing.getId());

        // Handle dynamic fields update in MongoDB if they are present in UpdateListingDto
        Map<String, Object> updatedDynamicFields = null; // Variable to hold the dynamic fields for the final DTO

        if (listingDetailsDto.getFields() != null) { // Check if the fields map is present in the DTO
            logger.debug("Dynamic fields found in UpdateListingDto for listing id: {}, initiating validation and update in MongoDB", updatedListing.getId());
            List<Field> expectedFields = getExpectedFieldsForListing(updatedListing);
            listingMongoService.validateListingFields(listingDetailsDto.getFields(), expectedFields);
            ListingMongo updatedListingMongo = listingMongoService.updateListingByListingId(updatedListing.getId(), listingDetailsDto.getFields());
            logger.debug("Dynamic fields updated in MongoDB for listing id: {}", updatedListing.getId());
            if (updatedListingMongo != null) {
                updatedDynamicFields = updatedListingMongo.getFields();
            } else {
                updatedDynamicFields = listingDetailsDto.getFields();
                logger.warn("ListingMongo document not found for update, but dynamic fields were in DTO. Using DTO fields for response DTO.");
            }
        } else {
            // If dynamic fields were not passed in the DTO, get them from MongoDB for the DTO response
            logger.debug("No dynamic fields in UpdateListingDto for listing id: {}, fetching existing from MongoDB for Full DTO", updatedListing.getId());
            ListingMongo existingListingMongo = listingMongoService.getListingByListingId(updatedListing.getId());
            if (existingListingMongo != null) {
                updatedDynamicFields = existingListingMongo.getFields();
            } else {
                updatedDynamicFields = Collections.emptyMap();
                logger.debug("No existing ListingMongo document found for listing id: {}", updatedListing.getId());
            }
        }

        if (updatedDynamicFields == null) {
            updatedDynamicFields = Collections.emptyMap();
        }

        // Assemble and return ListingFullInfoDto, including additional data
        ListingFullInfoDto fullListingDto = convertToFullInfoDto(updatedListing, updatedDynamicFields);
        try {
            fullListingDto.setComments(commentService.getCommentsForListing(updatedListing.getId()));
            fullListingDto.setAverageRating(updatedListing.getAverageRating());
            fullListingDto.setTotalRatings(updatedListing.getTotalRatings());
            fullListingDto.setSimilarListings(listingRandomService.getRandomListingsByCategoryAndSubcategory(updatedListing.getCategory().getId(), updatedListing.getSubCategory().getId(), 4));

        } catch (Exception e) {
            logger.error("Failed to fetch additional data (comments, rating, related) for full DTO of listing id {}: {}", updatedListing.getId(), e.getMessage(), e);
        }

        logger.info("Successfully assembled ListingFullInfoDto after updating listing id {}", updatedListing.getId());
        return fullListingDto; // Return the assembled DTO
    }

    private void updateListingDetails(Listing existingListing, UpdateListingDto listingDetailsDto) {
        if (listingDetailsDto.getTitle() != null) {
            existingListing.setTitle(listingDetailsDto.getTitle());
        }
        if (listingDetailsDto.getDescription() != null) {
            existingListing.setDescription(listingDetailsDto.getDescription());
        }
        if (listingDetailsDto.getPrice() != null) {
            existingListing.setPrice(listingDetailsDto.getPrice());
        }
        if (listingDetailsDto.getMainImage() != null) {
            existingListing.setMainImage(listingDetailsDto.getMainImage());
        }
        if (listingDetailsDto.getImages() != null) {
            existingListing.setImages(listingDetailsDto.getImages());
        }
        if (listingDetailsDto.getVideoUrl() != null) {
            existingListing.setVideoUrl(listingDetailsDto.getVideoUrl());
        }
        if (listingDetailsDto.getCountry() != null) {
            existingListing.setCountry(listingDetailsDto.getCountry());
        }
        if (listingDetailsDto.getCity() != null) {
            existingListing.setCity(listingDetailsDto.getCity());
        }
        if (listingDetailsDto.getFullAddress() != null) {
            existingListing.setFullAddress(listingDetailsDto.getFullAddress());
        }
        if (listingDetailsDto.getStatus() != null) {
            existingListing.setStatus(listingDetailsDto.getStatus());
        }
        // If UpdateListingDto contains dynamic fields, they should be handled here or in the updateListing methods.
        // The Invest flag can also be updated via DTO, and its update will affect which field set is expected
        // during subsequent dynamic field updates.
        if (listingDetailsDto.getInvest() != null) {
            existingListing.setInvest(listingDetailsDto.getInvest());
        }
    }

    // This method updateListingDetails(Listing existingListing, Listing listingDetails)
    // is used for internal conversion/update of the Listing entity.
    // It does not directly handle dynamic fields from a DTO, so it remains unchanged.
    private void updateListingDetails(Listing existingListing, Listing listingDetails) {
        existingListing.setTitle(listingDetails.getTitle());
        existingListing.setCategory(listingDetails.getCategory());
        existingListing.setSubCategory(listingDetails.getSubCategory());
        existingListing.setDescription(listingDetails.getDescription());
        existingListing.setPrice(listingDetails.getPrice());
        existingListing.setMainImage(listingDetails.getMainImage());
        existingListing.setImages(listingDetails.getImages());
        existingListing.setVideoUrl(listingDetails.getVideoUrl());
        existingListing.setCountry(listingDetails.getCountry());
        existingListing.setCity(listingDetails.getCity());
        existingListing.setFullAddress(listingDetails.getFullAddress());
        existingListing.setPremiumSubscription(listingDetails.getPremiumSubscription());
        existingListing.setInvest(listingDetails.getInvest());
        existingListing.setStatus(listingDetails.getStatus());
    }


    /**
     * Deletes a listing owned by a specific user.
     * Also deletes associated packaging details and the corresponding document from MongoDB.
     *
     * @param userId The ID of the user performing the deletion.
     * @param listingId The ID of the listing to delete.
     * @throws AccessDeniedException if the listing does not belong to the user.
     */
    @Transactional
    public void deleteListingByOwner(Long userId, Long listingId) throws AccessDeniedException {
        logger.info("Attempting to delete listing with id {} for user {}", listingId, userId);
        Listing listingToDelete = findListingOrThrow(listingId);

        // Check if the listing belongs to the specified user using the User object association
        if (!listingToDelete.getUser().getId().equals(userId)) {
            logger.warn("User {} attempted to delete listing {} that belongs to user {}", userId, listingId, listingToDelete.getUser().getId());
            throw new AccessDeniedException("Listing with id " + listingId + " does not belong to user " + userId);
        }

        // Delete associated packaging details
        listingPackagingDetailsRepository.deleteByListing_Id(listingId);

        // Delete the document from MongoDB before deleting from the relational database
        listingMongoService.deleteListingByListingId(listingId);

        // Delete from the relational database
        listingRepository.deleteById(listingId);

        logger.info("Deleted listing with id {} for user {}", listingId, userId);
        logger.info("Listing with id {} deleted successfully by user {}", listingId, userId);
    }

    /**
     * Deletes a listing
     * Also deletes associated packaging details and the corresponding document from MongoDB.
     *
     * @param listingId The ID of the listing to delete.
     */
    @Transactional
    public void deleteListing(Long listingId) {
        logger.info("Attempting to delete listing with id {}", listingId);
        findListingOrThrow(listingId);

        // Delete associated packaging details
        listingPackagingDetailsRepository.deleteByListing_Id(listingId);

        // Delete the document from MongoDB before deleting from the relational database
        listingMongoService.deleteListingByListingId(listingId);

        // Delete from the relational database
        listingRepository.deleteById(listingId);

        logger.info("Deleted listing with id {}", listingId);
        logger.info("Listing with id {} deleted successfully", listingId);
    }


    /**
     * Counts the total number of listings.
     *
     * @return The total number of listings.
     */
    public long countAllListings() {
        logger.info("Counting all listings");
        long count = listingRepository.count();
        logger.info("Total number of listings: {}", count);
        return count;
    }

    /**
     * Updates the images list for a listing owned by a specific user.
     *
     * @param userId The ID of the user performing the update.
     * @param listingId The ID of the listing to update.
     * @param images The new list of image URLs.
     * @return The updated Listing entity.
     * @throws AccessDeniedException if the listing does not belong to the user.
     */
    @Transactional
    public Listing updateListingImages(Long userId, Long listingId, List<String> images) throws AccessDeniedException {
        logger.info("Attempting to update images for listing with id {} for user {}", listingId, userId);
        Listing existingListing = findListingOrThrow(listingId);

        // Check if the listing belongs to the specified user using the User object association
        if (!existingListing.getUser().getId().equals(userId)) {
            logger.warn("User {} attempted to update images for listing {} that belongs to user {}", userId, listingId, existingListing.getUser().getId());
            throw new AccessDeniedException("Listing with id " + listingId + " does not belong to user " + userId);
        }

        existingListing.setImages(images);
        Listing updatedListing = listingRepository.save(existingListing);
        logger.info("Updated images for listing with id {} for user {}", listingId, userId);
        return updatedListing;
    }

    /**
     * Updates the main image for a listing owned by a specific user.
     *
     * @param userId The ID of the user performing the update.
     * @param listingId The ID of the listing to update.
     * @param image The new main image URL.
     * @return The updated Listing entity.
     * @throws AccessDeniedException if the listing does not belong to the user.
     */
    @Transactional
    public Listing updateListingMainImage(Long userId, Long listingId, String image) throws AccessDeniedException {
        logger.info("Attempting to update main image for listing with id {} for user {}", listingId, userId);
        Listing existingListing = findListingOrThrow(listingId);

        // Check if the listing belongs to the specified user using the User object association
        if (!existingListing.getUser().getId().equals(userId)) {
            logger.warn("User {} attempted to update main image for listing {} that belongs to user {}", userId, listingId, existingListing.getUser().getId());
            throw new AccessDeniedException("Listing with id " + listingId + " does not belong to user " + userId);
        }

        existingListing.setMainImage(image);
        Listing updatedListing = listingRepository.save(existingListing);
        logger.info("Updated main image for listing with id {} for user {}", listingId, userId);
        return updatedListing;
    }

    // Count listings by category
    public long countListingsByCategory(Long categoryId) {
        logger.info("Counting listings for category id {}", categoryId);
        long count = listingRepository.countByCategoryId(categoryId);
        logger.info("Total number of listings for category id {}: {}", categoryId, count);
        return count;
    }

    // Count listings by subcategory
    public long countListingsBySubCategory(Long subCategoryId) {
        logger.info("Counting listings for subcategory id {}", subCategoryId);
        long count = listingRepository.countBySubCategoryId(subCategoryId);
        logger.info("Total number of listings for subcategory id {}: {}", subCategoryId, count);
        return count;
    }

    /**
     * Retrieves full information about a listing, including dynamic fields from MongoDB.
     *
     * @param id The ID of the listing.
     * @return An Optional containing the ListingFullInfoDto if found.
     */
    public Optional<ListingFullInfoDto> getFullListingById(Long id) {
        Optional<Listing> listingOptional = listingRepository.findById(id);
        if (listingOptional.isEmpty()) {
            return Optional.empty();
        }

        Listing listing = listingOptional.get();
        // Retrieve dynamic fields from MongoDB
        ListingMongo listingMongo = listingMongoService.getListingByListingId(id);

        // If the MongoDB document is not found, the listing might have been created without dynamic fields
        // or there was an error. Here you can decide how to handle this (return an empty DTO,
        // throw an exception, or return a DTO without dynamic fields).
        // In the current logic, an Optional<ListingFullInfoDto> is returned without dynamic fields.
        Map<String, Object> dynamicFields = (listingMongo != null) ? listingMongo.getFields() : Collections.emptyMap();

        if (listingMongo == null) {
            logger.warn("ListingMongo document not found for listing id: {}", id);
        }

        ListingFullInfoDto fullListingDto = new ListingFullInfoDto(listing, dynamicFields, listingRandomService.getRandomListingsByCategoryAndSubcategory(listing.getCategory().getId(), listing.getSubCategory().getId(), 4));
        fullListingDto.setComments(commentService.getCommentsForListing(listing.getId()));
        fullListingDto.setAverageRating(listing.getAverageRating());
        fullListingDto.setTotalRatings(listing.getTotalRatings());
        return Optional.of(fullListingDto);
    }

    /**
     * Creates a new listing with full details, including dynamic fields.
     * Includes validation of dynamic fields based on listing type (regular/investment).
     *
     * @param userId The ID of the user creating the listing.
     * @param createListingDto The DTO containing listing details.
     * @return The created ListingFullInfoDto.
     * @throws EntityNotFoundException if the user, category, or subcategory is not found.
     * @throws IllegalArgumentException if category/subcategory IDs are invalid.
     * @throws RuntimeException for other unexpected errors.
     */
    @Transactional
    public ListingFullInfoDto createListing(Long userId, CreateListingDto createListingDto) {
        logger.info("Creating listing for user {} with data: {}", userId, createListingDto);
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

            // 1. Create and save the main listing entity in the relational database
            Listing listing = createListingFromDto(createListingDto);
            listing.setUser(user);
            Listing savedListing = listingRepository.save(listing);
            logger.info("Saved Listing entity with id: {}", savedListing.getId());

            // 2. Determine the listing type and get the expected fields for validation
            List<Field> expectedFields = getExpectedFieldsForListing(savedListing);
            logger.debug("Expected fields for validation: {}", expectedFields.stream().map(Field::getName).collect(Collectors.joining(", ")));

            // 3. Validate dynamic fields from DTO using ListingMongoService
            // If validation fails, ListingMongoService will throw an exception
            if (createListingDto.getFields() != null && !createListingDto.getFields().isEmpty()) {
                logger.debug("Validating dynamic fields from CreateListingDto for listing id: {}", savedListing.getId());
                listingMongoService.validateListingFields(createListingDto.getFields(), expectedFields);
                logger.debug("Dynamic fields validated successfully for listing id: {}", savedListing.getId());

                // 4. Create and save dynamic fields in MongoDB
                createListingMongo(savedListing, createListingDto);
            } else {
                logger.debug("No dynamic fields provided in CreateListingDto for listing id: {}", savedListing.getId());
                // Optionally, you could validate that no fields are provided if the expected list is not empty and all are required.
                // This depends on whether listings are *required* to have dynamic fields.
            }


            // 5. Create and save PremiumSubscription if data is in DTO
            createPremiumSubscription(savedListing, createListingDto);
            // If PremiumSubscription relationship is not cascade save, you might need:
            // premiumSubscriptionRepository.save(premiumSubscription);


            // 6. Assemble and return the Full DTO
            ListingFullInfoDto responseDto = convertToFullInfoDto(savedListing, createListingDto.getFields());
            logger.info("Listing created successfully: {}", responseDto);
            return responseDto;
        } catch (EntityNotFoundException e) {
            logger.error("Error creating listing: {}", e.getMessage());
            throw e; // Re-throw specific exception
        } catch (IllegalArgumentException e) {
            logger.error("Error creating listing due to invalid category/subcategory: {}", e.getMessage());
            throw e; // Re-throw specific exception
        }
        // Catch validation exceptions from ListingMongoService and potentially wrap them
        // if a different exception type is preferred for the controller.
        // catch (FieldNotFoundException | TypeMismatchException | UnknownFieldException e) {
        //     logger.error("Validation error during listing creation: {}", e.getMessage());
        //     throw new ValidationException("Failed to validate listing fields: " + e.getMessage(), e); // Example wrapping
        // }
        catch (Exception e) {
            logger.error("Unexpected error creating listing: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create listing", e); // Wrap unknown errors
        }
    }

    /**
     * Helper method to get the expected list of fields based on the listing type (regular/investment).
     *
     * @param listing The Listing entity.
     * @return A list of expected Field objects.
     */
    private List<Field> getExpectedFieldsForListing(Listing listing) {
        Long categoryId = listing.getCategory().getId();
        Boolean isInvestmentListing = listing.getInvest(); // Assuming Invest flag is in the Listing entity

        if (isInvestmentListing != null && isInvestmentListing) {
            logger.debug("Listing is investment type, fetching fields from InvestmentCategoryFieldService for category id: {}", categoryId);
            // Get fields for investment listing (with default fallback logic)
            return investmentCategoryFieldService.getInvestmentListingFields(categoryId).getFields();
        } else {
            logger.debug("Listing is regular type, fetching fields from CategoryFieldService for category id: {}", categoryId);
            // Get fields for regular listing
            Optional<CategoryField> categoryFieldOptional = categoryFieldService.getCategoryFields(categoryId);
            // If no fields found for a regular category, return empty list (or throw exception)
            return categoryFieldOptional.map(CategoryField::getFields).orElseGet(() -> {
                logger.warn("No CategoryField configuration found for regular listing category id: {}", categoryId);
                return Collections.emptyList();
            });
        }
    }


    private Listing createListingFromDto(CreateListingDto dto) {
        Listing listing = new Listing();
        listing.setTitle(dto.getTitle());
        // When creating a listing via DTO, get Category and SubCategory by ID
        listing.setCategory(categoryRepository.findById(dto.getCategoryId()).orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + dto.getCategoryId())));
        listing.setSubCategory(subCategoryRepository.findById(dto.getSubCategoryId()).orElse(null)); // SubCategory can be optional
        listing.setDescription(dto.getDescription());
        listing.setPrice(dto.getPrice());
        listing.setMainImage(dto.getMainImage());
        listing.setImages(dto.getImages());
        listing.setVideoUrl(dto.getVideoUrl());
        listing.setCountry(dto.getCountry());
        listing.setCity(dto.getCity());
        listing.setFullAddress(dto.getFullAddress());
        listing.setStatus(dto.getStatus());
        listing.setInvest(dto.getInvest()); // Set the Invest flag from DTO
        return listing;
    }

    private void createPremiumSubscription(Listing listing, CreateListingDto dto) {
        if (dto.getPremiumStartDate() != null && dto.getPremiumEndDate() != null) {
            PremiumSubscription premiumSubscription = new PremiumSubscription();
            premiumSubscription.setListing(listing); // Associate subscription with the listing
            premiumSubscription.setStartDate(dto.getPremiumStartDate());
            premiumSubscription.setEndDate(dto.getPremiumEndDate());
            listing.setPremiumSubscription(premiumSubscription); // Set the subscription in the listing
            // PremiumSubscriptionRepository might be needed to save the subscription itself
            // premiumSubscriptionRepository.save(premiumSubscription); // If no cascade
        }
    }

    private void createListingMongo(Listing listing, CreateListingDto dto) {
        // Create the ListingMongo object only after successful field validation
        ListingMongo listingMongo = new ListingMongo();
        listingMongo.setListingId(listing.getId()); // Use the ID of the saved Listing entity
        listingMongo.setCategoryId(dto.getCategoryId()); // Category from DTO (should match the category in Listing)
        listingMongo.setFields(dto.getFields()); // Dynamic fields from DTO
        listingMongoService.createListing(listingMongo); // Delegate saving to MongoDB
    }

    private ListingFullInfoDto convertToFullInfoDto(Listing listing, java.util.Map<String, Object> fields) {
        // When converting to DTO, use the fields from the request or obtained from ListingMongo
        ListingFullInfoDto dto = new ListingFullInfoDto(listing, fields);
        // Set other fields that do not depend on dynamic fields
        // For example, if you need to set averageRating, totalRatings, etc. here
        // dto.setAverageRating(listing.getAverageRating());
        // dto.setTotalRatings(listing.getTotalRatings());
        return dto;
    }

    /**
     * Retrieves a list of listings by their IDs.
     *
     * @param ids A list of listing IDs.
     * @return A list of ListingDto objects.
     */
    public List<ListingDto> getListingsByIds(List<Long> ids) {
        logger.info("Fetching listings by ids: {}", ids);
        List<Listing> listings = listingRepository.findAllById(ids);
        logger.info("Found {} listings by ids", listings.size());
        return listings.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Method to convert Listing to ListingDto
    private ListingDto convertToDto(Listing listing) {
        ListingDto dto = new ListingDto();
        dto.setId(listing.getId());
        dto.setTitle(listing.getTitle());
        dto.setCategory(listing.getCategory());
        dto.setSubCategory(listing.getSubCategory());
        dto.setPrice(listing.getPrice());
        dto.setCountry(listing.getCountry());
        dto.setCity(listing.getCity());
        dto.setCreatedAt(listing.getCreatedAt());
        dto.setStatus(listing.getStatus());
        dto.setPremiumSubscription(listing.getPremiumSubscription() != null);
        dto.setAverageRating(listing.getAverageRating());
        dto.setMainImage(listing.getMainImage());
        dto.setViewCount(listing.getViewCount());
        dto.setInvest(listing.getInvest()); // Include Invest flag in DTO if needed there
        return dto;
    }
}