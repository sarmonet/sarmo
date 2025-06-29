package com.sarmo.contentservice.service;

import com.sarmo.contentservice.dto.ArticleCommentDTO;
import com.sarmo.contentservice.dto.NewsCommentDTO;
import com.sarmo.contentservice.entity.ArticleComment;
import com.sarmo.contentservice.entity.NewsComment;
import com.sarmo.contentservice.entity.News; // Import News entity
import com.sarmo.contentservice.entity.User; // Import your local User entity
import com.sarmo.contentservice.repository.NewsCommentRepository;
import com.sarmo.contentservice.repository.NewsRepository; // Import NewsRepository
import com.sarmo.contentservice.repository.UserRepository; // Import your local UserRepository
import com.sarmo.contentservice.dto.NewsCommentCreateDTO; // Assuming NewsCommentCreateDTO exists
import com.sarmo.contentservice.dto.NewsCommentUpdateDTO; // Assuming NewsCommentUpdateDTO exists
import com.sarmo.contentservice.exception.ResourceNotFoundException; // Import custom exceptions
import com.sarmo.contentservice.exception.UnauthorizedActionException; // Import custom exceptions

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NewsCommentService {

    private static final Logger logger = LoggerFactory.getLogger(NewsCommentService.class);

    private final NewsCommentRepository commentRepository;
    private final NewsRepository newsRepository; // Inject NewsRepository to fetch the News
    private final UserRepository userRepository; // Inject UserRepository to fetch the User entity (author)

    @Autowired
    public NewsCommentService(NewsCommentRepository commentRepository, NewsRepository newsRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.newsRepository = newsRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true) // Read-only transaction
    public boolean isAuthor(Long commentId, String userIdString) {
        logger.debug("Checking authorship for news comment {} by user (string) {}", commentId, userIdString);

        if (userIdString == null) {
            logger.warn("isAuthor check called with null userIdString");
            return false;
        }
        if (commentId == null) {
            logger.warn("isAuthor check called with null commentId");
            return false;
        }

        try {
            Long userId = Long.parseLong(userIdString);

            Optional<NewsComment> commentOptional = commentRepository.findById(commentId);

            if (commentOptional.isPresent()) {
                NewsComment comment = commentOptional.get();
                if (comment.getAuthor() == null || comment.getAuthor().getId() == null) {
                    logger.warn("News comment {} found but has no author assigned.", commentId);
                    return false;
                }
                boolean isActualAuthor = comment.getAuthor().getId().equals(userId);
                if (isActualAuthor) {
                    logger.debug("User (string) {} IS author of news comment {}", userIdString, commentId);
                } else {
                    logger.debug("User (string) {} is NOT author of news comment {}. Actual author ID: {}", userIdString, commentId, comment.getAuthor().getId());
                }
                return isActualAuthor;
            } else {
                logger.warn("News comment with ID {} not found during isAuthor check", commentId);
                return false;
            }
        } catch (NumberFormatException e) {
            logger.error("Invalid userIdString format: {}", userIdString, e);
            return false;
        } catch (Exception e) {
            logger.error("Error during isAuthor check for news comment ID {} and user ID {}: {}", commentId, userIdString, e.getMessage(), e);
            return false;
        }
    }


    @Transactional(readOnly = true)
    public List<NewsCommentDTO> getAllComments() { // Изменили возвращаемый тип на List<ArticleCommentDTO>
        try {
            logger.info("Fetching all article comments for DTO mapping"); // Обновили сообщение лога

            List<NewsComment> comments = commentRepository.findAll();

            // Выполняем маппинг списка сущностей ArticleComment в список DTO
            List<NewsCommentDTO> commentDTOs = comments.stream()
                    .map(this::toNewsCommentDTO) // Вызываем вспомогательный метод маппинга
                    .collect(Collectors.toList());

            logger.info("Successfully fetched and mapped {} article comments to DTOs", commentDTOs.size()); // Обновили сообщение лога
            return commentDTOs; // Возвращаем список DTO

        } catch (Exception e) {
            logger.error("Error fetching and mapping all comments: {}", e.getMessage(), e); // Обновили сообщение лога
            throw e; // Перебрасываем исключение
        }
    }

    @Transactional(readOnly = true)
    public Optional<NewsCommentDTO> getCommentById(Long id) { // Изменили возвращаемый тип на Optional<ArticleCommentDTO>
        try {
            logger.info("Fetching article comment with ID: {}", id); // Обновили сообщение лога

            // Используем findById(). author (EAGER) будет загружен. article (LAZY) будет прокси.
            Optional<NewsComment> commentOptional = commentRepository.findById(id);

            // Выполняем маппинг сущности в DTO, если она присутствует
            Optional<NewsCommentDTO> commentDTOOptional = commentOptional.map(this::toNewsCommentDTO); // Используем вспомогательный метод маппинга

            logger.info("Successfully fetched and mapped article comment with ID {}", id); // Обновили сообщение лога
            return commentDTOOptional; // Возвращаем Optional с DTO

        } catch (Exception e) {
            logger.error("Error fetching and mapping article comment with ID {}: {}", id, e.getMessage(), e); // Обновили сообщение лога
            throw e; // Перебрасываем исключение
        }
    }

    private NewsCommentDTO toNewsCommentDTO(NewsComment comment) {
        if (comment == null) {
            return null;
        }
        NewsCommentDTO dto = new NewsCommentDTO();
        dto.setId(comment.getId());
        dto.setText(comment.getText());
        dto.setCreationDate(comment.getCreationDate());
        dto.setParentCommentId(comment.getParentCommentId());
        dto.setAuthor(comment.getAuthor());

        return dto;
    }

    @Transactional(readOnly = true) // Read-only transaction
    public List<NewsCommentDTO> getCommentsByNewsId(Long newsId) { // Изменили возвращаемый тип на List<NewsCommentDTO>
        try {
            logger.info("Fetching news comments by newsID: {} for DTO mapping", newsId); // Обновили сообщение лога

            // Используем метод репозитория для поиска комментариев по ID новости
            // Убедитесь, что FetchType для автора подходит для этого метода,
            // или добавьте JOIN FETCH author в репозитории, если author LAZY.
            List<NewsComment> comments = commentRepository.findAllByNewsId(newsId); // Используем метод репозитория

            // Выполняем маппинг списка сущностей в список DTO, используя ПРАВИЛЬНЫЙ метод маппинга
            List<NewsCommentDTO> commentDTOs = comments.stream()
                    .map(this::toNewsCommentDTO) // <--- ВЫЗЫВАЕМ ПРАВИЛЬНЫЙ МЕТОД МАППИНГА
                    .collect(Collectors.toList());

            logger.info("Successfully fetched and mapped {} comments for news {}", commentDTOs.size(), newsId); // Обновили сообщение лога
            return commentDTOs; // Возвращаем список DTO

        } catch (Exception e) {
            logger.error("Error fetching and mapping comments for news {}: {}", newsId, e.getMessage(), e); // Обновили сообщение лога
            throw e;
        }
    }

    @Transactional // Transaction for creating data
    // Accept DTO and authorId instead of raw entity
    public NewsComment createComment(NewsCommentCreateDTO commentCreateDTO, Long authorId) {
        try {
            logger.info("Creating news comment for news ID: {} by author ID: {}", commentCreateDTO.getNewsId(), authorId);

            // 1. Fetch the News the comment belongs to
            News news = newsRepository.findById(commentCreateDTO.getNewsId())
                    .orElseThrow(() -> new ResourceNotFoundException("News not found with ID: " + commentCreateDTO.getNewsId()));

            // 2. Fetch the User entity (author) based on the authenticated authorId
            User author = userRepository.findById(authorId)
                    .orElseThrow(() -> new ResourceNotFoundException("Author not found with ID: " + authorId));

            // 3. Create a new NewsComment entity
            NewsComment comment = new NewsComment();

            // 4. Map fields from DTO to entity
            comment.setText(commentCreateDTO.getText());
            comment.setParentCommentId(commentCreateDTO.getParentCommentId()); // Set parent comment ID if provided

            // Set creationDate (assuming @CreationTimestamp handles it, but can set explicitly)
            // comment.setCreationDate(LocalDateTime.now());

            // 5. Set the relationships
            comment.setNews(news); // Use the setNews method
            comment.setAuthor(author); // Use the setAuthor method

            // TODO: If implementing parent/reply relationship via entities, fetch parent comment here
            // if (commentCreateDTO.getParentCommentId() != null) { ... fetch parent and set relationship ... }

            // 6. Save the NewsComment entity
            NewsComment savedComment = commentRepository.save(comment);
            logger.info("News comment created with ID: {}", savedComment.getId());
            return savedComment;
        } catch (Exception e) {
            logger.error("Error creating news comment for news ID {}: {}", commentCreateDTO.getNewsId(), e.getMessage(), e);
            throw e;
        }
    }

    @Transactional // Transaction for updating data
    // Accept comment ID, update DTO, and authenticated user ID
    public NewsComment updateComment(Long commentId, NewsCommentUpdateDTO commentUpdateDTO, Long authenticatedUserId) {
        try {
            logger.info("Updating news comment with ID: {} by user ID: {}", commentId, authenticatedUserId);

            // 1. Find the existing comment
            NewsComment existingComment = commentRepository.findById(commentId)
                    .orElseThrow(() -> new ResourceNotFoundException("News comment not found with ID: " + commentId));

            // 2. Verify that the authenticated user is the author or has permission to update
            if (!existingComment.getAuthor().getId().equals(authenticatedUserId)) {
                // TODO: Implement more sophisticated authorization if needed (e.g., Admin role)
                throw new UnauthorizedActionException("User is not authorized to update this news comment");
            }

            // 3. Map allowed fields from DTO to existing entity (handles partial updates by checking for nulls)
            // Likely only text is updateable by a user after creation. Parent is usually not changeable.
            if (commentUpdateDTO.getText() != null) {
                existingComment.setText(commentUpdateDTO.getText());
            }
            // TODO: If allowing parentCommentId update, handle fetching and setting relationship here

            // Do NOT allow updating ID, author, news, creation date directly via update DTO

            // 4. Save the updated comment entity
            NewsComment updatedComment = commentRepository.save(existingComment);
            logger.info("News comment with ID: {} updated successfully", updatedComment.getId());
            return updatedComment;
        } catch (Exception e) {
            logger.error("Error updating news comment with ID {}: {}", commentId, e.getMessage(), e);
            throw e;
        }
    }

    @Transactional // Transaction for deleting data
    public void deleteComment(Long id, Long authenticatedUserId) { // Add authenticatedUserId for authorization
        try {
            logger.info("Deleting news comment with ID: {} by user ID: {}", id, authenticatedUserId);

            // Optional: Verify that the authenticated user is the author or has permission to delete
            NewsComment commentToDelete = commentRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("News comment not found with ID: " + id));

            if (!commentToDelete.getAuthor().getId().equals(authenticatedUserId)) {
                // TODO: Implement more sophisticated authorization if needed (e.g., Admin role)
                throw new UnauthorizedActionException("User is not authorized to delete this news comment");
            }

            commentRepository.deleteById(id);
            logger.info("News comment with ID: {} deleted successfully by user {}", id, authenticatedUserId);
        } catch (Exception e) {
            logger.error("Error deleting news comment with ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

}