package com.sarmo.contentservice.controller;

import com.sarmo.contentservice.dto.NewsCommentDTO;
import com.sarmo.contentservice.entity.NewsComment; // Still used for GET return types
import com.sarmo.contentservice.service.NewsCommentService;
import com.sarmo.contentservice.dto.NewsCommentCreateDTO; // Import create DTO
import com.sarmo.contentservice.dto.NewsCommentUpdateDTO; // Import update DTO
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
import org.springframework.security.core.context.SecurityContextHolder; // Needed for getting user ID from context

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/content/news")
public class NewsCommentController {

    private static final Logger logger = LoggerFactory.getLogger(NewsCommentController.class);

    private final NewsCommentService commentService;
    private final JwtTokenDataExtractor jwtTokenDataExtractor; // Your injected JWT extractor

    public NewsCommentController(NewsCommentService commentService, JwtTokenDataExtractor jwtTokenDataExtractor) {
        this.commentService = commentService;
        this.jwtTokenDataExtractor = jwtTokenDataExtractor;
    }

    @GetMapping("/comment")
    public ResponseEntity<List<NewsCommentDTO>> getAllComments() {
        try {
            List<NewsCommentDTO> comments = commentService.getAllComments();
            // TODO: Best practice: Map NewsComment entities to NewsCommentDTOs before returning
            return ResponseEntity.ok(comments);
        } catch (Exception e) {
            logger.error("Error getting all news comments: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/comment/{id}")
    public ResponseEntity<NewsCommentDTO> getCommentById(@PathVariable Long id) {
        try {
            Optional<NewsCommentDTO> comment = commentService.getCommentById(id);
            // TODO: Best practice: Map the NewsComment entity to NewsCommentDTO before returning
            return comment.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
        } catch (ResourceNotFoundException e) { // Handle NotFoundException from service
            logger.warn("News comment not found with ID: {}", id);
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
        catch (Exception e) {
            logger.error("Error getting news comment with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{newsId}/comment")
    public ResponseEntity<List<NewsCommentDTO>> getCommentsByNewsId(@PathVariable Long newsId) {
        try {
            // TODO: Best practice: Map NewsComment entities to NewsCommentDTOs before returning
            List<NewsCommentDTO> comments = commentService.getCommentsByNewsId(newsId); // Use updated service method name
            return new ResponseEntity<>(comments, HttpStatus.OK); // 200 OK
        } catch (Exception e) {
            logger.error("Error getting comments by newsID {}: {}", newsId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/comment")
    public ResponseEntity<NewsComment> createComment(@RequestBody NewsCommentCreateDTO commentCreateDTO, @RequestHeader("Authorization") String token) {
        try {
            // Extract user ID using your JwtTokenDataExtractor (as in your original code)
            Long authorId = jwtTokenDataExtractor.extractUserIdFromToken(token); // <-- Use your extractor

            // Call the updated service method with DTO and authorId
            NewsComment createdComment = commentService.createComment(commentCreateDTO, authorId);
            // TODO: Best practice: Map the created NewsComment entity to a DTO before returning
            return ResponseEntity.status(HttpStatus.CREATED).body(createdComment); // 201 Created
        } catch (ResourceNotFoundException e) {
            logger.warn("Resource not found during comment creation: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // 400 Bad Request (e.g., author not found)
        } catch (Exception e) { // Catch any other exceptions during token extraction or service call
            logger.error("Error creating news comment: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/comment/{id}")
    // Обновляем PreAuthorize, чтобы передавать #id и authentication.name
    @PreAuthorize("isAuthenticated() and (@newsCommentService.isAuthor(#id, authentication.name) or hasAnyRole('ADMIN', 'MODERATOR'))")
    public ResponseEntity<NewsComment> updateComment(@PathVariable Long id, @RequestBody NewsCommentUpdateDTO commentUpdateDTO) { // Removed token/Authentication parameter
        try {
            Long authenticatedUserId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName()); // <-- Adapt based on your principal type

            // Call the updated service method with ID, DTO, and authenticated user ID
            NewsComment updatedComment = commentService.updateComment(id, commentUpdateDTO, authenticatedUserId);
            // TODO: Best practice: Map the updated NewsComment entity to a DTO before returning
            return ResponseEntity.ok(updatedComment); // 200 OK
        } catch (ResourceNotFoundException e) {
            logger.warn("News comment not found for update with ID: {}", id);
            return ResponseEntity.notFound().build(); // 404 Not Found
        } catch (UnauthorizedActionException e) {
            logger.warn("Unauthorized attempt to update news comment {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403 Forbidden
        } catch (Exception e) { // Catch any other exceptions (e.g., NumberFormatException from principal name)
            logger.error("Error updating news comment with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/comment/{id}")
    // Обновляем PreAuthorize, чтобы передавать #id и authentication.name
    @PreAuthorize("isAuthenticated() and (@newsCommentService.isAuthor(#id, authentication.name) or hasAnyRole('ADMIN', 'MODERATOR'))")
    // Get authenticated user ID for service layer authorization check
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) { // Removed token/Authentication parameter
        try {
            // Get authenticated user ID (needed by service for its authorization check)
            Long authenticatedUserId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName()); // <-- Adapt based on your principal type

            // Call the updated service method with ID and authenticated user ID
            commentService.deleteComment(id, authenticatedUserId);
            logger.info("News comment with ID {} deleted successfully by user {}", id, authenticatedUserId);
            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (ResourceNotFoundException e) {
            logger.warn("News comment not found for deletion with ID: {}", id);
            return ResponseEntity.notFound().build(); // 404 Not Found
        } catch (UnauthorizedActionException e) {
            logger.warn("Unauthorized attempt to delete news comment {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 403 Forbidden
        } catch (Exception e) { // Catch any other exceptions (e.g., NumberFormatException from principal name)
            logger.error("Error deleting news comment with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}