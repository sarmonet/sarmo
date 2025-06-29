package com.sarmo.contentservice.controller;

import com.sarmo.contentservice.dto.ArticleCreateDTO;
import com.sarmo.contentservice.dto.ArticleFullDTO;
import com.sarmo.contentservice.dto.NewsCreateDTO;
import com.sarmo.contentservice.dto.NewsFullDTO;
import com.sarmo.contentservice.service.ContentFullService; // Assuming this service exists
import com.sarmo.contentservice.service.RedisViewCountService; // Assuming this service exists
import com.sarmo.contentservice.exception.ResourceNotFoundException; // Import custom exception

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication; // Needed for getCurrentUserId method
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Import the new DTOs for updates
import com.sarmo.contentservice.dto.NewsUpdateFullDTO;
import com.sarmo.contentservice.dto.ArticleUpdateFullDTO;


@RestController
@RequestMapping("/api/v1/content/full")
public class ContentFullController {

    private static final Logger logger = LoggerFactory.getLogger(ContentFullController.class);

    private final ContentFullService contentFullService;
    private final RedisViewCountService redisViewCountService;

    public ContentFullController(ContentFullService contentFullService, RedisViewCountService redisViewCountService) {
        this.contentFullService = contentFullService;
        this.redisViewCountService = redisViewCountService;
    }

    @GetMapping("/news/{id}")
    public ResponseEntity<NewsFullDTO> getFullNewsInfo(@PathVariable Long id) {
        try {
            NewsFullDTO newsFullDTO = contentFullService.getFullNewsInfo(id);
            if (newsFullDTO != null) {
                redisViewCountService.incrementNewsViewCount(id);
                return ResponseEntity.ok(newsFullDTO);
            } else {
                logger.warn("Full news info not found for ID: {}", id);
                return ResponseEntity.notFound().build();
            }
        } catch (ResourceNotFoundException e) {
            logger.warn("Full news info not found for ID: {}", id);
            return ResponseEntity.notFound().build();
        }
        catch (Exception e) {
            logger.error("Error getting full news info for ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/article/{id}")
    @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
    public ResponseEntity<ArticleFullDTO> getFullArticleInfo(@PathVariable Long id) {
        try {
            ArticleFullDTO articleFullDTO = contentFullService.getFullArticleInfo(id);
            if (articleFullDTO != null) {
                redisViewCountService.incrementArticleViewCount(id);
                return ResponseEntity.ok(articleFullDTO);
            } else {
                logger.warn("Full article info not found for ID: {}", id);
                return ResponseEntity.notFound().build();
            }
        } catch (ResourceNotFoundException e) {
            logger.warn("Full article info not found for ID: {}", id);
            return ResponseEntity.notFound().build();
        }
        catch (Exception e) {
            logger.error("Error getting full article info for ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/news")
    @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')") // Adjust roles as needed
    public ResponseEntity<NewsFullDTO> createNewsWithContent(@RequestBody NewsCreateDTO newsCreateDTO) {
        try {
            Long userId = getCurrentUserId();

            NewsFullDTO newsFullDTO = contentFullService.createNewsWithContent(newsCreateDTO, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(newsFullDTO);
        } catch (IllegalStateException e) {
            logger.error("Authentication error in createNewsWithContent: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (ResourceNotFoundException e) {
            logger.warn("Resource not found during news creation: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        catch (Exception e) {
            logger.error("Error creating news with content: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/articles")
    @PreAuthorize("isAuthenticated() and hasAnyRole('MODERATOR', 'ADMIN')") // Adjust roles as needed
    public ResponseEntity<ArticleFullDTO> createArticleWithContent(@RequestBody ArticleCreateDTO articleCreateDTO) {
        try {
            Long userId = getCurrentUserId();

            ArticleFullDTO articleFullDTO = contentFullService.createArticleWithContent(articleCreateDTO, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(articleFullDTO);
        } catch (IllegalStateException e) {
            logger.error("Authentication error in createArticleWithContent: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (ResourceNotFoundException e) {
            logger.warn("Resource not found during article creation: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        catch (Exception e) {
            logger.error("Error creating article with content: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PutMapping("/news/{id}")
    @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
    public ResponseEntity<NewsFullDTO> updateNews(@PathVariable Long id, @RequestBody NewsUpdateFullDTO newsUpdateFullDTO) {
        try {

            NewsFullDTO updatedNews = contentFullService.updateNews(id, newsUpdateFullDTO);
            return ResponseEntity.ok(updatedNews);
        } catch (ResourceNotFoundException e) {
            logger.warn("News with ID: {} not found for update. {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error updating news with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/articles/{id}")
    @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
    public ResponseEntity<ArticleFullDTO> updateArticle(@PathVariable Long id, @RequestBody ArticleUpdateFullDTO articleUpdateFullDTO) {
        try {

            ArticleFullDTO updatedArticle = contentFullService.updateArticle(id, articleUpdateFullDTO);
            return ResponseEntity.ok(updatedArticle);
        } catch (ResourceNotFoundException e) {
            logger.warn("Article with ID: {} not found for update. {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error updating article with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || (authentication.getPrincipal() instanceof String && authentication.getPrincipal().equals("anonymousUser"))) {
            logger.error("getCurrentUserId called but user is not authenticated or principal is anonymous/unexpected type");
            throw new IllegalStateException("User is not authenticated or principal is not as expected");
        }

        try {
            return Long.parseLong(authentication.getName());
        } catch (NumberFormatException e) {
            logger.error("Principal name is not a valid user ID (Long): {}", authentication.getName(), e);
            throw new IllegalStateException("Authenticated principal's name is not a valid user ID format");
        }
    }
}