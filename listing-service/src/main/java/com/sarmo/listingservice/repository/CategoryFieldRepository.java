package com.sarmo.listingservice.repository;

import com.sarmo.listingservice.entity.CategoryField;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryFieldRepository extends MongoRepository<CategoryField, Long> {
    CategoryField findByCategoryId(Long categoryId);
}
