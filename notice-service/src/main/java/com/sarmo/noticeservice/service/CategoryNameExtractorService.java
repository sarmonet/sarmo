package com.sarmo.noticeservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import com.sarmo.noticeservice.dto.CategoryAndSubCategoryNamesDto;
import org.springframework.http.HttpStatus;

@Service
public class CategoryNameExtractorService {

    private static final Logger logger = LoggerFactory.getLogger(CategoryNameExtractorService.class);

    private final RestClient restClient;

    @Value("${listing.service.url}")
    private String catalogServiceUrl;

    public CategoryNameExtractorService(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.build();
    }

    public CategoryAndSubCategoryNamesDto getCategoryAndSubCategoryNames(Integer subCategoryId) {
        CategoryAndSubCategoryNamesDto namesDto = new CategoryAndSubCategoryNamesDto();
        try {
            String url = catalogServiceUrl + "/api/v1/listing/subcategory/" + subCategoryId;
            JsonNode responseNode = restClient.get() // Renamed to responseNode
                    .uri(url)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (request, clientResponse) -> {
                        logger.error("Error fetching category and subcategory names for subcategory ID {}: {} {}",
                                subCategoryId, clientResponse.getStatusCode(), clientResponse.getBody());
                        throw new RuntimeException(String.format("Error fetching data from catalog service: %s", clientResponse.getStatusCode()));
                    })
                    .body(JsonNode.class);

            if (responseNode != null) {
                if (responseNode.has("name")) {
                    namesDto.setSubCategoryName(responseNode.get("name").asText());
                }
                if (responseNode.has("category") && responseNode.get("category").isObject()) {
                    JsonNode categoryNode = responseNode.get("category");
                    if (categoryNode.has("name")) {
                        namesDto.setCategoryName(categoryNode.get("name").asText());
                    }
                }
            } else {
                logger.warn("Empty response received for subcategory ID: {}", subCategoryId);
            }

        } catch (Exception e) {
            logger.error("Error processing response for subcategory ID {}: {}", subCategoryId, e.getMessage());
        }
        return namesDto;
    }
}