package com.sarmo.listingservice.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "sub_category_translations")
public class SubCategoryTranslation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sub_category_id", nullable = false)
    private SubCategory subCategory;

    @Column(nullable = false)
    private String languageCode; // "en", "fr", "de", "es"

    @Column(nullable = false)
    private String translatedName;

    public SubCategoryTranslation() {
    }

    public SubCategoryTranslation(SubCategory subCategory, String languageCode, String translatedName) {
        this.subCategory = subCategory;
        this.languageCode = languageCode;
        this.translatedName = translatedName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SubCategory getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(SubCategory subCategory) {
        this.subCategory = subCategory;
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