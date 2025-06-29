package com.sarmo.noticeservice.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sarmo.noticeservice.dto.*;
import com.sarmo.noticeservice.entity.CategorySubscription;
import com.sarmo.noticeservice.entity.NotificationSettings;
import com.sarmo.noticeservice.entity.User;
import com.sarmo.noticeservice.enums.FrequencyType;
import com.sarmo.noticeservice.repository.CategorySubscriptionRepository;
import com.sarmo.noticeservice.repository.NotificationSettingsRepository;
import com.sarmo.noticeservice.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NotificationSettingsService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationSettingsService.class);

    private final NotificationSettingsRepository notificationSettingsRepository;
    private final CategorySubscriptionRepository categorySubscriptionRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final CategoryNameExtractorService categoryNameExtractorService;

    public NotificationSettingsService(NotificationSettingsRepository notificationSettingsRepository,
                                       CategorySubscriptionRepository categorySubscriptionRepository,
                                       UserRepository userRepository, ObjectMapper objectMapper,
                                       CategoryNameExtractorService categoryNameExtractorService) {
        this.notificationSettingsRepository = notificationSettingsRepository;
        this.categorySubscriptionRepository = categorySubscriptionRepository;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
        this.categoryNameExtractorService = categoryNameExtractorService;
    }

    @Transactional
    public NotificationSettingsDto createNotificationSettingsWithSubscription(Long userId, NotificationSettingsCreateDto createDto) {
        logger.info("Creating notification settings for user ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> logAndThrowIllegalArgumentException("User with ID {} not found", userId));

        NotificationSettings notificationSettings = new NotificationSettings();
        notificationSettings.setUser(user);
        notificationSettings.setPreferredCommunicationChannel(createDto.getPreferredCommunicationChannel());

        CategorySubscription categorySubscription = convertCreateDtoToEntity(createDto.getCategorySubscription(), notificationSettings);
        notificationSettings.setCategorySubscription(categorySubscription);

        NotificationSettings savedSettings = notificationSettingsRepository.save(notificationSettings);
        logger.info("Notification settings created with ID: {}", savedSettings.getId());
        return convertToDtoWithCategoryNames(savedSettings);
    }

    public List<NotificationSettingsDto> getAllNotificationSettings() {
        logger.info("Fetching all notification settings");
        List<NotificationSettings> allSettings = notificationSettingsRepository.findAll();
        logger.info("Found {} notification settings", allSettings.size());
        return allSettings.stream()
                .map(this::convertToDtoWithCategoryNames)
                .collect(Collectors.toList());
    }

    public List<NotificationSettingsDto> getAllNotificationSettingsByUserId(Long userId) {
        logger.info("Fetching all notification settings for user ID: {}", userId);
        List<NotificationSettings> allSettings = notificationSettingsRepository.findByUser_Id(userId);
        logger.info("Found {} notification settings for user ID: {}", allSettings.size(), userId);
        return allSettings.stream()
                .map(this::convertToDtoWithCategoryNames)
                .collect(Collectors.toList());
    }

    public Optional<NotificationSettings> getNotificationSettingsById(Long id) {
        logger.info("Fetching notification settings by ID: {}", id);
        return notificationSettingsRepository.findById(id);
    }

    @Transactional
    public NotificationSettings updateNotificationSettings(Long id, NotificationSettingsDto updateDto) {
        logger.info("Updating notification settings with ID: {}", id);
        NotificationSettings existingSettings = notificationSettingsRepository.findById(id)
                .orElseThrow(() -> logAndThrowIllegalArgumentException("Notification settings with ID {} not found", id));

        if (updateDto.getPreferredCommunicationChannel() != null) {
            existingSettings.setPreferredCommunicationChannel(updateDto.getPreferredCommunicationChannel());
        }

        CategorySubscriptionDto categoryUpdateDto = updateDto.getCategorySubscription();
        if (categoryUpdateDto != null) {
            CategorySubscription existingSubscription = existingSettings.getCategorySubscription();
            if (existingSubscription == null) {
                CategorySubscription newSubscription = convertDtoToEntity(categoryUpdateDto, existingSettings);
                existingSettings.setCategorySubscription(categorySubscriptionRepository.save(newSubscription));
                logger.info("Created new category subscription for notification settings ID: {}", id);
            } else {
                updateCategorySubscriptionEntity(existingSubscription, categoryUpdateDto);
                categorySubscriptionRepository.save(existingSubscription);
                logger.info("Updated category subscription for notification settings ID: {}", id);
            }
        }

        NotificationSettings updatedSettings = notificationSettingsRepository.save(existingSettings);
        logger.info("Notification settings with ID {} updated successfully", id);
        return updatedSettings;
    }

    private void updateCategorySubscriptionEntity(CategorySubscription existingSubscription, CategorySubscriptionDto updateDto) {
        if (updateDto.getFrequency() != null) {
            existingSubscription.setFrequency(updateDto.getFrequency());
        }
        if (updateDto.getActive() != null) {
            existingSubscription.setActive(updateDto.getActive());
        }
        if (updateDto.getFilters() != null) {
            existingSubscription.setFilters(convertFiltersToString(updateDto.getFilters()));
        }
    }

    private CategorySubscription convertDtoToEntity(CategorySubscriptionDto dto, NotificationSettings settings) {
        CategorySubscription entity = new CategorySubscription();
        entity.setFrequency(dto.getFrequency() != null ? dto.getFrequency() : FrequencyType.DAILY);
        entity.setActive(dto.getActive());
        entity.setFilters(convertFiltersToString(dto.getFilters()));
        entity.setNotificationSettings(settings);
        return entity;
    }

    private String convertFiltersToString(Map<String, Object> filters) {
        if (filters == null || filters.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(filters);
        } catch (IOException e) {
            logger.error("Error converting filters to JSON string: {}", e.getMessage());
            return null;
        }
    }

    @Transactional
    public void deleteNotificationSettings(Long id) {
        logger.info("Deleting notification settings with ID: {}", id);
        if (notificationSettingsRepository.existsById(id)) {
            notificationSettingsRepository.deleteById(id);
            logger.info("Notification settings with ID {} deleted successfully", id);
        } else {
            logger.warn("Notification settings with ID {} not found for deletion", id);
        }
    }

    private NotificationSettingsDto convertToDtoWithCategoryNames(NotificationSettings settings) {
        NotificationSettingsDto dto = new NotificationSettingsDto();
        dto.setId(settings.getId());
        dto.setUserId(settings.getUser().getId());
        dto.setPreferredCommunicationChannel(settings.getPreferredCommunicationChannel());
        if (settings.getCategorySubscription() != null) {
            dto.setCategorySubscription(convertToDtoWithCategoryNames(settings.getCategorySubscription()));
        }
        return dto;
    }

    private CategorySubscriptionDto convertToDtoWithCategoryNames(CategorySubscription subscription) {
        CategorySubscriptionDto dto = new CategorySubscriptionDto();
        dto.setId(subscription.getId());
        dto.setFrequency(subscription.getFrequency());
        dto.setActive(subscription.isActive());
        dto.setFilters(enrichFiltersWithCategoryNames(parseFilters(subscription.getFilters())));
        dto.setCreatedAt(subscription.getCreatedAt());
        return dto;
    }

    private Map<String, Object> parseFilters(String filtersJson) {
        if (filtersJson == null || filtersJson.isEmpty()) {
            return new HashMap<>();
        }
        try {
            return objectMapper.readValue(filtersJson, new TypeReference<Map<String, Object>>() {});
        } catch (IOException e) {
            logger.error("Error parsing filters JSON: {}", filtersJson, e);
            return new HashMap<>();
        }
    }

    private Map<String, Object> enrichFiltersWithCategoryNames(Map<String, Object> parsedFilters) {
        if (parsedFilters == null || parsedFilters.isEmpty()) {
            return new HashMap<>();
        }

        Map<String, Object> enrichedFilters = new HashMap<>(parsedFilters);
        Optional.ofNullable(enrichedFilters.get("filteredParams"))
                .filter(rawFilteredParams -> rawFilteredParams instanceof Map)
                .map(rawFilteredParams -> (Map<String, Object>) rawFilteredParams)
                .flatMap(filteredParams -> Optional.ofNullable(filteredParams.get("sqlFilters"))
                        .filter(rawSqlFilters -> rawSqlFilters instanceof Map)
                        .map(rawSqlFilters -> (Map<String, Object>) rawSqlFilters))
                .ifPresent(sqlFilters -> {
                    Optional.ofNullable(sqlFilters.get("subCategory"))
                            .filter(subCategoryId -> subCategoryId instanceof Integer)
                            .map(subCategoryId -> (Integer) subCategoryId)
                            .ifPresent(subCategoryId -> {
                                CategoryAndSubCategoryNamesDto namesDto = categoryNameExtractorService.getCategoryAndSubCategoryNames(subCategoryId);
                                Optional.ofNullable(namesDto.getSubCategoryName())
                                        .ifPresent(name -> sqlFilters.put("subCategoryName", name));
                                Optional.ofNullable(namesDto.getCategoryName())
                                        .ifPresent(name -> sqlFilters.put("categoryName", name));
                            });
                });

        return enrichedFilters;
    }

    private CategorySubscription convertCreateDtoToEntity(CategorySubscriptionCreateDto createDto, NotificationSettings notificationSettings) {
        CategorySubscription categorySubscription = new CategorySubscription();
        categorySubscription.setFrequency(createDto.getFrequency());
        categorySubscription.setActive(createDto.isActive());
        try {
            categorySubscription.setFilters(objectMapper.writeValueAsString(createDto.getFilters()));
        } catch (IOException e) {
            logger.error("Failed to serialize filters to JSON: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to serialize filters to JSON", e);
        }
        categorySubscription.setNotificationSettings(notificationSettings);
        return categorySubscription;
    }

    private IllegalArgumentException logAndThrowIllegalArgumentException(String message, Object... args) {
        String formattedMessage = String.format(message, args);
        logger.error(formattedMessage);
        return new IllegalArgumentException(formattedMessage);
    }
}