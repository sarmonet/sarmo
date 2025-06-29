package com.sarmo.listingservice.service;

import com.sarmo.listingservice.dto.CommentDTO;
import com.sarmo.listingservice.dto.CreateCommentDto;
import com.sarmo.listingservice.dto.UpdateCommentDto;
import com.sarmo.listingservice.dto.UserInfoDto;
import com.sarmo.listingservice.entity.Comment;
import com.sarmo.listingservice.entity.Listing;
import com.sarmo.listingservice.entity.User; // Импортируем сущность User
import com.sarmo.listingservice.repository.CommentRepository;
import com.sarmo.listingservice.repository.ListingRepository;
import com.sarmo.listingservice.repository.UserRepository; // Импортируем UserRepository
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommentService {

    private static final Logger logger = LoggerFactory.getLogger(CommentService.class);

    private final CommentRepository commentRepository;
    private final ListingRepository listingRepository;
    private final UserRepository userRepository; // Добавляем UserRepository

    public CommentService(CommentRepository commentRepository, ListingRepository listingRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.listingRepository = listingRepository;
        this.userRepository = userRepository;
    }

    public boolean isCommentOwner(Long commentId, String userIdString) {
        logger.debug("Checking ownership for comment {} by user (string) {}", commentId, userIdString);

        if (userIdString == null) {
            logger.debug("userIdString is null, cannot check ownership for comment.");
            return false; // Cannot be an owner if user ID is unknown
        }

        // 1. Find the comment by its ID
        Optional<Comment> commentOptional = commentRepository.findById(commentId); // Assuming CommentRepository exists and is injected

        if (commentOptional.isPresent()) {
            Comment comment = commentOptional.get();

            // 2. Get the Long ID of the comment's author via the relationship with the User entity
            // Check in case the comment, for some reason, does not have an author assigned (although @NotNull)
            if (comment.getAuthor() == null || comment.getAuthor().getId() == null) {
                logger.warn("Comment {} found but has no author or author ID assigned (should not happen based on @NotNull).", commentId);
                return false; // Comment has no author, user cannot be the author
            }

            Long authorIdLong = comment.getAuthor().getId();

            // 3. Compare the Long authorId (converted to String) with the provided String user ID from the token
            boolean isActualOwner = String.valueOf(authorIdLong).equals(userIdString);

            if (isActualOwner) {
                logger.debug("User (string) {} IS owner of comment {}", userIdString, commentId);
            } else {
                logger.debug("User (string) {} is NOT owner of comment {}. Actual author ID: {}", userIdString, commentId, authorIdLong);
            }

            return isActualOwner;

        } else {
            logger.warn("Comment with id {} not found during ownership check", commentId);
            // Comment not found. User cannot be its owner.
            return false;
        }
    }

    private Listing findListingById(Long listingId) {
        return listingRepository.findById(listingId)
                .orElseThrow(() -> {
                    logger.error("Listing not found for id: {}", listingId);
                    return new RuntimeException("Listing not found");
                });
    }

    private Comment findCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> {
                    logger.error("Comment not found for id: {}", commentId);
                    return new RuntimeException("Comment not found");
                });
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User not found for id: {}", userId);
                    return new RuntimeException("User not found");
                });
    }

    @Transactional
    public CommentDTO addComment(CreateCommentDto createCommentDTO, Long userId) {
        Listing listing = findListingById(createCommentDTO.getListingId());
        User author = findUserById(userId); // Получаем сущность User
        logger.info("Adding comment for listingId: {}, userId: {}", createCommentDTO.getListingId(), userId);

        Optional<Comment> parentComment = Optional.empty();
        if (createCommentDTO.getParentCommentId() != null) {
            parentComment = Optional.ofNullable(findCommentById(createCommentDTO.getParentCommentId()));
            logger.info("Comment has a parent: parentCommentId: {}", createCommentDTO.getParentCommentId());
        }

        Comment comment = new Comment(listing, author, createCommentDTO.getContent(), parentComment.orElse(null)); // Используем сущность User
        Comment savedComment = commentRepository.save(comment);
        logger.info("Comment added with id: {}", savedComment.getId());
        return convertToCommentDTO(savedComment);
    }

    @Transactional
    public CommentDTO updateComment(Long commentId, UpdateCommentDto updateCommentDTO, Long userId) {
        Comment comment = findCommentById(commentId);
        logger.info("Updating comment with id: {}", commentId);

        if (!comment.getAuthor().getId().equals(userId)) {
            logger.error("User with id: {} is not authorized to update comment with id: {}", userId, commentId);
            throw new RuntimeException("Unauthorized to update comment");
        }

        if (!comment.getContent().equals(updateCommentDTO.getContent())) {
            comment.setContent(updateCommentDTO.getContent());
            comment.setEdited(true);
            logger.info("Comment with id: {} updated. Marked as edited.", commentId);
        } else {
            logger.debug("No change in comment content. Comment with id: {} remains unchanged.", commentId);
        }

        return convertToCommentDTO(commentRepository.save(comment));
    }

    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = findCommentById(commentId);
        logger.info("Deleting comment with id: {}", commentId);

        if (!comment.getAuthor().getId().equals(userId)) { // Проверяем ID пользователя через связь author
            logger.error("User with id: {} is not authorized to delete comment with id: {}", userId, commentId);
            throw new RuntimeException("Unauthorized to delete comment");
        }

        commentRepository.delete(comment);
        logger.info("Comment with id: {} deleted by user with id: {}", commentId, userId);
    }

    public List<CommentDTO> getCommentsForListing(Long listingId) {
        List<Comment> comments = commentRepository.findByListingId(listingId);
        List<Comment> flattenedComments = comments.stream()
                .flatMap(comment -> comment.flattenReplies().stream())
                .toList();

        LinkedHashSet<Comment> uniqueComments = new LinkedHashSet<>(flattenedComments);

        return uniqueComments.stream()
                .map(this::convertToCommentDTO)
                .collect(Collectors.toList());
    }

    public List<CommentDTO> getReplies(Long parentCommentId) {
        List<Comment> replies = commentRepository.findRepliesByParentId(parentCommentId);
        return replies.stream()
                .map(this::convertToCommentDTO)
                .collect(Collectors.toList());
    }

    private CommentDTO convertToCommentDTO(Comment comment) {
        CommentDTO dto = new CommentDTO();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setParentId(comment.getParentComment() != null ? comment.getParentComment().getId() : null);
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setEdited(comment.isEdited());

        // Создаем и заполняем UserInfoDto из сущности User author
        UserInfoDto authorInfoDto = new UserInfoDto();
        authorInfoDto.setId(comment.getAuthor().getId());
        authorInfoDto.setFirstName(comment.getAuthor().getFirstName());
        authorInfoDto.setLastName(comment.getAuthor().getLastName());
        authorInfoDto.setEmail(comment.getAuthor().getEmail());
        authorInfoDto.setProfileImageUrl(comment.getAuthor().getProfileImageUrl());

        dto.setAuthor(authorInfoDto);
        return dto;
    }
}