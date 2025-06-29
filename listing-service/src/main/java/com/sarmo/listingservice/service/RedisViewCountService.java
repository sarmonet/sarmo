package com.sarmo.listingservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisViewCountService {

    private final StringRedisTemplate redisTemplate;

    public RedisViewCountService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void incrementViewCount(Long listingId) {
        String key = "views:listing:" + listingId;
        redisTemplate.opsForValue().increment(key);
    }

    public Long getViewCount(Long listingId) {
        String key = "views:listing:" + listingId;
        String viewCount = redisTemplate.opsForValue().get(key);
        return viewCount != null ? Long.parseLong(viewCount) : 0L;
    }
}