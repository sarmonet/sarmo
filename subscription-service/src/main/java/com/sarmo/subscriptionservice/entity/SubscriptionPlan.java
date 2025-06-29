package com.sarmo.subscriptionservice.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sarmo.subscriptionservice.enums.BillingCycle; // Импорт enum
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "subscription_plans")
public class SubscriptionPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private BigDecimal price;

    @Enumerated(EnumType.STRING) // Указываем, что нужно хранить строковое представление enum
    @Column(nullable = false)
    private BillingCycle billingCycle; // Например, MONTHLY, QUARTERLY, YEARLY

    @Column(columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "subscriptionPlan", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<PlanFeature> planFeatures;

    public SubscriptionPlan() {
    }

    public SubscriptionPlan(String name, BigDecimal price, BillingCycle billingCycle, String description, List<PlanFeature> planFeatures) {
        this.name = name;
        this.price = price;
        this.billingCycle = billingCycle;
        this.description = description;
        this.planFeatures = planFeatures;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public List<PlanFeature> getPlanFeatures() {
        return planFeatures;
    }

    public void setPlanFeatures(List<PlanFeature> planFeatures) {
        this.planFeatures = planFeatures;
    }
}