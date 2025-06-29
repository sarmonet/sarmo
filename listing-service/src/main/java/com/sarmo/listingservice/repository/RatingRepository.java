package com.sarmo.listingservice.repository;

import com.sarmo.listingservice.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

}
