package com.sarmo.listingservice.service;

import com.sarmo.listingservice.dto.ListingDto;
import com.sarmo.listingservice.entity.Listing;
import com.sarmo.listingservice.repository.ListingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ListingRandomService {
    private final Logger logger = LoggerFactory.getLogger(ListingRandomService.class);
    private final ListingRepository listingRepository;

    public ListingRandomService(ListingRepository listingRepository) {
        this.listingRepository = listingRepository;
    }

    public List<ListingDto> getRandomListingsLimited(int count) {
        List<Listing> listings = listingRepository.findRandomActiveListingsLimited(count);
        return listings.stream().map(this::convertListingToDto).collect(Collectors.toList());
    }

    public List<ListingDto> getRandomListingsByCategoryAndSubcategory(Long categoryId, Long subcategoryId, int count) {
        List<Listing> listings = listingRepository.findRandomListingsByCategoryAndSubcategoryOrCategory(categoryId, subcategoryId, count);

        if (listings.size() < count) {
            List<ListingDto> categoryListings = getRandomListingsByCategory(categoryId, count - listings.size());
            List<ListingDto> result = listings.stream().map(this::convertListingToDto).collect(Collectors.toList());
            result.addAll(categoryListings);
            return result;
        }

        return listings.stream().map(this::convertListingToDto).collect(Collectors.toList());
    }

    public List<ListingDto> getRandomListingsByCategory(Long categoryId, int count) {
        List<Listing> listings = listingRepository.findRandomListingsByCategory(categoryId, count);
        return listings.stream().map(this::convertListingToDto).collect(Collectors.toList());
    }

    private ListingDto convertListingToDto(Listing listing) {
        if (listing == null) {
            logger.warn("Listing is null in convertListingToDto");
            return null;
        }
        ListingDto dto = new ListingDto();
        dto.setId(listing.getId());
        dto.setTitle(listing.getTitle());
        dto.setCategory(listing.getCategory());
        dto.setSubCategory(listing.getSubCategory());
        dto.setPrice(listing.getPrice());
        dto.setCountry(listing.getCountry());
        dto.setCity(listing.getCity());
        dto.setCreatedAt(listing.getCreatedAt());
        dto.setStatus(listing.getStatus());
        dto.setPremiumSubscription(listing.getPremiumSubscription() != null && listing.getPremiumSubscription().isActive());
        dto.setMainImage(listing.getMainImage());
        dto.setAverageRating(listing.getAverageRating());
        dto.setViewCount(listing.getViewCount());
        dto.setInvest(listing.getInvest());
        return dto;
    }
}