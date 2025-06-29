package com.sarmo.listingservice.dto; // Убедитесь, что пакет правильный

import java.math.BigDecimal;

public class PackagingServiceInfoRequestDto {

    private BigDecimal pageDesignPrice;
    private String pageDesignDescription;

    private BigDecimal presentationPrice;
    private String presentationDescription;

    private BigDecimal financialModelPrice;
    private String financialModelDescription;

    private BigDecimal discountPercentage;

    public PackagingServiceInfoRequestDto() {
    }

    public PackagingServiceInfoRequestDto(BigDecimal pageDesignPrice, String pageDesignDescription,
                                          BigDecimal presentationPrice, String presentationDescription,
                                          BigDecimal financialModelPrice, String financialModelDescription,
                                          BigDecimal discountPercentage) {
        this.pageDesignPrice = pageDesignPrice;
        this.pageDesignDescription = pageDesignDescription;
        this.presentationPrice = presentationPrice;
        this.presentationDescription = presentationDescription;
        this.financialModelPrice = financialModelPrice;
        this.financialModelDescription = financialModelDescription;
        this.discountPercentage = discountPercentage;
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

}