package com.sarmo.listingservice.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PackagingServiceInfoResponseDto {

    private Long id;

    private String pageDesignName;
    private BigDecimal pageDesignPrice;
    private String pageDesignDescription;

    private String presentationName;
    private BigDecimal presentationPrice;
    private String presentationDescription;

    private String financialModelName;
    private BigDecimal financialModelPrice;
    private String financialModelDescription;

    // Add fields for other fixed services here

    private BigDecimal discountPercentage;

    private BigDecimal totalPackagePrice; // Renamed field

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PackagingServiceInfoResponseDto() {
    }

    // Constructor without totalPackagePrice (calculated by service)
    public PackagingServiceInfoResponseDto(Long id, String pageDesignName, BigDecimal pageDesignPrice, String pageDesignDescription,
                                           String presentationName, BigDecimal presentationPrice, String presentationDescription,
                                           String financialModelName, BigDecimal financialModelPrice, String financialModelDescription,
                                           BigDecimal discountPercentage, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.pageDesignName = pageDesignName;
        this.pageDesignPrice = pageDesignPrice;
        this.pageDesignDescription = pageDesignDescription;
        this.presentationName = presentationName;
        this.presentationPrice = presentationPrice;
        this.presentationDescription = presentationDescription;
        this.financialModelName = financialModelName;
        this.financialModelPrice = financialModelPrice;
        this.financialModelDescription = financialModelDescription;
        this.discountPercentage = discountPercentage;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Constructor including totalPackagePrice
    public PackagingServiceInfoResponseDto(Long id, String pageDesignName, BigDecimal pageDesignPrice, String pageDesignDescription,
                                           String presentationName, BigDecimal presentationPrice, String presentationDescription,
                                           String financialModelName, BigDecimal financialModelPrice, String financialModelDescription,
                                           BigDecimal discountPercentage, BigDecimal totalPackagePrice, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.pageDesignName = pageDesignName;
        this.pageDesignPrice = pageDesignPrice;
        this.pageDesignDescription = pageDesignDescription;
        this.presentationName = presentationName;
        this.presentationPrice = presentationPrice;
        this.presentationDescription = presentationDescription;
        this.financialModelName = financialModelName;
        this.financialModelPrice = financialModelPrice;
        this.financialModelDescription = financialModelDescription;
        this.discountPercentage = discountPercentage;
        this.totalPackagePrice = totalPackagePrice;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPageDesignName() {
        return pageDesignName;
    }

    public void setPageDesignName(String pageDesignName) {
        this.pageDesignName = pageDesignName;
    }

    public BigDecimal getPageDesignPrice() {
        return pageDesignPrice;
    }

    public void setPageDesignPrice(BigDecimal pageDesignPrice) {
        this.pageDesignPrice = pageDesignPrice;
    }

    public String getPageDesignDescription() {
        return pageDesignDescription;
    }

    public void setPageDesignDescription(String pageDesignDescription) {
        this.pageDesignDescription = pageDesignDescription;
    }

    public String getPresentationName() {
        return presentationName;
    }

    public void setPresentationName(String presentationName) {
        this.presentationName = presentationName;
    }

    public BigDecimal getPresentationPrice() {
        return presentationPrice;
    }

    public void setPresentationPrice(BigDecimal presentationPrice) {
        this.presentationPrice = presentationPrice;
    }

    public String getPresentationDescription() {
        return presentationDescription;
    }

    public void setPresentationDescription(String presentationDescription) {
        this.presentationDescription = presentationDescription;
    }

    public String getFinancialModelName() {
        return financialModelName;
    }

    public void setFinancialModelName(String financialModelName) {
        this.financialModelName = financialModelName;
    }

    public BigDecimal getFinancialModelPrice() {
        return financialModelPrice;
    }

    public void setFinancialModelPrice(BigDecimal financialModelPrice) {
        this.financialModelPrice = financialModelPrice;
    }

    public String getFinancialModelDescription() {
        return financialModelDescription;
    }

    public void setFinancialModelDescription(String financialModelDescription) {
        this.financialModelDescription = financialModelDescription;
    }

    public BigDecimal getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(BigDecimal discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public BigDecimal getTotalPackagePrice() { // Renamed getter
        return totalPackagePrice;
    }

    public void setTotalPackagePrice(BigDecimal totalPackagePrice) { // Renamed setter
        this.totalPackagePrice = totalPackagePrice;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

}