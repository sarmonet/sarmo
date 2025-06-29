package com.sarmo.listingservice.repository;

import com.sarmo.listingservice.entity.PackagingServiceInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PackagingServiceInfoRepository extends JpaRepository<PackagingServiceInfo, Long> {

}