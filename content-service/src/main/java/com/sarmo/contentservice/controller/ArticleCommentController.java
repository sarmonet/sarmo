package com.sarmo.contentservice.controller;

import com.sarmo.contentservice.dto.ArticleCommentDTO;
import com.sarmo.contentservice.entity.ArticleComment; // Still used for GET return types
import com.sarmo.contentservice.service.ArticleCommentService;
import com.sarmo.contentservice.dto.ArticleCommentCreateDTO; // Import create DTO
import com.sarmo.contentservice.dto.ArticleCommentUpdateDTO; // Import update DTO
import com.sarmo.contentservice.exception.ResourceNotFoundException; // Import custom exceptions
import com.sarmo.contentservice.exception.UnauthorizedActionException; // Import custom exceptions
import com.sarmo.contentservice.jwt.JwtTokenDataExtractor; // Your JWT extractor

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication; // Needed to access authentication.name in SpEL
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/content/article")
public class ArticleCommentController {

    private static final Logger logger = LoggerFactory.getLogger(ArticleCommentController.class);

    private final ArticleCommentService commentService;
    private final JwtTokenDataExtractor jwtTokenDataExtractor;

    public ArticleCommentController(ArticleCommentService commentService, JwtTokenDataExtractor jwtTokenDataExtractor) {
        this.commentService = commentService;
        this.jwtTokenDataExtractor = jwtTokenDataExtractor;
    }

    @GetMapping("/comment")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<List<ArticleCommentDTO>> getAllComments() {
        try {
            List<ArticleCommentDTO> comments = commentService.getAllComments();
            return ResponseEntity.ok(comments);
        } catch (Exception e) {
            logger.error("Error getting all comments: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/comment/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<ArticleCommentDTO> getCommentById(@PathVariable Long id) {
        try {
            Optional<ArticleCommentDTO> comment = commentService.getCommentById(id);
            // TODO: Best practice: Map the ArticleComment entity to ArticleCommentDTO before returning
            return comment.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
        } catch (ResourceNotFoundException e) { // Handle NotFoundException from service
            logger.warn("Comment not found with ID: {}", id);
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
        catch (Exception e) {
            logger.error("Error getting comment with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{articleId}/comment")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<List<ArticleCommentDTO>> getCommentsByArticleId(@PathVariable Long articleId) {
        try {
            // TODO: Best practice: Map ArticleComment entities to ArticleCommentDTOs before returning
            List<ArticleCommentDTO> comments = commentService.getCommentsByArticleId(articleId); // Use updated service method name
            return new ResponseEntity<>(comments, HttpStatus.OK); // 200 OK
        } catch (Exception e) {
            logger.error("Error getting comments by articleID {}: {}", articleId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/comment")
    // Keeping your existing PreAuthorize annotation
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')") // Consider allowing 'USER' role here?
    // Accept ArticleCommentCreateDTO
    // Keep @RequestHeader("Authorization") to get the token for extraction
    public ResponseEntity<ArticleComment> createComment(@RequestBody ArticleCommentCreateDTO commentCreateDTO, @RequestHeader("Authorization") String token) {
        try {
            // Extract user ID using your JwtTokenDataExtractor
            Long authorId = jwtTokenDataExtractor.extractUserIdFromToken(token); // <-- Use your extractor

            // Call the updated service method with DTO and authorId
            ArticleComment createdComment = commentService.createComment(commentCreateDTO, authorId);
            // TODO: Best practice: Map the created ArticleComment entity to a DTO before returning
            return ResponseEntity.status(HttpStatus.CREATED).body(createdComment); // 201 Created
        } catch (ResourceNotFoundException e) {
            logger.warn("Resource not found during comment creation: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // 400 Bad Request (e.g., author not found)
        } catch (Exception e) { // Catch any other exceptions during token extraction or service call
            logger.error("Error creating comment: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PutMapping("/comment/{id}")
    @PreAuthorize("isAuthenticated() and (@articleCommentService.isAuthor(#id, authentication.name) or hasAnyRole('ADMIN', 'MODERATOR'))")
    public ResponseEntity<ArticleComment> updateComment(@PathVariable Long id, @RequestBody ArticleCommentUpdateDTO commentUpdateDTO) {
        try {
            // Get authenticated user ID (needed by service for its authorization check)
            // Using SecurityContextHolder as in ArticleController, assuming principal name is user ID string
            Long authenticatedUserId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());

            // Call the updated service method with ID, DTO, and authenticated user ID
            ArticleComment updatedComment = commentService.updateComment(id, commentUpdateDTO, authenticatedUserId);
            // TODO: Best practice: Map the updated ArticleComment entity to a DTO before returning
            return ResponseEntity.ok(updatedComment); // 200 OK
        } catch (ResourceNotFoundException e) {
            logger.warn("Comment not found for update with ID: {}", id);
            return ResponseEntity.notFound().build(); // 404 Not Found
        } catch (UnauthorizedActionException e) {
            logger.warn("Unauthorized attempt to update comment {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403 Forbidden
        } catch (Exception e) { // Catch any other exceptions (e.g., NumberFormatException from principal name)
            logger.error("Error updating comment with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/comment/{id}")
    @PreAuthorize("isAuthenticated() and (@articleCommentService.isAuthor(#id, authentication.name) or hasAnyRole('ADMIN', 'MODERATOR'))")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        try {
            Long authenticatedUserId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());

            // Call the updated service method with ID and authenticated user ID
            commentService.deleteComment(id, authenticatedUserId);
            logger.info("Comment with ID {} deleted successfully by user {}", id, authenticatedUserId);
            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (ResourceNotFoundException e) {
            logger.warn("Comment not found for deletion with ID: {}", id);
            return ResponseEntity.notFound().build(); // 404 Not Found
        } catch (UnauthorizedActionException e) {
            logger.warn("Unauthorized attempt to delete comment {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403 Forbidden
        } catch (Exception e) { // Catch any other exceptions (e.g., NumberFormatException from principal name)
            logger.error("Error deleting comment with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}