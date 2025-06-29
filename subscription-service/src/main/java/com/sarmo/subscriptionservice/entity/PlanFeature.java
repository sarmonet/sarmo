package com.sarmo.subscriptionservice.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "plan_features")
public class PlanFeature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "plan_id", nullable = false)
    @JsonBackReference
    private SubscriptionPlan subscriptionPlan;

    @ManyToOne
    @JoinColumn(name = "feature_id", nullable = false)
    private SubscriptionFeature subscriptionFeature;

    @Column(nullable = false)
    private String value;

    @Column(nullable = false)
    private String unit;

    public PlanFeature() {
    }

    public PlanFeature(SubscriptionPlan subscriptionPlan, SubscriptionFeature subscriptionFeature, String value, String unit) {
        this.subscriptionPlan = subscriptionPlan;
        this.subscriptionFeature = subscriptionFeature;
        this.value = value;
        this.unit = unit;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SubscriptionPlan getSubscriptionPlan() {
        return subscriptionPlan;
    }

    public void setSubscriptionPlan(SubscriptionPlan subscriptionPlan) {
        this.subscriptionPlan = subscriptionPlan;
    }

    public SubscriptionFeature getSubscriptionFeature() {
        return subscriptionFeature;
    }

    public void setSubscriptionFeature(SubscriptionFeature subscriptionFeature) {
        this.subscriptionFeature = subscriptionFeature;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}