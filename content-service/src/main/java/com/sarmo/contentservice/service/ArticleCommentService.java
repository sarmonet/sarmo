package com.sarmo.contentservice.service;

import com.sarmo.contentservice.dto.ArticleCommentDTO;
import com.sarmo.contentservice.entity.ArticleComment;
import com.sarmo.contentservice.entity.Article;
import com.sarmo.contentservice.entity.User;
import com.sarmo.contentservice.repository.ArticleCommentRepository;
import com.sarmo.contentservice.repository.ArticleRepository;
import com.sarmo.contentservice.repository.UserRepository;
import com.sarmo.contentservice.dto.ArticleCommentCreateDTO;
import com.sarmo.contentservice.dto.ArticleCommentUpdateDTO;
import com.sarmo.contentservice.exception.ResourceNotFoundException;
import com.sarmo.contentservice.exception.UnauthorizedActionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ArticleCommentService {

    private static final Logger logger = LoggerFactory.getLogger(ArticleCommentService.class);

    private final ArticleCommentRepository commentRepository;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;

    public ArticleCommentService(ArticleCommentRepository commentRepository, ArticleRepository articleRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.articleRepository = articleRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<ArticleCommentDTO> getAllComments() { // Изменили возвращаемый тип на List<ArticleCommentDTO>
        try {
            logger.info("Fetching all article comments for DTO mapping"); // Обновили сообщение лога

            List<ArticleComment> comments = commentRepository.findAll();

            // Выполняем маппинг списка сущностей ArticleComment в список DTO
            List<ArticleCommentDTO> commentDTOs = comments.stream()
                    .map(this::toArticleCommentDTO) // Вызываем вспомогательный метод маппинга
                    .collect(Collectors.toList());

            logger.info("Successfully fetched and mapped {} article comments to DTOs", commentDTOs.size()); // Обновили сообщение лога
            return commentDTOs; // Возвращаем список DTO

        } catch (Exception e) {
            logger.error("Error fetching and mapping all comments: {}", e.getMessage(), e); // Обновили сообщение лога
            throw e; // Перебрасываем исключение
        }
    }

    @Transactional(readOnly = true)
    public Optional<ArticleCommentDTO> getCommentById(Long id) { // Изменили возвращаемый тип на Optional<ArticleCommentDTO>
        try {
            logger.info("Fetching article comment with ID: {}", id); // Обновили сообщение лога

            // Используем findById(). author (EAGER) будет загружен. article (LAZY) будет прокси.
            Optional<ArticleComment> commentOptional = commentRepository.findById(id);

            // Выполняем маппинг сущности в DTO, если она присутствует
            Optional<ArticleCommentDTO> commentDTOOptional = commentOptional.map(this::toArticleCommentDTO); // Используем вспомогательный метод маппинга

            logger.info("Successfully fetched and mapped article comment with ID {}", id); // Обновили сообщение лога
            return commentDTOOptional; // Возвращаем Optional с DTO

        } catch (Exception e) {
            logger.error("Error fetching and mapping article comment with ID {}: {}", id, e.getMessage(), e); // Обновили сообщение лога
            throw e; // Перебрасываем исключение
        }
    }

    // *** Вспомогательный метод для маппинга сущности ArticleComment в ArticleCommentDTO ***
    private ArticleCommentDTO toArticleCommentDTO(ArticleComment comment) {
        if (comment == null) {
            return null;
        }
        ArticleCommentDTO dto = new ArticleCommentDTO();
        dto.setId(comment.getId());
        dto.setText(comment.getText());
        dto.setCreationDate(comment.getCreationDate());
        dto.setParentCommentId(comment.getParentCommentId());
        dto.setAuthor(comment.getAuthor());

        return dto;
    }


    @Transactional(readOnly = true)
    public List<ArticleCommentDTO> getCommentsByArticleId(Long articleId) { // Изменили возвращаемый тип на List<ArticleCommentDTO>
        try {
            logger.info("Fetching article comments by articleID: {} for DTO mapping", articleId); // Обновили сообщение лога

            // Используем метод репозитория для поиска комментариев по ID статьи
            // Связь 'author' (EAGER) будет загружена. Связь 'article' (LAZY) будет прокси.
            List<ArticleComment> comments = commentRepository.findAllByArticleId(articleId); // Используем метод репозитория

            // Выполняем маппинг списка сущностей в список DTO, используя ПРАВИЛЬНЫЙ метод маппинга
            List<ArticleCommentDTO> commentDTOs = comments.stream()
                    .map(this::toArticleCommentDTO) // <--- ВЫЗЫВАЕМ ПРАВИЛЬНЫЙ МЕТОД МАППИНГА
                    .collect(Collectors.toList());

            logger.info("Successfully fetched and mapped {} comments for article {}", commentDTOs.size(), articleId); // Обновили сообщение лога
            return commentDTOs; // Возвращаем список DTO

        } catch (Exception e) {
            logger.error("Error fetching and mapping comments for article {}: {}", articleId, e.getMessage(), e); // Обновили сообщение лога
            throw e; // Перебрасываем исключение
        }
    }

    @Transactional
    public ArticleComment createComment(ArticleCommentCreateDTO commentCreateDTO, Long authorId) {
        try {
            logger.info("Creating comment for article ID: {} by author ID: {}", commentCreateDTO.getArticleId(), authorId);

            Article article = articleRepository.findById(commentCreateDTO.getArticleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Article not found with ID: " + commentCreateDTO.getArticleId()));

            User author = userRepository.findById(authorId)
                    .orElseThrow(() -> new ResourceNotFoundException("Author not found with ID: " + authorId));

            ArticleComment comment = new ArticleComment();

            comment.setText(commentCreateDTO.getText());
            comment.setParentCommentId(commentCreateDTO.getParentCommentId());

            comment.setArticle(article);
            comment.setAuthor(author);

            ArticleComment savedComment = commentRepository.save(comment);
            logger.info("Comment created with ID: {}", savedComment.getId());
            return savedComment;
        } catch (Exception e) {
            logger.error("Error creating comment for article ID {}: {}", commentCreateDTO.getArticleId(), e.getMessage(), e);
            throw e;
        }
    }

    @Transactional
    public ArticleComment updateComment(Long commentId, ArticleCommentUpdateDTO commentUpdateDTO, Long authenticatedUserId) {
        try {
            logger.info("Updating comment with ID: {} by user ID: {}", commentId, authenticatedUserId);

            ArticleComment existingComment = commentRepository.findById(commentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Comment not found with ID: " + commentId));

            if (!existingComment.getAuthor().getId().equals(authenticatedUserId)) {
                throw new UnauthorizedActionException("User is not authorized to update this comment");
            }

            if (commentUpdateDTO.getText() != null) {
                existingComment.setText(commentUpdateDTO.getText());
            }

            ArticleComment updatedComment = commentRepository.save(existingComment);
            logger.info("Comment with ID: {} updated successfully", updatedComment.getId());
            return updatedComment;
        } catch (Exception e) {
            logger.error("Error updating comment with ID {}: {}", commentId, e.getMessage(), e);
            throw e;
        }
    }

    @Transactional
    public void deleteComment(Long id, Long authenticatedUserId) {
        try {
            logger.info("Deleting comment with ID: {} by user ID: {}", id, authenticatedUserId);

            ArticleComment commentToDelete = commentRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Comment not found with ID: " + id));

            if (!commentToDelete.getAuthor().getId().equals(authenticatedUserId)) {
                throw new UnauthorizedActionException("User is not authorized to delete this comment");
            }

            commentRepository.deleteById(id);
            logger.info("Comment with ID: {} deleted successfully by user {}", id, authenticatedUserId);
        } catch (Exception e) {
            logger.error("Error deleting comment with ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }


    @Transactional(readOnly = true)
    public boolean isAuthor(Long commentId, String userIdString) {
        logger.debug("Checking authorship for comment {} by user (string) {}", commentId, userIdString);

        if (userIdString == null) {
            logger.warn("isAuthor check called with null userIdString");
            return false; // Не может быть автором, если ID пользователя неизвестен
        }
        if (commentId == null) {
            logger.warn("isAuthor check called with null commentId");
            return false; // Нельзя проверить автора для пустого ID комментария
        }

        try {
            Long userId = Long.parseLong(userIdString); // Преобразуем строковый ID в Long

            // Находим комментарий по ID
            Optional<ArticleComment> commentOptional = commentRepository.findById(commentId);

            // Если комментарий найден, проверяем, соответствует ли ID его автора предоставленному userId
            if (commentOptional.isPresent()) {
                ArticleComment comment = commentOptional.get();
                if (comment.getAuthor() == null || comment.getAuthor().getId() == null) {
                    logger.warn("Comment {} found but has no author assigned.", commentId);
                    return false; // У комментария нет автора, пользователь не может быть автором
                }
                boolean isActualAuthor = comment.getAuthor().getId().equals(userId);
                if (isActualAuthor) {
                    logger.debug("User (string) {} IS author of comment {}", userIdString, commentId);
                } else {
                    logger.debug("User (string) {} is NOT author of comment {}. Actual author ID: {}", userIdString, commentId, comment.getAuthor().getId());
                }
                return isActualAuthor;
            } else {
                logger.warn("Comment with ID {} not found during isAuthor check", commentId);
                return false; // Комментарий не найден, поэтому пользователь не является автором
            }
        } catch (NumberFormatException e) {
            logger.error("Invalid userIdString format: {}", userIdString, e);
            return false; // Ошибка парсинга ID пользователя
        } catch (Exception e) {
            logger.error("Error during isAuthor check for comment ID {} and user ID {}: {}", commentId, userIdString, e.getMessage(), e);
            return false; // Предполагаем, что не авторизован при ошибке
        }
    }

    // TODO: Add methods for managing comment replies if implementing hierarchy
}