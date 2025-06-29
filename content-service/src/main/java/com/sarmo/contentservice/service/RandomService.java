package com.sarmo.contentservice.service;

import com.sarmo.contentservice.entity.Article;
import com.sarmo.contentservice.entity.News;
import com.sarmo.contentservice.repository.ArticleRepository;
import com.sarmo.contentservice.repository.NewsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RandomService {

    private static final Logger logger = LoggerFactory.getLogger(RandomService.class);

    private final NewsRepository newsRepository;
    private final ArticleRepository articleRepository;

    public RandomService(NewsRepository newsRepository, ArticleRepository articleRepository) {
        this.newsRepository = newsRepository;
        this.articleRepository = articleRepository;
    }

    public List<News> getRandomNews(int count) {
        try {
            logger.info("Получение {} случайных новостей", count);
            return newsRepository.findRandomNews(count);
        } catch (Exception e) {
            logger.error("Ошибка при получении случайных новостей: {}", e.getMessage());
            throw e; // Пробросить исключение для обработки в контроллере
        }
    }

    public List<Article> getRandomArticles(int count) {
        try {
            logger.info("Получение {} случайных блогов", count);
            return articleRepository.findRandomArticles(count);
        } catch (Exception e) {
            logger.error("Ошибка при получении случайных блогов: {}", e.getMessage());
            throw e; // Пробросить исключение для обработки в контроллере
        }
    }
}
