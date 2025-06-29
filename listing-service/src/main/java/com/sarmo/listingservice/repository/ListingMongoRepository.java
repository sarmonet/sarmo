package com.sarmo.listingservice.repository;

import com.sarmo.listingservice.entity.ListingMongo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ListingMongoRepository extends MongoRepository<ListingMongo, String> {
    List<ListingMongo> findByCategoryId(Long categoryId);
    ListingMongo findByListingId(Long listingId);
}