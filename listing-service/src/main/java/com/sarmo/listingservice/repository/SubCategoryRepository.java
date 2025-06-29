package com.sarmo.listingservice.repository;

import com.sarmo.listingservice.entity.Category;
import com.sarmo.listingservice.entity.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubCategoryRepository extends JpaRepository<SubCategory, Long> {
    List<SubCategory> findByCategory(Category category);
}