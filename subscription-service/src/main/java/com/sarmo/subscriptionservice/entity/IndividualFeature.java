package com.sarmo.subscriptionservice.entity;

import com.sarmo.subscriptionservice.enums.IndividualFeatureType; // Импорт enum
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "individual_features")
public class IndividualFeature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // Кодовое название функции (например, "extendedStorage")

    @Column(nullable = false)
    private String displayName; // Отображаемое название функции (например, "Расширенное хранилище")

    @Column(nullable = false)
    private String description; // Описание функции

    @Enumerated(EnumType.STRING) // Указываем, что нужно хранить строковое представление enum
    @Column(nullable = false)
    private IndividualFeatureType type; // Тип ограничения: BOOLEAN, NUMBER, TEXT

    @Column(nullable = false)
    private BigDecimal price;

    public IndividualFeature() {
    }

    public IndividualFeature(String name, String displayName, String description, IndividualFeatureType type, BigDecimal price) {
        this.name = name;
        this.displayName = displayName;
        this.description = description;
        this.type = type;
        this.price = price;
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

    public IndividualFeatureType getType() {
        return type;
    }

    public void setType(IndividualFeatureType type) {
        this.type = type;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}