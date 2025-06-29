package com.sarmo.listingservice.repository;

import com.sarmo.listingservice.entity.Listing;
import com.sarmo.listingservice.enums.ListingStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.lang.NonNull;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ListingRepository extends JpaRepository<Listing, Long> {

    List<Listing> findByIdIn(List<Long> ids);

    Page<Listing> findByIdIn(List<Long> ids, Pageable pageable);

    @Query("SELECT l FROM Listing l JOIN l.premiumSubscription ps " +
            "WHERE l.id IN :ids " +
            "AND ps.startDate <= CURRENT_TIMESTAMP " +
            "AND ps.endDate >= CURRENT_TIMESTAMP " +
            "AND l.status = 'ACTIVE'") // Предполагаем, что премиум-листинги также должны быть активными
    List<Listing> findPremiumListingsByIdInAndActiveSubscription(@Param("ids") List<Long> ids);

    @Override
    @NonNull
    Page<Listing> findAll(@NonNull Pageable pageable);

    long countByCategoryId(Long categoryId);

    long countBySubCategoryId(Long subCategoryId);

    @Modifying
    @Transactional
    @Query("UPDATE Listing l SET l.viewCount = :views WHERE l.id = :listingId")
    void updateViewCount(@Param("listingId") Long listingId, @Param("views") Long views);

    @Query(value = "SELECT * FROM listings WHERE status = 'ACTIVE' AND category_id = :categoryId AND (:subcategoryId IS NULL OR sub_category_id = :subcategoryId) ORDER BY random() LIMIT :limit", nativeQuery = true)
    List<Listing> findRandomListingsByCategoryAndSubcategoryOrCategory(
            @Param("categoryId") Long categoryId,
            @Param("subcategoryId") Long subcategoryId,
            @Param("limit") int limit);

    @Query(value = "SELECT * FROM listings WHERE status = 'ACTIVE' AND category_id = :categoryId ORDER BY random() LIMIT :limit", nativeQuery = true)
    List<Listing> findRandomListingsByCategory(
            @Param("categoryId") Long categoryId,
            @Param("limit") int limit);

    // НОВЫЙ МЕТОД: Возвращает определенное количество любых активных объявлений
    @Query(value = "SELECT * FROM listings WHERE status = 'ACTIVE' ORDER BY random() LIMIT :limit", nativeQuery = true)
    List<Listing> findRandomActiveListingsLimited(@Param("limit") int limit);

    List<Listing> findByUserId(Long userId);

    Page<Listing> findByStatus(ListingStatus status, Pageable pageable);

    List<Listing> findByUser_IdAndStatus(Long userId, ListingStatus status);

    default Page<Listing> findByIsActiveFalse(Pageable pageable) {
        return findByStatus(ListingStatus.INACTIVE, pageable);
    }

    default Page<Listing> findByIsActiveTrue(Pageable pageable) {
        return findByStatus(ListingStatus.ACTIVE, pageable);
    }

    default Page<Listing> findByIsRejectedTrue(Pageable pageable) {
        return findByStatus(ListingStatus.REJECTED, pageable);
    }

    default List<Listing> findByUserIdAndIsActiveFalse(Long userId) {
        return findByUser_IdAndStatus(userId, ListingStatus.INACTIVE);
    }

    @Query("SELECT l.viewCount FROM Listing l WHERE l.id = :id")
    Long findViewCountById(@Param("id") Long id);

    @Query("SELECT l.id FROM Listing l WHERE l.status = 'ACTIVE'")
    List<Long> findActiveListingIds();

    @Query("SELECT l.id FROM Listing l WHERE l.status = 'ACTIVE' AND l.category.id = :categoryId")
    List<Long> findActiveListingIdsByCategoryId(@Param("categoryId") Long categoryId);


}
