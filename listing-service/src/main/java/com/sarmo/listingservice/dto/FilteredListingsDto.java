package com.sarmo.listingservice.dto;

import org.springframework.data.domain.Page;
import java.util.List;

public class FilteredListingsDto {
    private List<ListingDto> premiumListings;
    private Page<ListingDto> paginatedListings;

    public FilteredListingsDto(List<ListingDto> premiumListings, Page<ListingDto> paginatedListings) {
        this.premiumListings = premiumListings;
        this.paginatedListings = paginatedListings;
    }

    public List<ListingDto> getPremiumListings() {
        return premiumListings;
    }

    public void setPremiumListings(List<ListingDto> premiumListings) {
        this.premiumListings = premiumListings;
    }

    public Page<ListingDto> getPaginatedListings() { // Возвращаем Page
        return paginatedListings;
    }

    public void setPaginatedListings(Page<ListingDto> paginatedListings) { // Принимаем Page
        this.paginatedListings = paginatedListings;
    }
}