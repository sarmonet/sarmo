package com.sarmo.listingservice.controller;

import com.sarmo.listingservice.dto.PackagingServiceInfoRequestDto; // Import Request DTO
import com.sarmo.listingservice.dto.PackagingServiceInfoResponseDto; // Import Response DTO
import com.sarmo.listingservice.service.PackagingServiceInfoService; // Import Service

import jakarta.persistence.EntityNotFoundException; // Import Exception
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Import for Security
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/listing/packaging-config") // Base path for administrative packaging configuration
@PreAuthorize("hasRole('ADMIN')")
public class PackagingServiceInfoController {

    private static final Logger logger = LoggerFactory.getLogger(PackagingServiceInfoController.class);

    private final PackagingServiceInfoService packagingServiceInfoService;

    public PackagingServiceInfoController(PackagingServiceInfoService packagingServiceInfoService) {
        this.packagingServiceInfoService = packagingServiceInfoService;
    }

    // GET /api/v1/listing/packaging-config - Get the current packaging service info configuration
    @GetMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<PackagingServiceInfoResponseDto> getPackagingConfig() {
        logger.info("GET /api/v1/listing/packaging-config - Fetching packaging service info configuration");
        PackagingServiceInfoResponseDto configDto = packagingServiceInfoService.getPackagingServiceInfo();

        if (configDto != null) {
            logger.debug("Packaging service info configuration found.");
            return ResponseEntity.ok(configDto);
        } else {
            logger.warn("No packaging service info configuration found.");
            // Can return 404 if the configuration is expected to always exist
            return ResponseEntity.notFound().build();
        }
    }

    // POST /api/v1/listing/packaging-config - Create a new packaging service info configuration
    // This endpoint should typically be used only once to create the initial entry.
    @PostMapping
    public ResponseEntity<PackagingServiceInfoResponseDto> createPackagingConfig(@RequestBody PackagingServiceInfoRequestDto requestDto) {
        logger.info("POST /api/v1/listing/packaging-config - Creating new packaging service info configuration");
        try {
            PackagingServiceInfoResponseDto createdConfig = packagingServiceInfoService.createPackagingServiceInfo(requestDto);
            logger.info("Packaging service info configuration created with ID: {}", createdConfig.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdConfig); // Return 201 Created

        } catch (IllegalStateException e) {
            logger.warn("Creation failed: Configuration already exists. {}", e.getMessage());
            // Return 409 Conflict if the configuration already exists
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        } catch (Exception e) {
            logger.error("Error creating packaging service info configuration: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Handle other errors
        }
    }

    // PUT /api/v1/listing/packaging-config/{configId} - Update an existing configuration
    @PutMapping("/{configId}")
    public ResponseEntity<PackagingServiceInfoResponseDto> updatePackagingConfig(
            @PathVariable Long configId,
            @RequestBody PackagingServiceInfoRequestDto requestDto) {
        logger.info("PUT /api/v1/listing/packaging-config/{} - Updating packaging service info configuration", configId);
        try {
            PackagingServiceInfoResponseDto updatedConfig = packagingServiceInfoService.updatePackagingServiceInfo(configId, requestDto);
            logger.info("Packaging service info configuration updated successfully with ID: {}", updatedConfig.getId());
            return ResponseEntity.ok(updatedConfig); // Return 200 OK with updated DTO

        } catch (EntityNotFoundException e) {
            logger.warn("Update failed: Configuration with ID {} not found. {}", configId, e.getMessage());
            return ResponseEntity.notFound().build(); // Return 404 Not Found
        } catch (Exception e) {
            logger.error("Error updating packaging service info configuration with ID {}: {}", configId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Handle other errors
        }
    }

    // DELETE /api/v1/listing/packaging-config/{configId} - Delete a configuration
    @DeleteMapping("/{configId}")
    public ResponseEntity<Void> deletePackagingConfig(@PathVariable Long configId) {
        logger.info("DELETE /api/v1/listing/packaging-config/{} - Deleting packaging service info configuration", configId);
        try {
            packagingServiceInfoService.deletePackagingServiceInfo(configId);
            logger.info("Packaging service info configuration deleted successfully with ID: {}", configId);
            return ResponseEntity.noContent().build(); // Return 204 No Content

        } catch (EntityNotFoundException e) {
            logger.warn("Deletion failed: Configuration with ID {} not found. {}", configId, e.getMessage());
            return ResponseEntity.notFound().build(); // Return 404 Not Found
        } catch (Exception e) {
            logger.error("Error deleting packaging service info configuration with ID {}: {}", configId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Handle other errors
        }
    }

}