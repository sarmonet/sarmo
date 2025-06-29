package com.sarmo.subscriptionservice.dto;

import com.sarmo.subscriptionservice.enums.BillingCycle;
import java.math.BigDecimal;
import java.util.List;

public class CreateSubscriptionPlanDTO {
    private String name;
    private BigDecimal price;
    private BillingCycle billingCycle;
    private String description;
    private List<CreatePlanFeatureDTO> features;

    public CreateSubscriptionPlanDTO() {
    }

    public CreateSubscriptionPlanDTO(String name, BigDecimal price, BillingCycle billingCycle, String description, List<CreatePlanFeatureDTO> features) {
        this.name = name;
        this.price = price;
        this.billingCycle = billingCycle;
        this.description = description;
        this.features = features;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BillingCycle getBillingCycle() {
        return billingCycle;
    }

    public void setBillingCycle(BillingCycle billingCycle) {
        this.billingCycle = billingCycle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<CreatePlanFeatureDTO> getFeatures() {
        return features;
    }

    public void setFeatures(List<CreatePlanFeatureDTO> features) {
        this.features = features;
    }
}
