package com.sarmo.listingservice.service;

import com.sarmo.listingservice.entity.CategoryField;
import com.sarmo.listingservice.entity.Field;
import com.sarmo.listingservice.entity.ListingMongo;
import com.sarmo.listingservice.repository.ListingRepository; // Импортируем репозиторий
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MongoFilterService {

    private static final Logger logger = LoggerFactory.getLogger(MongoFilterService.class);

    private final MongoTemplate mongoTemplate;
    private final ListingRepository listingRepository;

    public MongoFilterService(MongoTemplate mongoTemplate, ListingRepository listingRepository) {
        this.mongoTemplate = mongoTemplate;
        this.listingRepository = listingRepository;
    }

    public Map<String, Object> getPopulatedFilters(Long categoryId) {
        logger.info("Getting populated filters for categoryId: {}", categoryId);
        Map<String, Object> populatedFilters = new HashMap<>();

        try {
            List<Long> activeListingIds = listingRepository.findActiveListingIdsByCategoryId(categoryId);
            if (activeListingIds.isEmpty()) {
                logger.info("No active listings found for categoryId: {}. Returning empty filters.", categoryId);
                return populatedFilters;
            }

            CategoryField categoryField = mongoTemplate.findOne(Query.query(Criteria.where("categoryId").is(categoryId)), CategoryField.class);
            if (categoryField == null) {
                logger.warn("Category fields not found for categoryId: {}", categoryId);
                return populatedFilters;
            }

            List<Field> fields = categoryField.getFields();

            for (Field field : fields) {
                if (field.getFilterable()) {
                    String fieldName = field.getName();
                    String fieldType = field.getType();

                    if (fieldType.equalsIgnoreCase("Integer") || fieldType.equalsIgnoreCase("Double") || fieldType.equalsIgnoreCase("Long")) {
                        // Передаем список activeListingIds в методы получения значений
                        getMinMaxValues(categoryId, fieldName, activeListingIds).ifPresent(minMax -> populatedFilters.put(fieldName, minMax));
                    } else if (fieldType.equalsIgnoreCase("String")) {
                        // Передаем список activeListingIds в методы получения значений
                        getDistinctStringValues(categoryId, fieldName, activeListingIds).ifPresent(distinctValues -> populatedFilters.put(fieldName, distinctValues));
                    } else if (fieldType.equalsIgnoreCase("Boolean")) {
                        populatedFilters.put(fieldName, Arrays.asList("true", "false"));
                    }
                }
            }

            logger.info("Populated filters: {}", populatedFilters);
        } catch (Exception e) {
            logger.error("Error getting populated filters for categoryId: {}", categoryId, e);
        }

        return populatedFilters;
    }

    private Optional<Map<String, Object>> getMinMaxValues(Long categoryId, String fieldName, List<Long> activeListingIds) {
        try {
            Query query = new Query(Criteria.where("categoryId").is(categoryId)
                    .and("listingId").in(activeListingIds));
            List<ListingMongo> listings = mongoTemplate.find(query, ListingMongo.class);

            if (listings.isEmpty()) {
                return Optional.empty();
            }

            List<Double> doubleValues = listings.stream()
                    .filter(listing -> listing.getFields() != null && listing.getFields().containsKey(fieldName) && listing.getFields().get(fieldName) instanceof Number)
                    .map(listing -> ((Number) listing.getFields().get(fieldName)).doubleValue())
                    .toList();

            if (doubleValues.isEmpty()) {
                return Optional.empty();
            }

            Double min = Collections.min(doubleValues);
            Double max = Collections.max(doubleValues);

            return Optional.of(Map.of("min", min, "max", max));
        } catch (Exception e) {
            logger.error("Error getting min/max values for categoryId: {}, fieldName: {}", categoryId, fieldName, e);
            return Optional.empty();
        }
    }

    private Optional<List<String>> getDistinctStringValues(Long categoryId, String fieldName, List<Long> activeListingIds) {
        try {
            Query query = new Query(Criteria.where("categoryId").is(categoryId)
                    .and("listingId").in(activeListingIds));
            List<ListingMongo> listings = mongoTemplate.find(query, ListingMongo.class);

            if (listings.isEmpty()) {
                return Optional.empty();
            }

            List<String> distinctValues = listings.stream()
                    .filter(listing -> listing.getFields() != null && listing.getFields().containsKey(fieldName) && listing.getFields().get(fieldName) instanceof String)
                    .map(listing -> (String) listing.getFields().get(fieldName))
                    .distinct()
                    .collect(Collectors.toList());

            return Optional.of(distinctValues);
        } catch (Exception e) {
            logger.error("Error getting distinct string values for categoryId: {}, fieldName: {}", categoryId, fieldName, e);
            return Optional.empty();
        }
    }
}