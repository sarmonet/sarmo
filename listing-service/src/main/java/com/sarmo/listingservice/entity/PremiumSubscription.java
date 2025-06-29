package com.sarmo.listingservice.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "premium_subscriptions")
public class PremiumSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "listing_id", referencedColumnName = "id")
    @JsonBackReference
    private Listing listing;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    public PremiumSubscription() {
    }

    public PremiumSubscription(Long id, Listing listing, LocalDateTime startDate, LocalDateTime endDate) {
        this.id = id;
        this.listing = listing;
        this.startDate = startDate;
        this.endDate = endDate;
    }
    public boolean isActive() {
        LocalDateTime now = LocalDateTime.now();
        return startDate != null && endDate != null && now.isAfter(startDate) && now.isBefore(endDate);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Listing getListing() {
        return listing;
    }

    public void setListing(Listing listing) {
        this.listing = listing;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }
}