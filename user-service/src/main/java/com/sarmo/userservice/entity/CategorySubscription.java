package com.sarmo.userservice.entity;

public class CategorySubscription {
    private String frequency;
    private boolean isActive;
    private Object filters; // Объект с фильтрами

    public  CategorySubscription() {}

    public CategorySubscription(String frequency, boolean isActive, Object filters) {
        this.frequency = frequency;
        this.isActive = isActive;
        this.filters = filters;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
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
