package com.sarmo.listingservice.repository;

import com.sarmo.listingservice.entity.InvestmentCategoryField;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvestmentCategoryFieldRepository extends MongoRepository<InvestmentCategoryField, Long> {
    Optional<InvestmentCategoryField> findByCategoryId(Long categoryId);

}