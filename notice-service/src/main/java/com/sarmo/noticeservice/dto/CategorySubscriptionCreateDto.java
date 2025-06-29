package com.sarmo.noticeservice.dto;

import com.sarmo.noticeservice.enums.FrequencyType;

public class CategorySubscriptionCreateDto {
    private FrequencyType frequency;
    private boolean isActive;
    private Object filters;

    public CategorySubscriptionCreateDto() {}

    public CategorySubscriptionCreateDto(FrequencyType frequency, boolean isActive, Object filters) {
        this.frequency = frequency;
        this.isActive = isActive;
        this.filters = filters;
    }

    public FrequencyType getFrequency() {
        return frequency;
    }

    public void setFrequency(FrequencyType frequency) {
        this.frequency = frequency;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Object getFilters() {
        return filters;
    }

    public void setFilters(Object filters) {
        this.filters = filters;
    }
}
