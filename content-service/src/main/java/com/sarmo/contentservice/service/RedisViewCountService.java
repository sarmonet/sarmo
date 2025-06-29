package com.sarmo.contentservice.service;


import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisViewCountService {

    private final StringRedisTemplate redisTemplate;

    public RedisViewCountService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void incrementArticleViewCount(Long articleId) {
        String key = "views:article:" + articleId;
        redisTemplate.opsForValue().increment(key);
    }

    public void incrementNewsViewCount(Long newsId) {
        String key = "views:news:" + newsId;
        redisTemplate.opsForValue().increment(key);
    }

    public Long getArticleViewCount(Long articleId) {
        String key = "views:article:" + articleId;
        String viewCount = redisTemplate.opsForValue().get(key);
        return viewCount != null ? Long.parseLong(viewCount) : 0L;
    }

    public Long getNewsViewCount(Long newsId) {
        String key = "views:article:" + newsId;
        String viewCount = redisTemplate.opsForValue().get(key);
        return viewCount != null ? Long.parseLong(viewCount) : 0L;
    }
}