package com.sarmo.contentservice.controller;

import com.sarmo.contentservice.dto.NewsDTO;
import com.sarmo.contentservice.entity.News; // Still used for GET return types
import com.sarmo.contentservice.service.NewsService;
import com.sarmo.contentservice.dto.NewsCreateDTO; // Import create DTO
import com.sarmo.contentservice.dto.NewsUpdateDTO; // Import update DTO
import com.sarmo.contentservice.exception.ResourceNotFoundException; // Import custom exceptions
import com.sarmo.contentservice.exception.UnauthorizedActionException; // Import custom exceptions

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication; // Still needed for getCurrentUserId method
import org.springframework.security.core.context.SecurityContextHolder; // Import SecurityContextHolder
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/content/news")
public class NewsController {

    private static final Logger logger = LoggerFactory.getLogger(NewsController.class);

    private final NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping
    public ResponseEntity<List<News>> getAllNews() {
        try {
            List<News> newsList = newsService.getAllNews(); // Renamed variable to avoid conflict
            return ResponseEntity.ok(newsList);
        } catch (Exception e) {
            logger.error("Error getting all news: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<News> getNewsById(@PathVariable Long id) {
        try {
            Optional<News> news = newsService.getNewsById(id);
            return news.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
        } catch (ResourceNotFoundException e) {
            logger.warn("News not found with ID: {}", id);
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
        catch (Exception e) {
            logger.error("Error getting news with ID: {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<News> createNews(@RequestBody NewsCreateDTO newsCreateDTO) {
        try {
            Long authorId = getCurrentUserId();

            News createdNews = newsService.createNews(newsCreateDTO, authorId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdNews); // 201 Created
        } catch (ResourceNotFoundException e) {
            logger.warn("Author not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // 400 Bad Request if authorId from principal is invalid
        } catch (IllegalStateException e) {
            // Catch exceptions from getCurrentUserId if user is not authenticated as expected
            logger.error("Authentication error in createNews: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 401 Unauthorized
        }
        catch (Exception e) {
            logger.error("Error creating news: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    // Keeping your existing PreAuthorize annotation
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    // Accept news ID, Update DTO. Get authenticated user ID using getCurrentUserId()
    public ResponseEntity<News> updateNews(@PathVariable Long id, @RequestBody NewsUpdateDTO newsUpdateDTO) { // Removed Authentication parameter
        try {
            // Get authenticated user ID using getCurrentUserId()
            Long authenticatedUserId = getCurrentUserId(); // <-- Call the helper method

            News updatedNews = newsService.updateNews(id, newsUpdateDTO, authenticatedUserId);
            // TODO: Best practice: Map the updated News entity to a DTO before returning
            return ResponseEntity.ok(updatedNews); // 200 OK
        } catch (ResourceNotFoundException e) {
            logger.warn("News not found for update with ID: {}", id);
            return ResponseEntity.notFound().build(); // 404 Not Found
        } catch (UnauthorizedActionException e) {
            logger.warn("Unauthorized attempt to update news {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403 Forbidden
        } catch (IllegalStateException e) {
            // Catch exceptions from getCurrentUserId if user is not authenticated as expected
            logger.error("Authentication error in updateNews: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 401 Unauthorized
        }
        catch (Exception e) {
            logger.error("Error updating news with ID: {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    // Keeping your existing PreAuthorize annotation
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    // Accept news ID. Get authenticated user ID using getCurrentUserId()
    public ResponseEntity<Void> deleteNews(@PathVariable Long id) { // Removed Authentication parameter
        try {
            // Get authenticated user ID using getCurrentUserId()
            Long authenticatedUserId = getCurrentUserId(); // <-- Call the helper method

            newsService.deleteNews(id, authenticatedUserId);
            logger.info("News with ID: {} deleted successfully by user {}", id, authenticatedUserId);
            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (ResourceNotFoundException e) {
            logger.warn("News not found for deletion with ID: {}", id);
            return ResponseEntity.notFound().build(); // 404 Not Found
        } catch (UnauthorizedActionException e) {
            logger.warn("Unauthorized attempt to delete news {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403 Forbidden
        } catch (IllegalStateException e) {
            // Catch exceptions from getCurrentUserId if user is not authenticated as expected
            logger.error("Authentication error in deleteNews: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 401 Unauthorized
        }
        catch (Exception e) {
            logger.error("Error deleting news with ID: {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || (authentication.getPrincipal() instanceof String && authentication.getPrincipal().equals("anonymousUser"))) {
            // If isAuthenticated() is true but principal is anonymousUser or unexpected type
            // If isAuthenticated() false, this branch should ideally not be reachable due to @PreAuthorize
            logger.error("getCurrentUserId called but user is not authenticated or principal is anonymous/unexpected type");
            throw new IllegalStateException("User is not authenticated or principal is not as expected");
        }

        try {
            return Long.parseLong(authentication.getName());
        } catch (NumberFormatException e) {
            // Log an error if the principal name is not a valid user ID (Long)
            logger.error("Principal name is not a valid user ID (Long): {}", authentication.getName(), e);
            // Throw an exception because the principal name should be the user ID
            throw new IllegalStateException("Authenticated principal's name is not a valid user ID format");
        }
    }

}