package com.sarmo.userservice.entity;

import com.sarmo.userservice.entity.compositeKey.UserFavoriteListingId;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "user_favorite_listings")
@IdClass(UserFavoriteListingId.class)
public class UserFavoriteListing {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Id
    @Column(name = "listing_id")
    private Long listingId;

    public UserFavoriteListing() {}

    public UserFavoriteListing(Long userId, Long listingId) {
        this.userId = userId;
        this.listingId = listingId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getListingId() {
        return listingId;
    }

    public void setListingId(Long listingId) {
        this.listingId = listingId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserFavoriteListing that = (UserFavoriteListing) o;
        return Objects.equals(userId, that.userId) && Objects.equals(listingId, that.listingId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, listingId);
    }
}