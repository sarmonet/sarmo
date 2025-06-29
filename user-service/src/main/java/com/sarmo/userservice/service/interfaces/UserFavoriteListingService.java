package com.sarmo.userservice.service.interfaces;

import com.sarmo.userservice.entity.UserFavoriteListing;
import java.util.List;

public interface UserFavoriteListingService {

    UserFavoriteListing addUserFavoriteListing(UserFavoriteListing userFavoriteListing);

    UserFavoriteListing getUserFavoriteListing(Long userId, Long listingId);

    void removeUserFavoriteListing(Long userId, Long listingId);

    List<UserFavoriteListing> getUserFavoriteListingsByUserId(Long userId);

    UserFavoriteListing addFavoriteListing(Long userId, Long listingId);

    void removeFavoriteListing(Long userId, Long listingId);

    List<UserFavoriteListing> getFavoriteListings(Long userId);

    List<Object> getFavoriteListingsWithDetails(Long userId);
}