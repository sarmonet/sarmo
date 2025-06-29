package com.sarmo.listingservice.entity;

import com.sarmo.listingservice.enums.PackagingSetStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;


@Entity
@Table(name = "listing_packaging_details")
public class ListingPackagingDetails {

    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "listing_id")
    private Listing listing;

    @Column(nullable = false)
    private boolean isPageDesignSelected = false;

    @Column(nullable = false)
    private boolean isPresentationSelected = false;

    @Column(nullable = false)
    private boolean isFinancialModelSelected = false;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PackagingSetStatus status = PackagingSetStatus.ACTIVE;


    public ListingPackagingDetails() {
    }

    public ListingPackagingDetails(Listing listing) {
        this.listing = listing;
    }

    public ListingPackagingDetails(Listing listing, boolean isPageDesignSelected, boolean isPresentationSelected, boolean isFinancialModelSelected, PackagingSetStatus status /*, ... other boolean fields */) {
        this.listing = listing;
        this.isPageDesignSelected = isPageDesignSelected;
        this.isPresentationSelected = isPresentationSelected;
        this.isFinancialModelSelected = isFinancialModelSelected;
        this.status = status;
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

    public boolean isPageDesignSelected() {
        return isPageDesignSelected;
    }

    public void setPageDesignSelected(boolean pageDesignSelected) {
        this.isPageDesignSelected = pageDesignSelected;
    }

    public boolean isPresentationSelected() {
        return isPresentationSelected;
    }

    public void setPresentationSelected(boolean presentationSelected) {
        this.isPresentationSelected = presentationSelected;
    }

    public boolean isFinancialModelSelected() {
        return isFinancialModelSelected;
    }

    public void setFinancialModelSelected(boolean financialModelSelected) {
        this.isFinancialModelSelected = financialModelSelected;
    }

    // Add getters and setters for other boolean fields

    public PackagingSetStatus getStatus() {
        return status;
    }

    public void setStatus(PackagingSetStatus status) {
        this.status = status;
    }

    // toString, hashCode, equals methods recommended
}