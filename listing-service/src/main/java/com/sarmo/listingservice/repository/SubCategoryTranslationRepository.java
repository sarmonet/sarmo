package com.sarmo.listingservice.repository;

import com.sarmo.listingservice.entity.SubCategory;
import com.sarmo.listingservice.entity.SubCategoryTranslation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubCategoryTranslationRepository extends JpaRepository<SubCategoryTranslation, Long> {
    List<SubCategoryTranslation> findBySubCategory(SubCategory subCategory);
    void deleteBySubCategory(SubCategory subCategory);
    Optional<SubCategoryTranslation> findBySubCategoryAndLanguageCode(SubCategory subCategory, String languageCode);
    Optional<SubCategoryTranslation> findBySubCategoryIdAndLanguageCode(Long subCategoryId, String languageCode);
}