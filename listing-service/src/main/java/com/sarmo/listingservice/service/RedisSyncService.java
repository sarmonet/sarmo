package com.sarmo.listingservice.service;

import com.sarmo.listingservice.repository.ListingRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class RedisSyncService {

    private final StringRedisTemplate redisTemplate;
    private final ListingRepository listingRepository;

    public RedisSyncService(StringRedisTemplate redisTemplate, ListingRepository listingRepository) {
        this.redisTemplate = redisTemplate;
        this.listingRepository = listingRepository;
    }

    @Scheduled(fixedRate = 300000)
    @Transactional
    public void syncViewsFromRedisToDb() {
        Set<String> keys = redisTemplate.keys("views:listing:*");
        if (!keys.isEmpty()) {
            for (String key : keys) {
                try {
                    Long listingId = Long.parseLong(key.split(":")[2]);
                    String viewCountStr = redisTemplate.opsForValue().get(key);

                    if (viewCountStr != null) {
                        long viewsFromRedis = Long.parseLong(viewCountStr);

                        Long oldViewsInDb = listingRepository.findViewCountById(listingId);
                        long currentTotalViews = (oldViewsInDb != null ? oldViewsInDb : 0L);

                        listingRepository.updateViewCount(listingId, currentTotalViews + viewsFromRedis);
                        redisTemplate.delete(key);
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Error parsing Redis key or value: " + key + ", Error: " + e.getMessage());
                } catch (Exception e) {
                    System.err.println("Unexpected error during Redis to DB sync for key: " + key + ", Error: " + e.getMessage());
                }
            }
        }
    }
}