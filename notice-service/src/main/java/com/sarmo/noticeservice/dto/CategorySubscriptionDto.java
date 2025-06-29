package com.sarmo.noticeservice.dto;

import com.sarmo.noticeservice.enums.FrequencyType;
import java.time.LocalDateTime;
import java.util.Map;

public class CategorySubscriptionDto {
    private Long id;
    private FrequencyType frequency;
    private Boolean isActive;
    private Map<String, Object> filters;
    private LocalDateTime createdAt;

    public CategorySubscriptionDto() {
    }

    public CategorySubscriptionDto(Long id, FrequencyType frequency, Boolean isActive, Map<String, Object> filters, LocalDateTime createdAt) {
        this.id = id;
        this.frequency = frequency;
        this.isActive = isActive;
        this.filters = filters;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FrequencyType getFrequency() {
        return frequency;
    }

    public void setFrequency(FrequencyType frequency) {
        this.frequency = frequency;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Map<String, Object> getFilters() {
        return filters;
    }

    public void setFilters(Map<String, Object> filters) {
        this.filters = filters;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}