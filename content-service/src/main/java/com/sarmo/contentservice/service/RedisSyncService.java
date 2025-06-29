package com.sarmo.contentservice.service;

import com.sarmo.contentservice.repository.ArticleRepository;
import com.sarmo.contentservice.repository.NewsRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class RedisSyncService {

    private final StringRedisTemplate redisTemplate;

    private final RedisViewCountService redisViewCountService;

    private final ArticleRepository articleRepository;

    private final NewsRepository newsRepository;

    public RedisSyncService(StringRedisTemplate redisTemplate, RedisViewCountService redisViewCountService, ArticleRepository articleRepository, NewsRepository newsRepository) {
        this.redisTemplate = redisTemplate;
        this.redisViewCountService = redisViewCountService;
        this.articleRepository = articleRepository;
        this.newsRepository = newsRepository;
    }

    @Scheduled(fixedRate = 300000)
    @Transactional
    public void syncArticleViewsFromRedisToDb() {
        Set<String> keys = redisTemplate.keys("views:article:*");
        if (!keys.isEmpty()) {
            for (String key : keys) {
                Long listingId = Long.parseLong(key.split(":")[2]);
                String viewCount = redisTemplate.opsForValue().get(key);
                if (viewCount != null) {
                    long views = Long.parseLong(viewCount);
                    articleRepository.incrementViewCount(listingId, views);
                    redisTemplate.delete(key);
                }
            }
        }
    }


    @Scheduled(fixedRate = 300000)
    @Transactional
    public void syncNewsViewsFromRedisToDb() {
        Set<String> keys = redisTemplate.keys("views:news:*");
        if (!keys.isEmpty()) {
            for (String key : keys) {
                Long listingId = Long.parseLong(key.split(":")[2]);
                String viewCount = redisTemplate.opsForValue().get(key);
                if (viewCount != null) {
                    long views = Long.parseLong(viewCount);
                    newsRepository.incrementViewCount(listingId, views);
                    redisTemplate.delete(key);
                }
            }
        }
    }


}