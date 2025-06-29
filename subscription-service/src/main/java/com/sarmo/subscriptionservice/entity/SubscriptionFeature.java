package com.sarmo.subscriptionservice.entity;

import com.sarmo.subscriptionservice.enums.SubscriptionFeatureType;
import jakarta.persistence.*;

@Entity
@Table(name = "subscription_features")
public class SubscriptionFeature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // Кодовое название функции (например, "maxListingsPerCategory")

    @Column(nullable = false)
    private String displayName; // Отображаемое название функции (например, "Максимальное количество объявлений в категории")

    @Column(nullable = false)
    private String description; // Подробное описание функции

    @Enumerated(EnumType.STRING) // Указываем, что нужно хранить строковое представление enum
    @Column(nullable = false)
    private SubscriptionFeatureType type; // Тип ограничения: BOOLEAN, NUMBER, TEXT

    public SubscriptionFeature() {
    }

    public SubscriptionFeature(String name, String displayName, String description, SubscriptionFeatureType type) {
        this.name = name;
        this.displayName = displayName;
        this.description = description;
        this.type = type;
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

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SubscriptionFeatureType getType() {
        return type;
    }

    public void setType(SubscriptionFeatureType type) {
        this.type = type;
    }
}