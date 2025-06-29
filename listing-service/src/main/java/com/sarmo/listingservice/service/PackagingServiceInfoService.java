package com.sarmo.listingservice.service;

import com.sarmo.listingservice.entity.PackagingServiceInfo;
import com.sarmo.listingservice.repository.PackagingServiceInfoRepository;
import com.sarmo.listingservice.dto.PackagingServiceInfoRequestDto;
import com.sarmo.listingservice.dto.PackagingServiceInfoResponseDto;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class PackagingServiceInfoService {

    private static final Logger logger = LoggerFactory.getLogger(PackagingServiceInfoService.class);
    private final PackagingServiceInfoRepository packagingServiceInfoRepository;

    public PackagingServiceInfoService(PackagingServiceInfoRepository packagingServiceInfoRepository) {
        this.packagingServiceInfoRepository = packagingServiceInfoRepository;
    }

    @Transactional(readOnly = true)
    public PackagingServiceInfoResponseDto getPackagingServiceInfo() {
        logger.debug("Fetching packaging service info configuration.");
        List<PackagingServiceInfo> configs = packagingServiceInfoRepository.findAll();

        if (configs.isEmpty()) {
            logger.warn("No packaging service info configuration found.");
            return null;
        }

        PackagingServiceInfo config = configs.getLast();
        logger.debug("Packaging service info configuration found with ID: {}", config.getId());
        return mapEntityToResponseDto(config);
    }

    @Transactional(readOnly = true)
    public PackagingServiceInfo getPackagingServiceInfoEntity() {
        logger.debug("Fetching packaging service info entity.");
        List<PackagingServiceInfo> configs = packagingServiceInfoRepository.findAll();
        if (configs.isEmpty()) {
            logger.warn("No packaging service info entity found.");
            return null;
        }
        PackagingServiceInfo config = configs.getLast();
        logger.debug("Packaging service info entity found with ID: {}", config.getId());
        return config;
    }


    @Transactional
    public PackagingServiceInfoResponseDto createPackagingServiceInfo(PackagingServiceInfoRequestDto requestDto) {
        logger.info("Attempting to create new packaging service info configuration.");
        if (!packagingServiceInfoRepository.findAll().isEmpty()) {
            logger.warn("Packaging service info configuration already exists. Creation rejected.");
            throw new IllegalStateException("Packaging service info configuration already exists.");
        }

        PackagingServiceInfo config = new PackagingServiceInfo();
        mapRequestDtoToEntity(requestDto, config);

        PackagingServiceInfo savedConfig = packagingServiceInfoRepository.save(config);
        logger.info("New packaging service info configuration created with ID: {}", savedConfig.getId());
        return mapEntityToResponseDto(savedConfig);
    }

    @Transactional
    public PackagingServiceInfoResponseDto updatePackagingServiceInfo(Long configId, PackagingServiceInfoRequestDto requestDto) {
        logger.info("Attempting to update packaging service info configuration with ID: {}", configId);
        Optional<PackagingServiceInfo> configOptional = packagingServiceInfoRepository.findById(configId);

        if (configOptional.isEmpty()) {
            logger.warn("Packaging service info configuration with ID {} not found. Update failed.", configId);
            throw new EntityNotFoundException("Packaging service info configuration with ID " + configId + " not found.");
        }

        PackagingServiceInfo config = configOptional.get();
        mapRequestDtoToEntity(requestDto, config);

        PackagingServiceInfo updatedConfig = packagingServiceInfoRepository.save(config);
        logger.info("Packaging service info configuration updated with ID: {}", updatedConfig.getId());
        return mapEntityToResponseDto(updatedConfig);
    }

    @Transactional
    public void deletePackagingServiceInfo(Long configId) {
        logger.info("Attempting to delete packaging service info configuration with ID: {}", configId);
        if (!packagingServiceInfoRepository.existsById(configId)) {
            logger.warn("Packaging service info configuration with ID {} not found. Deletion failed.", configId);
            throw new EntityNotFoundException("Packaging service info configuration with ID " + configId + " not found.");
        }
        packagingServiceInfoRepository.deleteById(configId);
        logger.info("Packaging service info configuration deleted with ID: {}", configId);
    }

    private PackagingServiceInfoResponseDto mapEntityToResponseDto(PackagingServiceInfo entity) {
        if (entity == null) return null;
        PackagingServiceInfoResponseDto dto = new PackagingServiceInfoResponseDto();
        dto.setId(entity.getId());
        dto.setPageDesignName(entity.getPageDesignName());
        dto.setPageDesignPrice(entity.getPageDesignPrice());
        dto.setPageDesignDescription(entity.getPageDesignDescription());
        dto.setPresentationName(entity.getPresentationName());
        dto.setPresentationPrice(entity.getPresentationPrice());
        dto.setPresentationDescription(entity.getPresentationDescription());
        dto.setFinancialModelName(entity.getFinancialModelName());
        dto.setFinancialModelPrice(entity.getFinancialModelPrice());
        dto.setFinancialModelDescription(entity.getFinancialModelDescription());
        // TODO: Add mapping for other fixed services (Name, Price, Description)

        dto.setDiscountPercentage(entity.getDiscountPercentage());

        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        BigDecimal sumOfAllServices = BigDecimal.ZERO;
        if (entity.getPageDesignPrice() != null) sumOfAllServices = sumOfAllServices.add(entity.getPageDesignPrice());
        if (entity.getPresentationPrice() != null) sumOfAllServices = sumOfAllServices.add(entity.getPresentationPrice());
        if (entity.getFinancialModelPrice() != null) sumOfAllServices = sumOfAllServices.add(entity.getFinancialModelPrice());
        // TODO: Add summation for other fixed service prices here

        BigDecimal discountPercentage = entity.getDiscountPercentage();
        BigDecimal totalAfterDiscount = sumOfAllServices;

        if (discountPercentage != null && discountPercentage.compareTo(BigDecimal.ZERO) > 0) {
            if (discountPercentage.compareTo(BigDecimal.valueOf(100)) > 0) {
                discountPercentage = BigDecimal.valueOf(100);
            }

            BigDecimal hundred = new BigDecimal("100");
            BigDecimal discountFactor = discountPercentage.divide(hundred, 4, BigDecimal.ROUND_HALF_UP);
            BigDecimal discountAmount = sumOfAllServices.multiply(discountFactor);

            totalAfterDiscount = sumOfAllServices.subtract(discountAmount);

            if (totalAfterDiscount.compareTo(BigDecimal.ZERO) < 0) {
                totalAfterDiscount = BigDecimal.ZERO;
            }
        }

        dto.setTotalPackagePrice(totalAfterDiscount);

        return dto;
    }

    private PackagingServiceInfo mapRequestDtoToEntity(PackagingServiceInfoRequestDto dto, PackagingServiceInfo entity) {
        if (dto == null || entity == null) return null;

        entity.setPageDesignPrice(dto.getPageDesignPrice());
        entity.setPageDesignDescription(dto.getPageDesignDescription());
        // entity.setPresentationName(dto.getPresentationName());
        entity.setPresentationPrice(dto.getPresentationPrice());
        entity.setPresentationDescription(dto.getPresentationDescription());
        // entity.setFinancialModelName(dto.getFinancialModelName());
        entity.setFinancialModelPrice(dto.getFinancialModelPrice());
        entity.setFinancialModelDescription(dto.getFinancialModelDescription());
        // TODO: Add mapping for other fixed services (Price, Description)
        entity.setDiscountPercentage(dto.getDiscountPercentage());
        return entity;
    }
}