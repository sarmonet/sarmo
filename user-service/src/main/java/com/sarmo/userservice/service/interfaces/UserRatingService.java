package com.sarmo.userservice.service.interfaces;

import com.sarmo.userservice.entity.UserRating;

import java.util.List;

public interface UserRatingService {

    UserRating createUserRating(UserRating userRating);

    UserRating getUserRatingById(Long id);

    List<UserRating> getAllUserRatings();

    UserRating updateUserRating(UserRating userRating);

    void deleteUserRating(Long id);

    List<UserRating> getUserRatingsByRatedUserId(Long ratedUserId);

    List<UserRating> getUserRatingsByUserId(Long userId);
}