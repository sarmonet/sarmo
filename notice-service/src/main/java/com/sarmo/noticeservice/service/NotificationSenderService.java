package com.sarmo.noticeservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sarmo.noticeservice.dto.CountNewListingsRequestDto;
import com.sarmo.noticeservice.entity.CategorySubscription;
import com.sarmo.noticeservice.entity.NotificationSettings;
import com.sarmo.noticeservice.enums.FrequencyType;
import com.sarmo.noticeservice.repository.CategorySubscriptionRepository;
import com.sarmo.notificationservice.proto.NotificationRequest;
import com.sarmo.notificationservice.proto.NotificationServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class NotificationSenderService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationSenderService.class);

    private final CategorySubscriptionRepository categorySubscriptionRepository;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    @Value("${listing.service.url}")
    private String listingServiceUrl;

    @Value("${catalog.url}")
    private String catalogUrl;

    @GrpcClient("notification-service")
    private NotificationServiceGrpc.NotificationServiceBlockingStub notificationServiceBlockingStub;

    public NotificationSenderService(CategorySubscriptionRepository categorySubscriptionRepository,
                                     RestClient.Builder restClientBuilder,
                                     ObjectMapper objectMapper) {
        this.categorySubscriptionRepository = categorySubscriptionRepository;
        this.restClient = restClientBuilder.baseUrl(listingServiceUrl).build();
        this.objectMapper = objectMapper;
    }

    @Scheduled(cron = "0 0/30 * * * *")
    public void processNotifications() {
        LocalTime now = LocalTime.now().truncatedTo(ChronoUnit.MINUTES);
        List<CategorySubscription> subscriptionsToSend = categorySubscriptionRepository.findAllActiveByCreatedAtHourAndMinute(now.getHour(), now.getMinute());

        logger.info("Found {} subscriptions to process at {}", subscriptionsToSend.size(), now);

        for (CategorySubscription subscription : subscriptionsToSend) {
            NotificationSettings settings = subscription.getNotificationSettings();
            if (settings != null && settings.getUser() != null) {
                LocalDateTime createdAt = subscription.getCreatedAt();
                FrequencyType frequency = subscription.getFrequency();
                String filters = subscription.getFilters();
                String userEmail = settings.getUser().getEmail();
                Long userId = settings.getUser().getId();

                LocalDateTime from = null;
                LocalDateTime to = LocalDateTime.now();

                switch (frequency) {
                    case DAILY:
                        from = LocalDateTime.now().minusDays(1);
                        break;
                    case WEEKLY:
                        from = LocalDateTime.now().minusWeeks(1);
                        break;
                    case MONTHLY:
                        from = LocalDateTime.now().minusMonths(1);
                        break;
                    default:
                        logger.warn("Unsupported frequency type: {} for subscription ID: {}", frequency, subscription.getId());
                        continue;
                }

                int newListingsCount = getNewListingsCount(filters, from, to);
                if (newListingsCount > 0) {
                    String catalogLink = generateCatalogLink(filters);
                    String subject = String.format("New listings for your subscription (%s)", frequency.toString().toLowerCase());
                    String body = String.format("Hello! There are %d new listings matching your preferences for the last %s. Check them out here: %s",
                            newListingsCount, frequency.toString().toLowerCase(), catalogLink);

                    sendNotificationGrpc(userEmail, subject, body);
                    logger.info("Sent gRPC notification request for user {} ({}) about {} new listings ({}), link: {}", userId, userEmail, newListingsCount, frequency, catalogLink);
                } else {
                    logger.info("No new listings found for user {} ({}) with frequency {}", userId, userEmail, frequency);
                }
            }
        }
    }

    private String generateCatalogLink(String filtersJson) {
        StringBuilder sb = new StringBuilder(catalogUrl);
        sb.append("?");

        try {
            JsonNode filtersNode = objectMapper.readTree(filtersJson);
            JsonNode sqlFiltersNode = filtersNode.get("sqlFilters");
            JsonNode mongoFiltersNode = filtersNode.get("mongoFilters");
            JsonNode sortByNode = filtersNode.get("sortBy");
            JsonNode sortOrderNode = filtersNode.get("sortOrder");

            if (sortByNode != null) {
                sb.append("sortBy=").append(encodeValue(sortByNode.asText())).append("&");
            }
            if (sortOrderNode != null) {
                sb.append("sortOrder=").append(encodeValue(sortOrderNode.asText())).append("&");
            }

            if (sqlFiltersNode != null) {
                sqlFiltersNode.fields().forEachRemaining(entry -> {
                    sb.append(encodeValue(entry.getKey())).append("=").append(encodeValue(entry.getValue().asText())).append("&");
                });
            }

            if (mongoFiltersNode != null && mongoFiltersNode.isObject()) {
                mongoFiltersNode.fields().forEachRemaining(entry -> {
                    sb.append(encodeValue(entry.getKey())).append("=").append(encodeValue(entry.getValue().asText())).append("&");
                });
            }

            // Удалите последний '&', если он есть
            if (sb.length() > catalogUrl.length() + 1) {
                sb.deleteCharAt(sb.length() - 1);
            }

            return sb.toString();

        } catch (IOException e) {
            logger.error("Error при парсинге JSON фильтров для ссылки: {}", e.getMessage());
            return catalogUrl; // Вернуть базовый URL в случае ошибки
        }
    }

    private static String encodeValue(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private int getNewListingsCount(String filters, LocalDateTime from, LocalDateTime to) {
        try {
            CountNewListingsRequestDto requestDto = new CountNewListingsRequestDto(from, to, filters);
            Integer count = restClient.post()
                    .uri("/api/v1/listing/count-new")
                    .body(requestDto)
                    .retrieve()
                    .body(Integer.class);
            return count != null ? count : 0;
        } catch (Exception e) {
            logger.error("Error communicating with listing service: {}", e.getMessage());
            return 0;
        }
    }

    private void sendNotificationGrpc(String email, String subject, String body) {
        NotificationRequest request = NotificationRequest.newBuilder()
                .setEmail(email)
                .setSubject(subject)
                .setBody(body)
                .build();

        try {
            com.sarmo.notificationservice.proto.NotificationResponse response = notificationServiceBlockingStub.sendNewListingsNotification(request);
            if (response.getSuccess()) {
                logger.info("Successfully sent notification email to {} via gRPC: {}", email, response.getMessage());
            } else {
                logger.error("Failed to send notification email to {} via gRPC: {}", email, response.getMessage());
            }
        } catch (Exception e) {
            logger.error("Error during gRPC call to notification-service: {}", e.getMessage());
        }
    }

}