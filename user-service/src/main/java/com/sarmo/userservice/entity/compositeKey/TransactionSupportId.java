package com.sarmo.userservice.entity.compositeKey;

import java.io.Serializable;
import java.util.Objects;


public class TransactionSupportId implements Serializable {

    private Long userId;
    private Long listingId;

    public TransactionSupportId() {}

    public TransactionSupportId(Long userId, Long listingId) {
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
        TransactionSupportId that = (TransactionSupportId) o;
        return Objects.equals(userId, that.userId) && Objects.equals(listingId, that.listingId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, listingId);
    }
}