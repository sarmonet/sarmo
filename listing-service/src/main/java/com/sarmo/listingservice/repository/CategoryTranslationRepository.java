package com.sarmo.listingservice.repository;

import com.sarmo.listingservice.entity.Category;
import com.sarmo.listingservice.entity.CategoryTranslation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryTranslationRepository extends JpaRepository<CategoryTranslation, Long> {
    List<CategoryTranslation> findByCategory(Category category);
    void deleteByCategory(Category category);
    Optional<CategoryTranslation> findByCategoryIdAndLanguageCode(Long categoryId, String languageCode);
    Optional<CategoryTranslation> findByCategoryAndLanguageCode(Category category, String languageCode);
}