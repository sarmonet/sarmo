package com.sarmo.listingservice.repository;

import com.sarmo.listingservice.entity.ListingPackagingDetails;
import com.sarmo.listingservice.enums.PackagingSetStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ListingPackagingDetailsRepository extends JpaRepository<ListingPackagingDetails, Long> {

    List<ListingPackagingDetails> findByStatus(PackagingSetStatus status);

    @Transactional
    int deleteByListing_Id(Long listingId);

}