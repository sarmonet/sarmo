package com.sarmo.listingservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "packaging_service_info")
public class PackagingServiceInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String pageDesignName = "Оформление страницы";

    @NotNull
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal pageDesignPrice;

    @Column(columnDefinition = "TEXT")
    private String pageDesignDescription;

    @NotNull
    private String presentationName = "Презентация";

    @NotNull
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal presentationPrice;

    @Column(columnDefinition = "TEXT")
    private String presentationDescription;

    @NotNull
    private String financialModelName = "Финансовая модель";

    @NotNull
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal financialModelPrice;

    @Column(columnDefinition = "TEXT")
    private String financialModelDescription;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal discountPercentage = BigDecimal.ZERO;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;


    public PackagingServiceInfo() {
    }

    public PackagingServiceInfo(BigDecimal pageDesignPrice, BigDecimal presentationPrice, BigDecimal financialModelPrice, BigDecimal discountPercentage) {
        this.pageDesignPrice = pageDesignPrice;
        this.presentationPrice = presentationPrice;
        this.financialModelPrice = financialModelPrice;
        this.discountPercentage = discountPercentage;
    }

    public PackagingServiceInfo(String pageDesignName, BigDecimal pageDesignPrice, String pageDesignDescription,
                                String presentationName, BigDecimal presentationPrice, String presentationDescription,
                                String financialModelName, BigDecimal financialModelPrice, String financialModelDescription,
                                BigDecimal discountPercentage) {
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

}