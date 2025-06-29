package com.sarmo.contentservice.service;

import com.sarmo.contentservice.entity.Article;
import com.sarmo.contentservice.entity.User; // Import your local User entity
import com.sarmo.contentservice.repository.ArticleRepository;
import com.sarmo.contentservice.repository.UserRepository; // Import your local UserRepository
import com.sarmo.contentservice.dto.ArticleCreateDTO; // Import the DTO for creating articles
import com.sarmo.contentservice.dto.ArticleUpdateDTO; // Import the DTO for updating articles (should be a separate file)
import com.sarmo.contentservice.exception.ResourceNotFoundException; // Import your custom exception class
import com.sarmo.contentservice.exception.UnauthorizedActionException; // Import your custom exception class

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime; // Needed for potential date handling
import java.util.List;
import java.util.Optional;
import java.util.UUID; // Assuming contentId is a UUID or similar generated ID

@Service
public class ArticleService {

    private static final Logger logger = LoggerFactory.getLogger(ArticleService.class);

    private final ArticleRepository articleRepository;
    private final UserRepository userRepository; // Inject the UserRepository to fetch the User entity

    // TODO: Inject ContentService or similar to handle saving/mapping ContentItems from DTO

    @Autowired
    public ArticleService(ArticleRepository articleRepository, UserRepository userRepository) {
        this.articleRepository = articleRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true) // Read-only transaction for fetching data
    public List<Article> getAllArticles() { // Изменили возвращаемый тип на List<ArticleDTO>
        try {
            logger.info("Fetching all news");
            // TODO: Consider returning a List<NewsSummaryDTO> instead of entities to avoid issues with lazy loading
            return articleRepository.findAll();
        } catch (Exception e) {
            logger.error("Error fetching all news: {}", e.getMessage(), e);
            throw e;
        }
    }


    @Transactional(readOnly = true) // Read-only transaction for fetching data
    public Optional<Article> getArticleById(Long id) {
        try {
            logger.info("Fetching article with ID: {}", id);
            // TODO: Consider returning ArticleFullDTO instead of the entity to avoid issues with lazy loading
            // If returning entity, ensure 'author' is fetched eagerly or within a transaction
            return articleRepository.findById(id); // add ".orElse(null)" or ".orElseThrow()" if preferred
        } catch (Exception e) {
            logger.error("Error fetching article with ID: {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @Transactional
    public Article createArticle(ArticleCreateDTO articleCreateDTO, Long authorId) {
        try {
            logger.info("Creating article for author ID: {}", authorId);

            // 1. Fetch the User entity (author) based on the authenticated authorId
            User author = userRepository.findById(authorId)
                    .orElseThrow(() -> new ResourceNotFoundException("Author not found with ID: " + authorId));

            // 2. Create a new Article entity
            Article article = new Article();

            // 3. Map fields from DTO to entity
            article.setMainImage(articleCreateDTO.getMainImage());
            article.setTitle(articleCreateDTO.getTitle());
            article.setDescription(articleCreateDTO.getDescription());

            String contentId = UUID.randomUUID().toString(); // Example: Generate a UUID for contentId
            article.setContentId(contentId); // Set the generated contentId

            // 4. Set the author relationship
            article.setAuthor(author); // Use the setAuthor method on the entity

            // 5. Save the Article entity
            Article savedArticle = articleRepository.save(article);
            logger.info("Article created with ID: {}", savedArticle.getId());
            return savedArticle;
        } catch (Exception e) {
            logger.error("Error creating article for author ID: {}: {}", authorId, e.getMessage(), e);
            throw e;
        }
    }

    @Transactional
    public Article updateArticle(Long articleId, ArticleUpdateDTO articleUpdateDTO, Long authenticatedUserId) {
        try {
            logger.info("Updating article with ID: {} by user ID: {}", articleId, authenticatedUserId);

            Article existingArticle = articleRepository.findById(articleId)
                    .orElseThrow(() -> new ResourceNotFoundException("Article not found with ID: " + articleId));

            if (!existingArticle.getAuthor().getId().equals(authenticatedUserId)) {
                throw new UnauthorizedActionException("User is not authorized to update this article");
            }

            if (articleUpdateDTO.getMainImage() != null) {
                existingArticle.setMainImage(articleUpdateDTO.getMainImage());
            }
            if (articleUpdateDTO.getTitle() != null) {
                existingArticle.setTitle(articleUpdateDTO.getTitle());
            }
            if (articleUpdateDTO.getDescription() != null) {
                existingArticle.setDescription(articleUpdateDTO.getDescription());
            }

            Article updatedArticle = articleRepository.save(existingArticle);
            logger.info("Article with ID: {} updated successfully", updatedArticle.getId());
            return updatedArticle;
        } catch (Exception e) {
            logger.error("Error updating article with ID: {}: {}", articleId, e.getMessage(), e);
            throw e;
        }
    }

    @Transactional
    public void deleteArticle(Long id, Long authenticatedUserId) {
        try {
            logger.info("Deleting article with ID: {} by user ID: {}", id, authenticatedUserId);

            Article articleToDelete = articleRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Article not found with ID: " + id));

            if (!articleToDelete.getAuthor().getId().equals(authenticatedUserId)) {
                throw new UnauthorizedActionException("User is not authorized to delete this article");
            }

            articleRepository.deleteById(id);
            logger.info("Article with ID: {} deleted successfully", id);
        } catch (Exception e) {
            logger.error("Error deleting article with ID: {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }
}