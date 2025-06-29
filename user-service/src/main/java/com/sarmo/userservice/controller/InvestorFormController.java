package com.sarmo.userservice.controller;

import com.sarmo.userservice.dto.InvestorFormRequestDto;
import com.sarmo.userservice.dto.InvestorFormResponseDto;
import com.sarmo.userservice.service.interfaces.InvestorFormService; // Import the service interface
import jakarta.persistence.EntityNotFoundException; // Import exception type for handling
import org.slf4j.Logger;
import org.slf4j.LoggerFactory; // Import LoggerFactory
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Import PreAuthorize
import org.springframework.security.core.Authentication; // Import Authentication
import org.springframework.security.core.context.SecurityContextHolder; // Import SecurityContextHolder
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user/investor-form") // Base path for investor form operations
public class InvestorFormController {

    private static final Logger logger = LoggerFactory.getLogger(InvestorFormController.class); // Logger instance

    private final InvestorFormService investorFormService;

    // Inject the service
    public InvestorFormController(InvestorFormService investorFormService) {
        this.investorFormService = investorFormService;
    }

    // --- Endpoints for Current Authenticated User (/me) ---
    // Authenticated users can manage their own investor form.

    @PostMapping("/me") // Create or Update form for the current user
    @PreAuthorize("isAuthenticated()") // Requires the user to be authenticated
    public ResponseEntity<InvestorFormResponseDto> saveInvestorFormForCurrentUser(
            @RequestBody InvestorFormRequestDto requestDto) {
        logger.info("Received POST /me request for investor form");
        Long userId = getCurrentUserId(); // Get user ID from the authenticated principal

        // The service method handles whether it's a create or update based on existence.
        // We return CREATED status for a successful operation via POST.
        InvestorFormResponseDto responseDto = investorFormService.saveOrUpdateInvestorForm(userId, requestDto);

        logger.info("Investor form processed (save/update) for user ID: {}", userId);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED); // 201 CREATED for successful save/update
    }

    @PutMapping("/me") // Update form for the current user
    @PreAuthorize("isAuthenticated()") // Requires the user to be authenticated
    public ResponseEntity<InvestorFormResponseDto> updateInvestorFormForCurrentUser(
            @RequestBody InvestorFormRequestDto requestDto) {
        logger.info("Received PUT /me request for investor form");
        Long userId = getCurrentUserId(); // Get user ID from the authenticated principal

        // The service method handles the update logic.
        InvestorFormResponseDto responseDto = investorFormService.saveOrUpdateInvestorForm(userId, requestDto);

        logger.info("Investor form updated for user ID: {}", userId);
        return new ResponseEntity<>(responseDto, HttpStatus.OK); // 200 OK for successful update
    }


    @GetMapping("/me") // Get form for the current user
    @PreAuthorize("isAuthenticated()") // Requires the user to be authenticated
    public ResponseEntity<InvestorFormResponseDto> getInvestorFormForCurrentUser() {
        logger.info("Received GET /me request for investor form");
        Long userId = getCurrentUserId(); // Get user ID from the authenticated principal

        InvestorFormResponseDto responseDto = investorFormService.getInvestorFormByUserId(userId);

        if (responseDto != null) {
            logger.info("Investor form found for user ID: {}", userId);
            return new ResponseEntity<>(responseDto, HttpStatus.OK); // 200 OK with the form data
        } else {
            logger.warn("Investor form not found for user ID: {}", userId);
            // Service returned null, indicating the form doesn't exist for this user.
            // Map this to a 404 Not Found response.
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 NOT FOUND
        }
    }

    @DeleteMapping("/me") // Delete form for the current user
    @PreAuthorize("isAuthenticated()") // Requires the user to be authenticated
    public ResponseEntity<Void> deleteInvestorFormForCurrentUser() {
        logger.info("Received DELETE /me request for investor form");
        Long userId = getCurrentUserId(); // Get user ID from the authenticated principal

        // The service handles the deletion logic. It might log a warning if not found.
        // If the service were configured to throw EntityNotFoundException on delete-not-found,
        // the @ExceptionHandler below would catch it.
        investorFormService.deleteInvestorFormByUserId(userId);

        logger.info("Investor form deletion requested for user ID: {}", userId);
        // A 204 No Content response indicates successful deletion without a response body.
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 NO CONTENT
    }


    // --- Endpoints for Admin (Explicit User ID) ---
    // Admin users can manage investor forms for any user.

    @PostMapping("/by-user/{userId}") // Save or Update form for a specific user (Admin)
    @PreAuthorize("hasRole('ADMIN')") // Restricted to users with the ADMIN role
    public ResponseEntity<InvestorFormResponseDto> saveInvestorFormByUserId(
            @PathVariable Long userId, // User ID from the path
            @RequestBody InvestorFormRequestDto requestDto) {
        logger.info("Received POST /by-user/{} request for investor form (Admin)", userId);

        // Call the service method, passing the user ID from the path and the request DTO.
        // The service handles creation vs update.
        InvestorFormResponseDto responseDto = investorFormService.saveOrUpdateInvestorForm(userId, requestDto);

        logger.info("Investor form processed (save/update) for user ID: {} (Admin)", userId);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED); // 201 CREATED
    }

    @PutMapping("/by-user/{userId}") // Update form for a specific user (Admin)
    @PreAuthorize("hasRole('ADMIN')") // Restricted to users with the ADMIN role
    public ResponseEntity<InvestorFormResponseDto> updateInvestorFormByUserId(
            @PathVariable Long userId, // User ID from the path
            @RequestBody InvestorFormRequestDto requestDto) {
        logger.info("Received PUT /by-user/{} request for investor form (Admin)", userId);

        // Call the service method to update the form for the specified user.
        InvestorFormResponseDto responseDto = investorFormService.saveOrUpdateInvestorForm(userId, requestDto);

        logger.info("Investor form updated for user ID: {} (Admin)", userId);
        return new ResponseEntity<>(responseDto, HttpStatus.OK); // 200 OK
    }


    @GetMapping("/by-user/{userId}") // Get form for a specific user (Admin)
    @PreAuthorize("hasRole('ADMIN')") // Restricted to users with the ADMIN role
    public ResponseEntity<InvestorFormResponseDto> getInvestorFormByUserId(@PathVariable Long userId) { // User ID from the path
        logger.info("Received GET /by-user/{} request for investor form (Admin)", userId);

        InvestorFormResponseDto responseDto = investorFormService.getInvestorFormByUserId(userId);

        if (responseDto != null) {
            logger.info("Investor form found for user ID: {} (Admin)", userId);
            return new ResponseEntity<>(responseDto, HttpStatus.OK); // 200 OK with data
        } else {
            logger.warn("Investor form not found for user ID: {} (Admin)", userId);
            // Service returned null, map to 404 Not Found
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 NOT FOUND
        }
    }

    @DeleteMapping("/by-user/{userId}") // Delete form for a specific user (Admin)
    @PreAuthorize("hasRole('ADMIN')") // Restricted to users with the ADMIN role
    public ResponseEntity<Void> deleteInvestorFormByUserId(@PathVariable Long userId) { // User ID from the path
        logger.info("Received DELETE /by-user/{} request for investor form (Admin)", userId);

        // Service handles deletion. It might log a warning if not found.
        // If the service were configured to throw EntityNotFoundException on delete-not-found,
        // the @ExceptionHandler below would catch it.
        investorFormService.deleteInvestorFormByUserId(userId);

        logger.info("Investor form deleted for user ID: {} (Admin)", userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 NO CONTENT
    }

    // --- Endpoints for Admin (All Forms) ---

    @GetMapping("/all") // Get all investor forms (Admin)
    @PreAuthorize("hasRole('ADMIN')") // Restricted to users with the ADMIN role
    public ResponseEntity<List<InvestorFormResponseDto>> getAllInvestorForms() {
        logger.info("Received GET /all request for investor forms (Admin)");

        List<InvestorFormResponseDto> responseDtos = investorFormService.getAllInvestorForms();

        logger.info("Returning {} investor forms (Admin)", responseDtos.size());
        return new ResponseEntity<>(responseDtos, HttpStatus.OK); // 200 OK with list of forms
    }


    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> handleEntityNotFoundException(EntityNotFoundException ex) {
        logger.warn("Entity not found exception occurred: {}", ex.getMessage());
        // Return 404 Not Found status with the exception message in the body
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    // --- Helper method to get the current authenticated user's ID ---
    // (Copied from previous controllers for consistency)
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication.getPrincipal() instanceof String && authentication.getPrincipal().equals("anonymousUser"))) {
            try {
                // Assuming the principal name (username) is the user ID as a String
                return Long.parseLong(authentication.getName());
            } catch (NumberFormatException e) {
                // This indicates a misconfiguration in the security setup if an authenticated principal's name isn't the user ID
                logger.error("Principal name is not a valid user ID (Long): {}", authentication.getName(), e);
                throw new IllegalStateException("Authenticated principal's name is not a valid user ID format");
            }
        }
        // This should ideally not be reached if @PreAuthorize("isAuthenticated()") is used correctly
        logger.error("getCurrentUserId called but user is not authenticated or principal is anonymous");
        throw new IllegalStateException("User is not authenticated or principal is not as expected");
    }

}