package com.sarmo.listingservice.service;

import com.sarmo.listingservice.entity.Listing;
import com.sarmo.listingservice.entity.ListingPackagingDetails;
import com.sarmo.listingservice.enums.PackagingSetStatus;
import com.sarmo.listingservice.repository.ListingPackagingDetailsRepository;
import com.sarmo.listingservice.dto.ListingPackagingDetailsDto;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ListingPackagingDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(ListingPackagingDetailsService.class);
    private final ListingPackagingDetailsRepository packagingDetailsRepository;


    public ListingPackagingDetailsService(ListingPackagingDetailsRepository packagingDetailsRepository) {
        this.packagingDetailsRepository = packagingDetailsRepository;

    }

    @Transactional
    public void createPackagingDetails(Listing listing, ListingPackagingDetailsDto packagingDetailsDto) {
        logger.info("Creating packaging details for new listing with ID: {}", listing.getId());
        if (listing.getId() == null) {
            logger.error("Cannot create packaging details for a null or unsaved listing.");
            throw new IllegalArgumentException("Listing must be saved and have an ID before creating packaging details.");
        }
        if (packagingDetailsRepository.existsById(listing.getId())) {
            logger.warn("Packaging details already exist for listing ID: {}. Skipping creation.", listing.getId());
            throw new IllegalStateException("Packaging details already exist for listing ID: " + listing.getId());
        }

        ListingPackagingDetails details = new ListingPackagingDetails(listing);

        details = mapDtoToEntity(packagingDetailsDto, details);

        packagingDetailsRepository.save(details);
        logger.info("Packaging details created for listing ID: {}", listing.getId());
    }

    @Transactional
    public ListingPackagingDetails updatePackagingDetails(Long listingId, ListingPackagingDetailsDto packagingDetailsDto) {
        logger.info("Attempting to update packaging details for listing with ID: {}", listingId);
        Optional<ListingPackagingDetails> detailsOptional = packagingDetailsRepository.findById(listingId);

        if (detailsOptional.isEmpty()) {
            logger.warn("Packaging details not found for listing ID: {}. Cannot update.", listingId);
            throw new EntityNotFoundException("Packaging details not found for listing ID: " + listingId);
        }

        ListingPackagingDetails details = detailsOptional.get();

        ListingPackagingDetails updatedDetails = packagingDetailsRepository.save(details);
        logger.info("Packaging details updated for listing ID: {}", listingId);
        return updatedDetails;
    }


    @Transactional(readOnly = true)
    public ListingPackagingDetailsDto getPackagingDetails(Long listingId) {
        logger.debug("Fetching packaging details for listing with ID: {}", listingId);
        Optional<ListingPackagingDetails> detailsOptional = packagingDetailsRepository.findById(listingId);

        if (detailsOptional.isEmpty()) {
            logger.debug("Packaging details not found for listing ID: {}.", listingId);
            return null;
        }

        ListingPackagingDetails details = detailsOptional.get();
        ListingPackagingDetailsDto dto = mapEntityToDto(details);

        logger.debug("Packaging details fetched for listing ID: {}", listingId);
        return dto;
    }


    @Transactional(readOnly = true)
    public List<ListingPackagingDetailsDto> getPackagingDetailsForListings(List<Long> listingIds) {
        if (listingIds == null || listingIds.isEmpty()) {
            logger.debug("Input list of listing IDs is null or empty. Returning empty list.");
            return new ArrayList<>();
        }
        logger.debug("Fetching packaging details for a list of {} listing IDs.", listingIds.size());

        List<ListingPackagingDetails> detailsList = packagingDetailsRepository.findAllById(listingIds);

        if (detailsList.isEmpty()) {
            logger.debug("No packaging details found for the provided listing IDs.");
            return new ArrayList<>();
        }

        List<ListingPackagingDetailsDto> dtoList = detailsList.stream()
                .map(this::mapEntityToDto)
                .collect(Collectors.toList());

        logger.debug("Fetched {} packaging details entries.", dtoList.size());

        return dtoList;
    }


    @Transactional(readOnly = true)
    public List<ListingPackagingDetailsDto> getAllActivePackagingDetails() {
        logger.debug("Fetching all active packaging details.");
        List<ListingPackagingDetails> activeDetails = packagingDetailsRepository.findByStatus(PackagingSetStatus.ACTIVE);
        logger.debug("Found {} active packaging details entries.", activeDetails.size());

        // Map entities to DTOs
        return activeDetails.stream()
                .map(this::mapEntityToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ListingPackagingDetailsDto> getAllInactivePackagingDetails() {
        logger.debug("Fetching all inactive packaging details.");
        List<ListingPackagingDetails> inactiveDetails = packagingDetailsRepository.findByStatus(PackagingSetStatus.INACTIVE);
        logger.debug("Found {} inactive packaging details entries.", inactiveDetails.size());

        // Map entities to DTOs
        return inactiveDetails.stream()
                .map(this::mapEntityToDto)
                .collect(Collectors.toList());
    }


    @Transactional
    public void deletePackagingDetails(Long listingId) {
        logger.info("Attempting to delete packaging details for listing with ID: {}", listingId);
        if (!packagingDetailsRepository.existsById(listingId)) {
            logger.warn("Packaging details for listing ID {} not found. Deletion failed.", listingId);
            throw new EntityNotFoundException("Packaging details not found for listing ID: " + listingId);
        }
        packagingDetailsRepository.deleteById(listingId);
        logger.info("Packaging details deleted for listing with ID: {}", listingId);
    }


    @Transactional(readOnly = true)
    public boolean existsByListingId(Long listingId) {
        logger.debug("Checking if packaging details exist for listing ID: {}", listingId);
        boolean exists = packagingDetailsRepository.existsById(listingId);
        logger.debug("Packaging details exist for listing ID {}: {}", listingId, exists);
        return exists;
    }


    private ListingPackagingDetails mapDtoToEntity(ListingPackagingDetailsDto dto, ListingPackagingDetails entity) {
        if (dto == null || entity == null) return null;
        entity.setPageDesignSelected(dto.isPageDesignSelected());
        entity.setPresentationSelected(dto.isPresentationSelected());
        entity.setFinancialModelSelected(dto.isFinancialModelSelected());

        if (dto.getStatus() != null) {
            entity.setStatus(dto.getStatus());
        } else {
            entity.setStatus(PackagingSetStatus.ACTIVE);
        }

        return entity;
    }

    private ListingPackagingDetailsDto mapEntityToDto(ListingPackagingDetails entity) {
        if (entity == null) return null;
        ListingPackagingDetailsDto dto = new ListingPackagingDetailsDto();
        dto.setUserId(entity.getListing().getUser().getId());
        dto.setListingId(entity.getListing().getId());
        dto.setPageDesignSelected(entity.isPageDesignSelected());
        dto.setPresentationSelected(entity.isPresentationSelected());
        dto.setFinancialModelSelected(entity.isFinancialModelSelected());

        dto.setStatus(entity.getStatus());

        return dto;
    }
}