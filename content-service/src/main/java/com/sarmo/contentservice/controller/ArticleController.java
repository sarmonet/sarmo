package com.sarmo.contentservice.controller;

import com.sarmo.contentservice.dto.ArticleDTO;
import com.sarmo.contentservice.entity.Article;
import com.sarmo.contentservice.service.ArticleService;
import com.sarmo.contentservice.dto.ArticleCreateDTO;
import com.sarmo.contentservice.dto.ArticleUpdateDTO;
import com.sarmo.contentservice.exception.ResourceNotFoundException;
import com.sarmo.contentservice.exception.UnauthorizedActionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/content/article")
@PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
public class ArticleController {

    private static final Logger logger = LoggerFactory.getLogger(ArticleController.class);

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping
    public ResponseEntity<List<Article>> getAllArticles() {
        try {
            List<Article> articles = articleService.getAllArticles();
            return ResponseEntity.ok(articles);
        } catch (Exception e) {
            logger.error("Error getting all articles: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Article> getArticleById(@PathVariable Long id) {
        try {
            Optional<Article> article = articleService.getArticleById(id);
            // TODO: Best practice: Map the Article entity to ArticleFullDTO before returning to avoid lazy loading issues
            return article.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
        } catch (ResourceNotFoundException e) {
            logger.warn("Article not found with ID: {}", id);
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
        catch (Exception e) {
            logger.error("Error getting article with ID: {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    // Authorization check for author is done in the service
    public ResponseEntity<Article> createArticle(@RequestBody ArticleCreateDTO articleCreateDTO) {
        try {
            // Get authenticated user ID using getCurrentUserId()
            Long authorId = getCurrentUserId();

            Article createdArticle = articleService.createArticle(articleCreateDTO, authorId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdArticle); // 201 Created
        } catch (ResourceNotFoundException e) {
            logger.warn("Author not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (IllegalStateException e) {
            logger.error("Authentication error in createArticle: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 401 Unauthorized
        }
        catch (Exception e) {
            logger.error("Error creating article: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<Article> updateArticle(@PathVariable Long id, @RequestBody ArticleUpdateDTO articleUpdateDTO) {
        try {
            Long authenticatedUserId = getCurrentUserId();

            Article updatedArticle = articleService.updateArticle(id, articleUpdateDTO, authenticatedUserId);
            return ResponseEntity.ok(updatedArticle); // 200 OK
        } catch (ResourceNotFoundException e) {
            logger.warn("Article not found for update with ID: {}", id);
            return ResponseEntity.notFound().build(); // 404 Not Found
        } catch (UnauthorizedActionException e) {
            logger.warn("Unauthorized attempt to update article {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403 Forbidden
        } catch (IllegalStateException e) {
            logger.error("Authentication error in updateArticle: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 401 Unauthorized
        }
        catch (Exception e) {
            logger.error("Error updating article with ID: {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable Long id) {
        try {
            Long authenticatedUserId = getCurrentUserId();

            articleService.deleteArticle(id, authenticatedUserId);
            logger.info("Article with ID: {} deleted successfully by user {}", id, authenticatedUserId);
            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (ResourceNotFoundException e) {
            logger.warn("Article not found for deletion with ID: {}", id);
            return ResponseEntity.notFound().build(); // 404 Not Found
        } catch (UnauthorizedActionException e) {
            logger.warn("Unauthorized attempt to delete article {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403 Forbidden
        } catch (IllegalStateException e) {
            // Catch exceptions from getCurrentUserId if user is not authenticated as expected
            logger.error("Authentication error in deleteArticle: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 401 Unauthorized
        }
        catch (Exception e) {
            logger.error("Error deleting article with ID: {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication.getPrincipal() instanceof String && authentication.getPrincipal().equals("anonymousUser"))) {

            try {
                return Long.parseLong(authentication.getName());
            } catch (NumberFormatException e) {
                logger.error("Principal name is not a valid user ID (Long): {}", authentication.getName(), e);
                throw new IllegalStateException("Authenticated principal's name is not a valid user ID format");
            }
        }
        logger.error("getCurrentUserId called but user is not authenticated or principal is anonymous/unexpected type");
        throw new IllegalStateException("User is not authenticated or principal is not as expected");
    }

}