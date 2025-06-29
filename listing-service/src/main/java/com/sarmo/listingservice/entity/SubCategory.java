package com.sarmo.listingservice.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "sub_categories")
public class SubCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false, unique = true)
    private Long id; // Уникальный идентификатор подкатегории

    @Column(nullable = false)
    private String name; // Название подкатегории

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category; // Родительская категория

    public SubCategory(){

    }
    public SubCategory(Long id, String name, Category category) {
        this.id = id;
        this.name = name;
        this.category = category;
    }

    public SubCategory(String name, Category category) {
        this.name = name;
        this.category = category;
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

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}

