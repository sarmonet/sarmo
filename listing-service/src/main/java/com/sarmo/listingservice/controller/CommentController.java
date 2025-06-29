package com.sarmo.listingservice.controller;

import com.sarmo.listingservice.dto.CommentDTO;
import com.sarmo.listingservice.dto.CreateCommentDto;
import com.sarmo.listingservice.dto.UpdateCommentDto;
import com.sarmo.listingservice.service.CommentService; // Service with comment business logic and isCommentOwner check
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import org.springframework.security.access.prepost.PreAuthorize; // Import for method security
import org.springframework.security.core.Authentication; // Import for getting auth object
import org.springframework.security.core.context.SecurityContextHolder; // Import for getting security context


@RestController
@RequestMapping("/api/v1/listing/comment") // Base path for comment controller
public class CommentController {

    private final CommentService commentService;
    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    // Update the constructor
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    // POST /api/v1/listing/comment/ - Add a comment
    // Accessible only to authenticated users
    @PostMapping() // Keep trailing slash or remove consistently. RequestMapping("/api/v1/listing/comment") + PostMapping("/") = /api/v1/listing/comment/
    @PreAuthorize("isAuthenticated()") // Only authenticated users can add comments
    public ResponseEntity<CommentDTO> addComment(
            @RequestBody CreateCommentDto createCommentDto) { // Remove authorizationHeader

        logger.info("POST /api/v1/listing/comment/ - Adding comment");

        // Get user ID from SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userIdString = authentication.getName(); // Subject from JWT as String
        Long userId = Long.parseLong(userIdString); // Convert to Long

        // Call service method, passing the user ID
        CommentDTO addedComment = commentService.addComment(createCommentDto, userId); // Assuming addComment method accepts userId
        logger.info("POST /api/v1/listing/comment/ - Comment added with id {} by user id {}", addedComment.getId(), userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(addedComment);
    }

    // PUT /api/v1/listing/comment/{commentId} - Update a comment
    // Accessible only to Admin OR the comment owner
    @PutMapping("/{commentId}")
    @PreAuthorize("hasRole('ADMIN') or @commentService.isCommentOwner(#commentId, authentication.name)") // Access check
    public ResponseEntity<CommentDTO> updateComment(
            @PathVariable Long commentId,
            @RequestBody UpdateCommentDto updateCommentDto) { // Remove authorizationHeader

        logger.info("PUT /api/v1/listing/comment/{} - Updating comment by id", commentId);

        // Get user ID from SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userIdString = authentication.getName(); // Subject from JWT as String
        Long userId = Long.parseLong(userIdString); // Convert to Long

        // Call service method, passing the user ID
        CommentDTO updatedComment = commentService.updateComment(commentId, updateCommentDto, userId);
        logger.info("PUT /api/v1/listing/comment/{} - Comment updated by user id {}", commentId, userId);
        return ResponseEntity.ok(updatedComment);
    }

    // DELETE /api/v1/listing/comment/{commentId} - Delete a comment
    // Accessible only to Admin OR the comment owner
    @DeleteMapping("/{commentId}")
    @PreAuthorize("hasRole('ADMIN') or @commentService.isCommentOwner(#commentId, authentication.name)") // Access check
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId) { // Remove authorizationHeader

        logger.info("DELETE /api/v1/listing/comment/{} - Deleting comment by id", commentId);

        // Get user ID from SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userIdString = authentication.getName(); // Subject from JWT as String
        Long userId = Long.parseLong(userIdString); // Convert to Long

        // Call service method, passing the user ID
        // Assuming commentService.deleteComment method accepts userId to verify ownership inside (or trust PreAuthorize)
        commentService.deleteComment(commentId, userId);
        logger.info("DELETE /api/v1/listing/comment/{} - Comment deleted by user id {}", commentId, userId);
        return ResponseEntity.noContent().build();
    }

    // GET /api/v1/listing/comment/listing/{listingId} - Get comments for a listing
    // Accessible to everyone (if URL rule is permitAll())
    @GetMapping("/listing/{listingId}")
    // @PreAuthorize("permitAll()") // Optional: Add for explicit public access
    public ResponseEntity<List<CommentDTO>> getCommentsForListing(@PathVariable Long listingId) {
        logger.info("GET /api/v1/listing/comment/listing/{} - Getting comments for listingId", listingId);
        List<CommentDTO> comments = commentService.getCommentsForListing(listingId);
        logger.info("GET /api/v1/listing/comment/listing/{} - Found {} comments", listingId, comments.size());
        return ResponseEntity.ok(comments);
    }

    // GET /api/v1/listing/comment/replies/{parentCommentId} - Get replies to a comment
    // Accessible to everyone (if URL rule is permitAll())
    @GetMapping("/replies/{parentCommentId}")
    // @PreAuthorize("permitAll()") // Optional: Add for explicit public access
    public ResponseEntity<List<CommentDTO>> getReplies(@PathVariable Long parentCommentId) {
        logger.info("GET /api/v1/listing/comment/replies/{} - Getting replies for parentCommentId", parentCommentId);
        List<CommentDTO> replies = commentService.getReplies(parentCommentId);
        logger.info("GET /api/v1/listing/comment/replies/{} - Found {} replies", parentCommentId, replies.size());
        return ResponseEntity.ok(replies);
    }
}