package com.sarmo.contentservice.service;

import com.sarmo.contentservice.entity.News;
import com.sarmo.contentservice.entity.User; // Import your local User entity
import com.sarmo.contentservice.repository.NewsRepository;
import com.sarmo.contentservice.repository.UserRepository; // Import your local UserRepository
import com.sarmo.contentservice.dto.NewsCreateDTO; // Assuming a NewsCreateDTO exists
import com.sarmo.contentservice.dto.NewsUpdateDTO; // Assuming a NewsUpdateDTO exists
import com.sarmo.contentservice.exception.ResourceNotFoundException; // Import custom exceptions
import com.sarmo.contentservice.exception.UnauthorizedActionException; // Import custom exceptions

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
public class NewsService {

    private static final Logger logger = LoggerFactory.getLogger(NewsService.class);

    private final NewsRepository newsRepository;
    private final UserRepository userRepository; // Inject the UserRepository to fetch the User entity

    // TODO: Inject ContentService or similar to handle saving/mapping ContentItems from DTO

    @Autowired
    public NewsService(NewsRepository newsRepository, UserRepository userRepository) {
        this.newsRepository = newsRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true) // Read-only transaction for fetching data
    public List<News> getAllNews() {
        try {
            logger.info("Fetching all news");
            // TODO: Consider returning a List<NewsSummaryDTO> instead of entities to avoid issues with lazy loading
            return newsRepository.findAll();
        } catch (Exception e) {
            logger.error("Error fetching all news: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Transactional(readOnly = true) // Read-only transaction for fetching data
    public Optional<News> getNewsById(Long id) {
        try {
            logger.info("Fetching news with ID: {}", id);
            // TODO: Consider returning NewsFullDTO instead of the entity to avoid issues with lazy loading
            // If returning entity, ensure 'author' is fetched eagerly or within a transaction
            return newsRepository.findById(id); // add ".orElse(null)" or ".orElseThrow()" if preferred
        } catch (Exception e) {
            logger.error("Error fetching news with ID: {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @Transactional // Transaction for creating data
    public News createNews(NewsCreateDTO newsCreateDTO, Long authorId) {
        try {
            logger.info("Creating news for author ID: {}", authorId);

            // 1. Fetch the User entity (author) based on the authenticated authorId
            User author = userRepository.findById(authorId)
                    .orElseThrow(() -> new ResourceNotFoundException("Author not found with ID: " + authorId));

            // 2. Create a new News entity
            News news = new News();

            // 3. Map fields from DTO to entity
            news.setMainImage(newsCreateDTO.getMainImage());
            news.setTitle(newsCreateDTO.getTitle());
            news.setDescription(newsCreateDTO.getDescription());

            String contentId = UUID.randomUUID().toString();
            news.setContentId(contentId); // Set the generated contentId

            // 4. Set the author relationship
            news.setAuthor(author); // Use the setAuthor method on the entity

            // 5. Save the News entity
            News savedNews = newsRepository.save(news);
            logger.info("News created with ID: {}", savedNews.getId());
            return savedNews;
        } catch (Exception e) {
            logger.error("Error creating news for author ID: {}: {}", authorId, e.getMessage(), e);
            throw e;
        }
    }

    @Transactional // Transaction for updating data
    // Accept news ID, update DTO, and authenticated user ID
    public News updateNews(Long newsId, NewsUpdateDTO newsUpdateDTO, Long authenticatedUserId) {
        try {
            logger.info("Updating news with ID: {} by user ID: {}", newsId, authenticatedUserId);

            // 1. Find the existing news
            News existingNews = newsRepository.findById(newsId)
                    .orElseThrow(() -> new ResourceNotFoundException("News not found with ID: " + newsId));

            // 2. Verify that the authenticated user is the author or has permission to update
            if (!existingNews.getAuthor().getId().equals(authenticatedUserId)) {
                // TODO: Implement more sophisticated authorization if needed (e.g., Admin role)
                throw new UnauthorizedActionException("User is not authorized to update this news");
            }

            // 3. Map allowed fields from DTO to existing entity (handles partial updates by checking for nulls)
            // Do NOT allow updating ID, author, publication date, view count directly via update DTO
            if (newsUpdateDTO.getMainImage() != null) {
                existingNews.setMainImage(newsUpdateDTO.getMainImage());
            }
            if (newsUpdateDTO.getTitle() != null) {
                existingNews.setTitle(newsUpdateDTO.getTitle());
            }
            if (newsUpdateDTO.getDescription() != null) {
                existingNews.setDescription(newsUpdateDTO.getDescription());
            }

            // TODO: Handle updates to ContentItems if allowed via update DTO

            // 4. Save the updated news entity
            News updatedNews = newsRepository.save(existingNews);
            logger.info("News with ID: {} updated successfully", updatedNews.getId());
            return updatedNews;
        } catch (Exception e) {
            logger.error("Error updating news with ID: {}: {}", newsId, e.getMessage(), e);
            throw e;
        }
    }

    @Transactional // Transaction for deleting data
    public void deleteNews(Long id, Long authenticatedUserId) { // Add authenticatedUserId for authorization
        try {
            logger.info("Deleting news with ID: {} by user ID: {}", id, authenticatedUserId);

            // Optional: Verify that the authenticated user is the author or has permission to delete
            News newsToDelete = newsRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("News not found with ID: " + id));

            if (!newsToDelete.getAuthor().getId().equals(authenticatedUserId)) {
                // TODO: Implement more sophisticated authorization if needed (e.g., Admin role)
                throw new UnauthorizedActionException("User is not authorized to delete this news");
            }

            newsRepository.deleteById(id);
            logger.info("News with ID: {} deleted successfully by user {}", id, authenticatedUserId);
        } catch (Exception e) {
            logger.error("Error deleting news with ID: {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

}