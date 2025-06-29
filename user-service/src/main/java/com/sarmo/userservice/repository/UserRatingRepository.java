package com.sarmo.userservice.repository;

import com.sarmo.userservice.entity.UserRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRatingRepository extends JpaRepository<UserRating, Long> {

    List<UserRating> findByRatedUser_Id(Long ratedUserId);

    List<UserRating> findByUserId(Long userId);
}