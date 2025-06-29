package com.sarmo.listingservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import java.util.Map;

public class ListingFilterDto {
    @Valid
    private SqlListingFilterDto sqlFilters;
    private Map<String, Object> mongoFilters;

    private String sortBy;
    private String sortOrder; // "asc" or "desc"
    @Min(0)
    private Integer page; // page number
    @Min(1)
    private Integer size; // page count

    public SqlListingFilterDto getSqlFilters() {
        return sqlFilters;
    }

    public void setSqlFilters(SqlListingFilterDto sqlFilters) {
        this.sqlFilters = sqlFilters;
    }

    public Map<String, Object> getMongoFilters() {
        return mongoFilters;
    }

    public void setMongoFilters(Map<String, Object> mongoFilters) {
        this.mongoFilters = mongoFilters;
    }

    // Геттеры и сеттеры для НОВЫХ полей
    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "ListingFilterDto{" +
                "sqlFilters=" + sqlFilters +
                ", mongoFilters=" + mongoFilters +
                ", sortBy='" + sortBy + '\'' +
                ", sortOrder='" + sortOrder + '\'' +
                ", page=" + page +
                ", size=" + size +
                '}';
    }
}