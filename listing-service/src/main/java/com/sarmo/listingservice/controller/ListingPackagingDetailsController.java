package com.sarmo.listingservice.controller;

import com.sarmo.listingservice.dto.ListingPackagingDetailsDto;
import com.sarmo.listingservice.entity.Listing;
import com.sarmo.listingservice.entity.ListingPackagingDetails;
import com.sarmo.listingservice.service.ListingPackagingDetailsService;
import com.sarmo.listingservice.service.ListingService;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/listing")
public class ListingPackagingDetailsController {

    private static final Logger logger = LoggerFactory.getLogger(ListingPackagingDetailsController.class);

    private final ListingPackagingDetailsService packagingDetailsService;

    private final ListingService listingService;

    public ListingPackagingDetailsController(ListingPackagingDetailsService packagingDetailsService, ListingService listingService) {
        this.packagingDetailsService = packagingDetailsService;
        this.listingService = listingService;
    }

    @GetMapping("/{listingId}/packaging-details")
    public ResponseEntity<ListingPackagingDetailsDto> getListingPackagingDetails(@PathVariable Long listingId) {
        logger.info("GET /api/v1/listings/{}/packaging-details - Fetching packaging details for listing", listingId);
        ListingPackagingDetailsDto detailsDto = packagingDetailsService.getPackagingDetails(listingId);

        if (detailsDto != null) {
            logger.debug("Packaging details found for listing {}.", listingId);
            return ResponseEntity.ok(detailsDto);
        } else {
            logger.warn("Packaging details not found for listing {}.", listingId);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/packaging-details")
    public ResponseEntity<List<ListingPackagingDetailsDto>> getListingsPackagingDetails(@RequestParam List<Long> listingIds) {
        logger.info("GET /api/v1/listings/packaging-details?listingIds={} - Fetching packaging details for multiple listings", listingIds);
        List<ListingPackagingDetailsDto> detailsList = packagingDetailsService.getPackagingDetailsForListings(listingIds);
        logger.debug("Fetched {} packaging details entries for {} requested listing IDs.", detailsList.size(), listingIds.size());
        return ResponseEntity.ok(detailsList);
    }

    // New endpoint to get all active packaging details (Admin only)
    @GetMapping("/packaging-details/active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ListingPackagingDetailsDto>> getAllActivePackagingDetails() {
        logger.info("GET /api/v1/listings/packaging-details/active - Fetching all active packaging details");
        List<ListingPackagingDetailsDto> activeDetails = packagingDetailsService.getAllActivePackagingDetails();
        logger.debug("Found {} active packaging details entries.", activeDetails.size());
        return ResponseEntity.ok(activeDetails);
    }

    // New endpoint to get all inactive packaging details (Admin only)
    @GetMapping("/packaging-details/inactive")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ListingPackagingDetailsDto>> getAllInactivePackagingDetails() {
        logger.info("GET /api/v1/listings/packaging-details/inactive - Fetching all inactive packaging details");
        List<ListingPackagingDetailsDto> inactiveDetails = packagingDetailsService.getAllInactivePackagingDetails();
        logger.debug("Found {} inactive packaging details entries.", inactiveDetails.size());
        return ResponseEntity.ok(inactiveDetails);
    }


    @PostMapping("/{listingId}/packaging-details") // Используем POST для операции создания
    @PreAuthorize("hasRole('ADMIN') or @listingService.isOwner(#listingId, authentication.name)") // Проверка прав доступа
    public ResponseEntity<String> createListingPackagingDetails( // Тип возвращаемого значения изменен на ResponseEntity<Void>
                                                               @PathVariable Long listingId, // ID листинга из пути
                                                               @RequestBody ListingPackagingDetailsDto packagingDetailsDto) { // Данные для создания деталей

        logger.info("Received POST request to create packaging details for listing ID: {}", listingId);

        try {
            Optional<Listing> listingOptional = listingService.getListingById(listingId);
            if (listingOptional.isEmpty()) {
                logger.warn("Creation failed: Listing not found for ID: {}", listingId);
                throw new EntityNotFoundException("Listing not found with ID: " + listingId);
            }
            Listing listing = listingOptional.get();

            logger.debug("Listing entity found for ID: {}", listingId);

            packagingDetailsService.createPackagingDetails(listing, packagingDetailsDto); // Вызываем сервис, возвращаемое значение не используем для тела ответа

            logger.info("Packaging details created successfully for listing ID: {}", listingId);
            // 3. Возвращаем только статус 201 Created без тела ответа
            return new ResponseEntity<>(HttpStatus.CREATED); // Или ResponseEntity.status(HttpStatus.CREATED).build();

        } catch (EntityNotFoundException e) {
            logger.warn("Creation failed: Listing not found. Error: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalStateException e) {
            logger.warn("Creation failed: Packaging details already exist. Error: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT); // Возвращаем статус и сообщение
        } catch (IllegalArgumentException e) {
            logger.warn("Creation failed: Invalid argument. Error: {}", e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST); // Возвращаем статус и сообщение
        } catch (Exception e) {
            logger.error("Error creating packaging details for listing ID {}: {}", listingId, e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR); // Возвращаем статус и сообщение
        }
    }



    @PutMapping("/{listingId}/packaging-details")
    @PreAuthorize("hasRole('ADMIN') or @listingService.isOwner(#listingId, authentication.name)")
    public ResponseEntity<ListingPackagingDetails> updateListingPackagingDetails(
            @PathVariable Long listingId,
            @RequestBody ListingPackagingDetailsDto packagingDetailsDto) {
        logger.info("PUT /api/v1/listings/{}/packaging-details - Updating packaging details for listing", listingId);
        try {
            ListingPackagingDetails updatedDetails = packagingDetailsService.updatePackagingDetails(listingId, packagingDetailsDto);
            logger.info("Packaging details updated successfully for listing {}.", listingId);
            return ResponseEntity.ok(updatedDetails);

        } catch (EntityNotFoundException e) {
            logger.warn("Update failed: Packaging details not found for listing {}.", listingId);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error updating packaging details for listing {}: {}", listingId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{listingId}/packaging-details")
    @PreAuthorize("hasRole('ADMIN') or @listingService.isOwner(#listingId, authentication.name)")
    public ResponseEntity<Void> deleteListingPackagingDetails(@PathVariable Long listingId) {
        logger.info("DELETE /api/v1/listings/{}/packaging-details - Deleting packaging details for listing", listingId);
        try {
            packagingDetailsService.deletePackagingDetails(listingId);
            logger.info("Packaging details deleted successfully for listing {}.", listingId);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            logger.warn("Deletion failed: Packaging details not found for listing {}.", listingId);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error deleting packaging details for listing {}: {}", listingId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{listingId}/packaging-details/exists")
    public ResponseEntity<Boolean> packagingDetailsExists(@PathVariable Long listingId) {
        logger.info("GET /api/v1/listings/{}/packaging-details/exists - Checking if packaging details exist for listing", listingId);
        boolean exists = packagingDetailsService.existsByListingId(listingId);
        logger.debug("Packaging details exist for listing {}: {}", listingId, exists);
        return ResponseEntity.ok(exists);
    }
}