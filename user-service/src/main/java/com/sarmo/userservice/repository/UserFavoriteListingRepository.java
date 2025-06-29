package com.sarmo.userservice.repository;

import com.sarmo.userservice.entity.UserFavoriteListing;
import com.sarmo.userservice.entity.compositeKey.UserFavoriteListingId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserFavoriteListingRepository extends JpaRepository<UserFavoriteListing, UserFavoriteListingId> {
    List<UserFavoriteListing> findByUserId(Long userId);
}