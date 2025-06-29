package com.sarmo.subscriptionservice.entity;

import com.sarmo.subscriptionservice.enums.SubscriptionStatus;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "user_subscriptions")
public class UserSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId; // ID пользователя из локальной таблицы

    @ManyToOne
    @JoinColumn(name = "subscription_plan_id", nullable = false)
    private SubscriptionPlan subscriptionPlan;

    @Column(nullable = false)
    private LocalDate startDate;

    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionStatus status; // Например, ACTIVE, INACTIVE, CANCELED

    public UserSubscription() {
    }

    public UserSubscription(Long userId, SubscriptionPlan subscriptionPlan, LocalDate startDate, LocalDate endDate, SubscriptionStatus status) {
        this.userId = userId;
        this.subscriptionPlan = subscriptionPlan;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public SubscriptionPlan getSubscriptionPlan() {
        return subscriptionPlan;
    }

    public void setSubscriptionPlan(SubscriptionPlan subscriptionPlan) {
        this.subscriptionPlan = subscriptionPlan;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public SubscriptionStatus getStatus() {
        return status;
    }

    public void setStatus(SubscriptionStatus status) {
        this.status = status;
    }
}