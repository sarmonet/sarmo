package com.sarmo.contentservice.service;

import com.sarmo.contentservice.dto.ArticleCreateDTO;
import com.sarmo.contentservice.dto.ArticleFullDTO;
import com.sarmo.contentservice.dto.NewsCreateDTO;
import com.sarmo.contentservice.dto.NewsFullDTO;
import com.sarmo.contentservice.entity.*; // Import all entities
import com.sarmo.contentservice.repository.*; // Import all repositories
import com.sarmo.contentservice.exception.ResourceNotFoundException; // Import custom exception

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Import Transactional

import java.util.List;
import java.util.Optional;
import java.util.UUID;

// Import DTOs for updates with new names
import com.sarmo.contentservice.dto.NewsUpdateFullDTO;
import com.sarmo.contentservice.dto.ArticleUpdateFullDTO;


@Service
public class ContentFullService {

    private static final Logger logger = LoggerFactory.getLogger(ContentFullService.class.getName());

    private final NewsRepository newsRepository;
    private final ArticleRepository articleRepository;
    private final NewsCommentRepository newsCommentRepository;
    private final ContentRepository contentRepository;
    private final RandomService randomService;
    private final UserRepository userRepository;

    @Autowired
    public ContentFullService(NewsRepository newsRepository, ArticleRepository articleRepository, NewsCommentRepository newsCommentRepository, ContentRepository contentRepository, RandomService randomService, UserRepository userRepository) {
        this.newsRepository = newsRepository;
        this.articleRepository = articleRepository;
        this.newsCommentRepository = newsCommentRepository;
        this.contentRepository = contentRepository;
        this.randomService = randomService;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public NewsFullDTO getFullNewsInfo(Long newsId) {
        logger.info("Fetching full news info for ID: {}", newsId);
        News news = newsRepository.findById(newsId)
                .orElseThrow(() -> {
                    logger.error("News with ID: {} not found.", newsId);
                    return new ResourceNotFoundException("News not found with ID: " + newsId);
                });

        List<NewsComment> comments = newsCommentRepository.findAllByNewsId(newsId);

        return buildNewsFullDTO(news, comments);
    }

    @Transactional(readOnly = true)
    public ArticleFullDTO getFullArticleInfo(Long articleId) {
        logger.info("Fetching full article info for ID: {}", articleId);
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> {
                    logger.error("Article with ID: {} not found.", articleId);
                    return new ResourceNotFoundException("Article not found with ID: " + articleId);
                });

        return buildArticleFullDTO(article);
    }

