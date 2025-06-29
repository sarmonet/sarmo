package com.sarmo.listingservice.dto;

import java.math.BigDecimal;

public class SqlListingFilterDto {

    private String title;
    private Long category;
    private Long subCategory;
    private String country;
    private String city;
    private Boolean isInvest;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;


    public SqlListingFilterDto(){}

    public SqlListingFilterDto(String title, Long category, Long subCategory, String country, String city, Boolean isInvest, BigDecimal minPrice, BigDecimal maxPrice) {
        this.title = title;
        this.category = category;
        this.subCategory = subCategory;
        this.country = country;
        this.city = city;
        this.isInvest = isInvest;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getCategory() {
        return category;
    }

    public void setCategory(Long category) {
        this.category = category;
    }

    public Long getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(Long subCategory) {
        this.subCategory = subCategory;
    }

    public Boolean getInvest() {
        return isInvest;
    }

    public void setInvest(Boolean invest) {
        isInvest = invest;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }


    public BigDecimal getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(BigDecimal minPrice) {
        this.minPrice = minPrice;
    }

    public BigDecimal getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(BigDecimal maxPrice) {
        this.maxPrice = maxPrice;
    }

}