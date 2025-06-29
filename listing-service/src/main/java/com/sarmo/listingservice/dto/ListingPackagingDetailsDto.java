package com.sarmo.listingservice.dto;

import com.sarmo.listingservice.enums.PackagingSetStatus;

public class ListingPackagingDetailsDto {

    private Long userId;
    private Long listingId;

    private boolean isPageDesignSelected = false;
    private boolean isPresentationSelected = false;
    private boolean isFinancialModelSelected = false;

    private PackagingSetStatus status;

    public ListingPackagingDetailsDto() {
    }

    public ListingPackagingDetailsDto(Long userId, Long listingId, boolean isPageDesignSelected, boolean isPresentationSelected, boolean isFinancialModelSelected) {
        this.userId = userId;
        this.listingId = listingId;
        this.isPageDesignSelected = isPageDesignSelected;
        this.isPresentationSelected = isPresentationSelected;
        this.isFinancialModelSelected = isFinancialModelSelected;
    }

    public ListingPackagingDetailsDto(Long userId, Long listingId, boolean isPageDesignSelected, boolean isPresentationSelected, boolean isFinancialModelSelected, PackagingSetStatus status) {
        this.userId = userId;
        this.listingId = listingId;
        this.isPageDesignSelected = isPageDesignSelected;
        this.isPresentationSelected = isPresentationSelected;
        this.isFinancialModelSelected = isFinancialModelSelected;
        this.status = status;
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

    public boolean isFinancialModelSelected() {
        return isFinancialModelSelected;
    }

    public void setFinancialModelSelected(boolean financialModelSelected) {
        this.isFinancialModelSelected = financialModelSelected;
    }

    public PackagingSetStatus getStatus() {
        return status;
    }

    public void setStatus(PackagingSetStatus status) {
        this.status = status;
    }
}