    @Transactional
    public NewsFullDTO createNewsWithContent(NewsCreateDTO newsCreateDTO, Long userId) {
        logger.info("Creating news with content for user ID: {}", userId);
        try {
            User author = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Author not found with ID: " + userId));

            Content savedContent = contentRepository.save(createContentFromDTO(newsCreateDTO.getContent()));
            logger.info("Content created with ID: {}", savedContent.getId());

            News news = createNewsFromDTO(newsCreateDTO, author, savedContent.getId());

            News savedNews = newsRepository.save(news);
            logger.info("News created with ID: {}", savedNews.getId());

            return buildNewsFullDTO(savedNews, List.of());

        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error creating news with content for user ID {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Failed to create news with content.", e);
        }
    }

    @Transactional
    public ArticleFullDTO createArticleWithContent(ArticleCreateDTO articleCreateDTO, Long userId) {
        logger.info("Creating article with content for user ID: {}", userId);
        try {
            User author = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Author not found with ID: " + userId));

            Content savedContent = contentRepository.save(createContentFromDTO(articleCreateDTO.getContent()));
            logger.info("Content created with ID: {}", savedContent.getId());

            Article article = createArticleFromDTO(articleCreateDTO, author, savedContent.getId());

            Article savedArticle = articleRepository.save(article);
            logger.info("Article created with ID: {}", savedArticle.getId());

            return buildArticleFullDTO(savedArticle);

        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error creating article with content for user ID {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Failed to create article with content.", e);
        }
    }

    // --- New Edit Methods ---

    @Transactional
    public NewsFullDTO updateNews(Long newsId, NewsUpdateFullDTO newsUpdateFullDTO) {
        logger.info("Updating news with ID: {}", newsId);
        News existingNews = newsRepository.findById(newsId)
                .orElseThrow(() -> {
                    logger.error("News with ID: {} not found for update.", newsId);
                    return new ResourceNotFoundException("News not found with ID: " + newsId);
                });

        // Update News entity fields
        existingNews.setTitle(newsUpdateFullDTO.getTitle());
        existingNews.setDescription(newsUpdateFullDTO.getDescription());
        existingNews.setMainImage(newsUpdateFullDTO.getMainImage());

        // Update associated Content (in MongoDB)
        Content existingContent = contentRepository.findById(existingNews.getContentId())
                .orElseThrow(() -> {
                    logger.error("Content not found for news ID: {} with content ID: {}. Cannot update.", newsId, existingNews.getContentId());
                    return new ResourceNotFoundException("Content not found with ID: " + existingNews.getContentId());
                });
        existingContent.setContent(newsUpdateFullDTO.getContent());
        contentRepository.save(existingContent);
        logger.info("Content updated for news ID: {}", newsId);

        News updatedNews = newsRepository.save(existingNews);
        logger.info("News with ID: {} updated successfully.", newsId);

        List<NewsComment> comments = newsCommentRepository.findAllByNewsId(newsId);
        return buildNewsFullDTO(updatedNews, comments);
    }

    @Transactional
    public ArticleFullDTO updateArticle(Long articleId, ArticleUpdateFullDTO articleUpdateFullDTO) {
        logger.info("Updating article with ID: {}", articleId);
        Article existingArticle = articleRepository.findById(articleId)
                .orElseThrow(() -> {
                    logger.error("Article with ID: {} not found for update.", articleId);
                    return new ResourceNotFoundException("Article not found with ID: " + articleId);
                });

        // Update Article entity fields
        existingArticle.setTitle(articleUpdateFullDTO.getTitle());
        existingArticle.setDescription(articleUpdateFullDTO.getDescription());
        existingArticle.setMainImage(articleUpdateFullDTO.getMainImage());

        // Update associated Content (in MongoDB)
        Content existingContent = contentRepository.findById(existingArticle.getContentId())
                .orElseThrow(() -> {
                    logger.error("Content not found for article ID: {} with content ID: {}. Cannot update.", articleId, existingArticle.getContentId());
                    return new ResourceNotFoundException("Content not found with ID: " + existingArticle.getContentId());
                });
        existingContent.setContent(articleUpdateFullDTO.getContent());
        contentRepository.save(existingContent);
        logger.info("Content updated for article ID: {}", articleId);


        Article updatedArticle = articleRepository.save(existingArticle);
        logger.info("Article with ID: {} updated successfully.", articleId);

        return buildArticleFullDTO(updatedArticle);
    }

    // --- Helper Methods ---

    @Transactional(readOnly = true)
    public Content createContentFromDTO(List<com.sarmo.contentservice.entity.ContentItem> contentItems) {
        Content content = new Content();
        // Corrected to use the actual setter from your Content entity
        content.setContent(contentItems); // <-- Corrected setter name
        return content;
    }

    private News createNewsFromDTO(NewsCreateDTO newsCreateDTO, User author, String contentId) {
        News news = new News();
        news.setAuthor(author);

        news.setMainImage(newsCreateDTO.getMainImage());
        news.setTitle(newsCreateDTO.getTitle());
        news.setDescription(newsCreateDTO.getDescription());
        news.setContentId(contentId);

        return news;
    }

    private Article createArticleFromDTO(ArticleCreateDTO articleCreateDTO, User author, String contentId) {
        Article article = new Article();
        article.setAuthor(author);

        article.setMainImage(articleCreateDTO.getMainImage());
        article.setTitle(articleCreateDTO.getTitle());
        article.setDescription(articleCreateDTO.getDescription());
        article.setContentId(contentId);

        return article;
    }

    @Transactional(readOnly = true)
    public NewsFullDTO buildNewsFullDTO(News news, List<NewsComment> comments) {
        logger.info("Building NewsFullDTO for news ID: {}", news.getId());
        Content content = contentRepository.findById(news.getContentId())
                .orElseThrow(() -> {
                    logger.error("Content not found for news ID: {} with content ID: {}", news.getId(), news.getContentId());
                    return new ResourceNotFoundException("Content not found with ID: " + news.getContentId());
                });

        NewsFullDTO newsFullDTO = new NewsFullDTO();
        newsFullDTO.setNews(news);
        newsFullDTO.setContent(content);

        newsFullDTO.setRelatedNews(randomService.getRandomNews(4));

        // TODO: If NewsFullDTO should include Author details directly, map from news.getAuthor() here (e.g., map news.getAuthor() to AuthorDTO)

        return newsFullDTO;
    }

    @Transactional(readOnly = true)
    public ArticleFullDTO buildArticleFullDTO(Article article) {
        logger.info("Building ArticleFullDTO for article ID: {}", article.getId());
        Content content = contentRepository.findById(article.getContentId())
                .orElseThrow(() -> {
                    logger.error("Content not found for article ID: {} with content ID: {}", article.getId(), article.getContentId());
                    return new ResourceNotFoundException("Content not found with ID: " + article.getContentId());
                });

        ArticleFullDTO articleFullDTO = new ArticleFullDTO();
        articleFullDTO.setArticle(article);
        articleFullDTO.setContent(content);
        articleFullDTO.setRelatedArticles(randomService.getRandomArticles(4));

        // TODO: If ArticleFullDTO should include Author details directly, map from article.getAuthor() here (e.g., map article.getAuthor() to AuthorDTO)

        return articleFullDTO;
    }
}