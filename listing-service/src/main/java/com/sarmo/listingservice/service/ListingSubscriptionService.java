package com.sarmo.listingservice.service;

import com.sarmo.listingservice.entity.Listing;
import com.sarmo.listingservice.entity.PremiumSubscription;
import com.sarmo.listingservice.repository.ListingRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ListingSubscriptionService {

    private final ListingRepository listingRepository;

    public ListingSubscriptionService(ListingRepository listingRepository) {
        this.listingRepository = listingRepository;
    }

    public void addPremiumSubscription(Long listingId, LocalDateTime startDate, LocalDateTime endDate) {
        Listing listing = listingRepository.findById(listingId).orElse(null);
        if (listing != null) {
            PremiumSubscription subscription = new PremiumSubscription();
            subscription.setListing(listing);
            subscription.setStartDate(startDate);
            subscription.setEndDate(endDate);
            listing.setPremiumSubscription(subscription);
            listingRepository.save(listing);
        }
    }

    public void removePremiumSubscription(Long listingId) {
        Listing listing = listingRepository.findById(listingId).orElse(null);
        if (listing != null && listing.getPremiumSubscription() != null) {
            listing.setPremiumSubscription(null);
            listingRepository.save(listing);
        }
    }

    public boolean isPremiumActive(Long listingId) {
        Listing listing = listingRepository.findById(listingId).orElse(null);
        if (listing != null && listing.getPremiumSubscription() != null) {
            return listing.getPremiumSubscription().isActive();
        }
        return false;
    }
}
