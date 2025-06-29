package com.sarmo.listingservice.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "category_translations")
public class CategoryTranslation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false)
    private String languageCode;

    @Column(nullable = false)
    private String translatedName;

    public CategoryTranslation() {
    }

    public CategoryTranslation(Category category, String languageCode, String translatedName) {
        this.category = category;
        this.languageCode = languageCode;
        this.translatedName = translatedName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getTranslatedName() {
        return translatedName;
    }

    public void setTranslatedName(String translatedName) {
        this.translatedName = translatedName;
    }
}
