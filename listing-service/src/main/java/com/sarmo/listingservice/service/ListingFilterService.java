package com.sarmo.listingservice.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sarmo.listingservice.dto.FilteredListingsDto;
import com.sarmo.listingservice.dto.ListingDto;
import com.sarmo.listingservice.dto.ListingFilterDto;
import com.sarmo.listingservice.dto.SqlListingFilterDto;
import com.sarmo.listingservice.entity.*;
import com.sarmo.listingservice.enums.ListingStatus;
import com.sarmo.listingservice.repository.ListingRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Expression;
import jakarta.validation.Valid;
import org.hibernate.exception.JDBCConnectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Validated
public class ListingFilterService {

    private final ListingRepository listingRepository;
    private final ListingService listingService;
    private final MongoTemplate mongoTemplate;
    private final EntityManager entityManager;
    private final ObjectMapper objectMapper;
    private static final Logger logger = LoggerFactory.getLogger(ListingFilterService.class);

    public ListingFilterService(ListingRepository listingRepository, ListingService listingService, MongoTemplate mongoTemplate, EntityManager entityManager, ObjectMapper objectMapper) {
        this.listingRepository = listingRepository;
        this.listingService = listingService;
        this.mongoTemplate = mongoTemplate;
        this.entityManager = entityManager;
        this.objectMapper = objectMapper;
    }

    public FilteredListingsDto filterListings(@Valid ListingFilterDto filterDto) {
        logger.info("Starting filterListings with filterDto: {}", filterDto);

        if (filterDto == null || filterDto.getSqlFilters() == null) {
            logger.error("ListingFilterDto or SqlFilters is null");
            return new FilteredListingsDto(Collections.emptyList(),
                    listingService.getAllListings(0 ,21 ,"createdat" ,"asc")
            );
        }

        if (filterDto.getSqlFilters().getCategory() == null) {
            return new FilteredListingsDto(Collections.emptyList(),
                    listingService.getAllListings(
                            (filterDto.getPage() == null || filterDto.getPage() < 0) ? 0 : filterDto.getPage(),

                            (filterDto.getSize() == null || filterDto.getSize() <= 0) ? 21 : filterDto.getSize(),

                            (filterDto.getSortBy() == null || filterDto.getSortBy().trim().isEmpty()) ? "createdat" : filterDto.getSortBy(),

                            (filterDto.getSortOrder() == null || filterDto.getSortOrder().trim().isEmpty()) ? "asc" : filterDto.getSortOrder()
                    )
            );
        }

        List<Long> sqlListingIds = filterSql(filterDto.getSqlFilters());
        logger.debug("SQL Listing IDs (sqlListingIds): {}", sqlListingIds);

        if (sqlListingIds.isEmpty()) {
            logger.info("No listings found in SQL with provided filters.");
            return new FilteredListingsDto(Collections.emptyList(), new PageImpl<>(Collections.emptyList(), Pageable.unpaged(), 0));
        }

        List<Long> mongoListingIds = filterMongo(filterDto.getSqlFilters().getCategory(), filterDto.getMongoFilters(), sqlListingIds);
        logger.debug("MongoDB Listing IDs (mongoListingIds): {}", mongoListingIds);

        if (mongoListingIds.isEmpty()) {
            logger.info("No listings found in MongoDB with provided filters.");
            return new FilteredListingsDto(Collections.emptyList(), new PageImpl<>(Collections.emptyList(), Pageable.unpaged(), 0));
        }

        List<ListingDto> premiumListings = getPremiumListings(mongoListingIds);
        logger.debug("Premium listings (fetched optimally): {}", premiumListings.stream().map(ListingDto::getId).collect(Collectors.toList()));

        Set<Long> premiumIds = premiumListings.stream()
                .filter(Objects::nonNull)
                .map(ListingDto::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<Long> regularListingIds = mongoListingIds.stream()
                .filter(id -> !premiumIds.contains(id))
                .collect(Collectors.toList());
        logger.debug("Regular listing IDs (for pagination): {}", regularListingIds);


        int page = filterDto.getPage() != null ? filterDto.getPage() : 0;
        int size = filterDto.getSize() != null ? filterDto.getSize() : 21;
        Sort sort;

        Page<Listing> paginatedRegularListings;
        Sort.Direction sortDirection = filterDto.getSortOrder() != null && filterDto.getSortOrder().equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        String sortByField = filterDto.getSortBy();

        if (sortByField != null && sortByField.equalsIgnoreCase("rating")) {
            paginatedRegularListings = findListingsSortedByRating(regularListingIds, page, size, sortDirection);
        } else {
            String entitySortBy;
            switch (sortByField != null ? sortByField.toLowerCase() : "") {
                case "price":
                    entitySortBy = "price";
                    break;
                case "views":
                    entitySortBy = "viewCount";
                    break;
                case "id":
                    entitySortBy = "id";
                    break;
                default:
                    entitySortBy = "createdAt";
                    logger.warn("Unrecognized sort by field: {}. Defaulting to 'id' for sorting.", filterDto.getSortBy());
                    break;
            }
            sort = Sort.by(sortDirection, entitySortBy);
            Pageable pageable = PageRequest.of(page, size, sort);
            paginatedRegularListings = listingRepository.findByIdIn(regularListingIds, pageable);
        }

        logger.debug("Listings retrieved from repository (paginated regular listings): {}", paginatedRegularListings.getContent());

        Page<ListingDto> paginatedListingsDto = paginatedRegularListings.map(this::convertListingToDto);

        logger.info("Filtered listings: premiumListings.size()={}, paginatedListings.totalElements()={}", premiumListings.size(), paginatedListingsDto.getTotalElements());

        return new FilteredListingsDto(premiumListings, paginatedListingsDto);
    }

    private Page<Listing> findListingsSortedByRating(List<Long> ids, int page, int size, Sort.Direction sortDirection) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<Listing> cq = cb.createQuery(Listing.class);
        Root<Listing> listingRoot = cq.from(Listing.class);
        jakarta.persistence.criteria.Join<Listing, Rating> ratingJoin = listingRoot.join("ratings", JoinType.LEFT);

        if (ids != null && !ids.isEmpty()) {
            cq.where(listingRoot.get("id").in(ids));
        } else {
            return new PageImpl<>(Collections.emptyList(), PageRequest.of(page, size), 0);
        }

        cq.groupBy(listingRoot.get("id")); // Группируем по ID листинга

        Expression<Double> avgRating = cb.coalesce(cb.avg(ratingJoin.get("value")), 0.0);

        if (sortDirection == Sort.Direction.DESC) {
            cq.orderBy(cb.desc(avgRating), cb.desc(listingRoot.get("id")));
        } else {
            cq.orderBy(cb.asc(avgRating), cb.asc(listingRoot.get("id")));
        }

        TypedQuery<Listing> typedQuery = entityManager.createQuery(cq);
        typedQuery.setFirstResult(page * size);
        typedQuery.setMaxResults(size);

        List<Listing> resultList = typedQuery.getResultList();

        CriteriaQuery<Long> countCq = cb.createQuery(Long.class);
        Root<Listing> countListingRoot = countCq.from(Listing.class);
        countCq.select(cb.countDistinct(countListingRoot.get("id")));

        if (!ids.isEmpty()) {
            countCq.where(countListingRoot.get("id").in(ids));
        } else {
            return new PageImpl<>(Collections.emptyList(), PageRequest.of(page, size), 0);
        }

        Long totalElements = entityManager.createQuery(countCq).getSingleResult();

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "rating"));
        return new PageImpl<>(resultList, pageable, totalElements);
    }

    private List<ListingDto> getPremiumListings(List<Long> allFilteredListingIds) {
        if (allFilteredListingIds == null || allFilteredListingIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<Listing> premiumListingsFromDb = listingRepository.findPremiumListingsByIdInAndActiveSubscription(allFilteredListingIds);

        return premiumListingsFromDb.stream()
                .map(this::convertListingToDto)
                .collect(Collectors.toList());
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

    private List<Long> filterSql(SqlListingFilterDto sqlFilters) {
        logger.debug("Entering filterSql with sqlFilters: {}", sqlFilters);

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<Listing> listingRoot = criteriaQuery.from(Listing.class);
        criteriaQuery.select(listingRoot.get("id"));

        List<Predicate> predicates = new ArrayList<>();

        predicates.add(criteriaBuilder.equal(listingRoot.get("status"), ListingStatus.ACTIVE));

        if (sqlFilters != null) {
            if (sqlFilters.getTitle() != null && !sqlFilters.getTitle().isEmpty()) {
                logger.debug("Adding SQL filter: title like '%{}%'", sqlFilters.getTitle());
                predicates.add(criteriaBuilder.like(listingRoot.get("title"), "%" + sqlFilters.getTitle() + "%"));
            }
            if (sqlFilters.getCategory() != null) {
                logger.debug("Adding SQL filter: category.id = {}", sqlFilters.getCategory());
                predicates.add(criteriaBuilder.equal(listingRoot.get("category").get("id"), sqlFilters.getCategory()));
            }
            if (sqlFilters.getSubCategory() != null) {
                logger.debug("Adding SQL filter: subCategory.id = {}", sqlFilters.getSubCategory());
                predicates.add(criteriaBuilder.equal(listingRoot.get("subCategory").get("id"), sqlFilters.getSubCategory()));
            }
            if (sqlFilters.getCountry() != null && !sqlFilters.getCountry().isEmpty()) {
                logger.debug("Adding SQL filter: country like '%{}%'", sqlFilters.getCountry());
                predicates.add(criteriaBuilder.like(listingRoot.get("country"), "%" + sqlFilters.getCountry() + "%"));
            }
            if (sqlFilters.getCity() != null && !sqlFilters.getCity().isEmpty()) {
                logger.debug("Adding SQL filter: city like '%{}%'", sqlFilters.getCity());
                predicates.add(criteriaBuilder.like(listingRoot.get("city"), "%" + sqlFilters.getCity() + "%"));
            }
            if (sqlFilters.getInvest() != null) {
                logger.debug("Adding SQL filter: isInvest = {}", sqlFilters.getInvest());
                predicates.add(criteriaBuilder.equal(listingRoot.get("invest"), sqlFilters.getInvest()));
            }
            if (sqlFilters.getMinPrice() != null) {
                logger.debug("Adding SQL filter: price >= {}", sqlFilters.getMinPrice());
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(listingRoot.get("price"), sqlFilters.getMinPrice()));
            }
            if (sqlFilters.getMaxPrice() != null) {
                logger.debug("Adding SQL filter: price <= {}", sqlFilters.getMaxPrice());
                predicates.add(criteriaBuilder.lessThanOrEqualTo(listingRoot.get("price"), sqlFilters.getMaxPrice()));
            }
        }

        criteriaQuery.where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));

        try {
            return entityManager.createQuery(criteriaQuery).getResultList();
        } catch (JDBCConnectionException e) {
            logger.error("Error connecting to the database. SQL filters failed.", e);
            return Collections.emptyList();
        } catch (IllegalArgumentException e) {
            logger.error("Invalid query syntax in SQL filters.", e);
            return Collections.emptyList();
        } catch (PersistenceException e) {
            logger.error("Unexpected persistence error in SQL filters.", e);
            return Collections.emptyList();
        }
    }

    private List<Long> filterMongo(Long categoryId, Map<String, Object> mongoFilters, List<Long> sqlListingIds) {
        logger.debug("Starting filterMongo with categoryId: {}, mongoFilters: {}, sqlListingIds: {}", categoryId, mongoFilters, sqlListingIds);

        Query query = new Query(Criteria.where("categoryId").is(categoryId));

        if (sqlListingIds == null || sqlListingIds.isEmpty()) {
            logger.debug("sqlListingIds is null or empty, no MongoDB query will be performed.");
            return Collections.emptyList();
        }
        logger.debug("Adding SQL listing IDs to MongoDB query: {}", sqlListingIds);
        query.addCriteria(Criteria.where("listingId").in(sqlListingIds));


        if (mongoFilters != null && !mongoFilters.isEmpty()) {
            logger.debug("MongoDB filters are not null or empty. Processing filters.");
            CategoryField categoryField = mongoTemplate.findOne(Query.query(Criteria.where("categoryId").is(categoryId)), CategoryField.class);

            if (categoryField == null || categoryField.getFields() == null) {
                logger.warn("Category fields not found for categoryId: {}", categoryId);
                return Collections.emptyList();
            }

            List<Field> fields = categoryField.getFields();
            List<Criteria> allOrCriteria = new ArrayList<>();

            for (Map.Entry<String, Object> entry : mongoFilters.entrySet()) {
                String fieldName = entry.getKey();
                Object fieldValue = entry.getValue();

                Optional<Field> fieldOptional = fields.stream()
                        .filter(field -> field.getName().equals(fieldName) && field.getFilterable())
                        .findFirst();

                if (fieldOptional.isPresent()) {
                    String fieldType = fieldOptional.get().getType();
                    logger.debug("Processing field: {}, type: {}, value: {}", fieldName, fieldType, fieldValue);

                    if (fieldType.equalsIgnoreCase("Integer") || fieldType.equalsIgnoreCase("Double") || fieldType.equalsIgnoreCase("Long")) {
                        if (fieldValue instanceof Map<?, ?> rangeMap) {
                            Criteria criteria = Criteria.where("fields." + fieldName);
                            if (rangeMap.containsKey("min") && rangeMap.get("min") instanceof Number) {
                                logger.debug("Adding MongoDB filter: {} >= {}", fieldName, ((Number) rangeMap.get("min")).doubleValue());
                                criteria.gte(((Number) rangeMap.get("min")).doubleValue());
                            }
                            if (rangeMap.containsKey("max") && rangeMap.get("max") instanceof Number) {
                                logger.debug("Adding MongoDB filter: {} <= {}", fieldName, ((Number) rangeMap.get("max")).doubleValue());
                                criteria.lte(((Number) rangeMap.get("max")).doubleValue());
                            }
                            query.addCriteria(criteria);
                        } else {
                            logger.warn("Expected range filter for numeric field {}, but got: {}", fieldName, fieldValue);
                        }
                    } else if (fieldType.equalsIgnoreCase("String")) {
                        if (fieldValue instanceof List<?>) {
                            List<Criteria> orCriteriaForField = new ArrayList<>();
                            for (Object value : (List<?>) fieldValue) {
                                orCriteriaForField.add(Criteria.where("fields." + fieldName).is(value));
                            }
                            if (!orCriteriaForField.isEmpty()) {
                                allOrCriteria.add(new Criteria().orOperator(orCriteriaForField.toArray(new Criteria[0])));
                            }
                        } else {
                            query.addCriteria(Criteria.where("fields." + fieldName).is(fieldValue));
                        }
                    } else if (fieldType.equalsIgnoreCase("Boolean")) {
                        if (fieldValue instanceof List<?>) {
                            List<Boolean> booleanValues = ((List<?>) fieldValue).stream()
                                    .filter(val -> val instanceof Boolean || val instanceof String)
                                    .map(val -> val instanceof Boolean ? (Boolean) val : Boolean.parseBoolean(val.toString()))
                                    .collect(Collectors.toList());
                            logger.debug("Adding MongoDB filter: {} in {}", fieldName, booleanValues);
                            query.addCriteria(Criteria.where("fields." + fieldName).in(booleanValues));
                        } else {
                            boolean parsedValue = fieldValue instanceof Boolean ? (Boolean) fieldValue : Boolean.parseBoolean(fieldValue.toString());
                            logger.debug("Adding MongoDB filter: {} is {}", fieldName, parsedValue);
                            query.addCriteria(Criteria.where("fields." + fieldName).is(parsedValue));
                        }
                    }
                } else {
                    logger.warn("Field {} is not filterable or not found for categoryId: {}", fieldName, categoryId);
                }
            }
            if (!allOrCriteria.isEmpty()) {
                query.addCriteria(new Criteria().andOperator(allOrCriteria.toArray(new Criteria[0])));
            }
        }

        logger.debug("MongoDB query: {}", query.getQueryObject().toJson());

        try {
            List<Long> result = mongoTemplate.find(query, ListingMongo.class, "listings").stream()
                    .map(ListingMongo::getListingId)
                    .collect(Collectors.toList());
            logger.debug("MongoDB query result: {}", result);
            return result;
        } catch (DataAccessException e) {
            logger.error("Error executing MongoDB query", e);
            return Collections.emptyList();
        }
    }


    public int countNewListings(String filtersJson, LocalDateTime from, LocalDateTime to) {
        logger.info("Counting new listings created between {} and {} with filters: {}", from, to, filtersJson);

        JsonNode filtersNode = null;
        SqlListingFilterDto sqlFilters = null;
        Map<String, Object> mongoFilters = null;
        Long categoryId = null;

        if (filtersJson != null && !filtersJson.isEmpty()) {
            try {
                filtersNode = objectMapper.readTree(filtersJson);

                JsonNode filteredParamsNode = filtersNode.get("filteredParams");
                if (filteredParamsNode != null) {
                    JsonNode sqlFiltersNode = filteredParamsNode.get("sqlFilters");
                    if (sqlFiltersNode != null) {
                        sqlFilters = objectMapper.treeToValue(sqlFiltersNode, SqlListingFilterDto.class);
                        JsonNode categoryNode = sqlFiltersNode.get("category");
                        if (categoryNode != null && categoryNode.isInt()) {
                            categoryId = (long) categoryNode.intValue();
                        }
                    }
                    JsonNode mongoFiltersNode = filteredParamsNode.get("mongoFilters");
                    if (mongoFiltersNode != null && mongoFiltersNode.isObject()) {
                        mongoFilters = objectMapper.convertValue(mongoFiltersNode, new TypeReference<Map<String, Object>>() {});
                    }
                }
            } catch (IOException e) {
                logger.error("Failed to deserialize filters JSON: {}", e.getMessage(), e);
                return 0;
            }
        }

        // Count SQL results
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> sqlCriteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<Listing> listingRoot = sqlCriteriaQuery.from(Listing.class);
        sqlCriteriaQuery.select(criteriaBuilder.count(listingRoot));

        List<Predicate> sqlPredicates = new ArrayList<>();
        sqlPredicates.add(criteriaBuilder.greaterThanOrEqualTo(listingRoot.get("createdAt"), from));
        sqlPredicates.add(criteriaBuilder.lessThan(listingRoot.get("createdAt"), to));
        sqlPredicates.add(criteriaBuilder.equal(listingRoot.get("status"), ListingStatus.ACTIVE));

        if (sqlFilters != null) {
            if (sqlFilters.getTitle() != null && !sqlFilters.getTitle().isEmpty()) {
                sqlPredicates.add(criteriaBuilder.like(listingRoot.get("title"), "%" + sqlFilters.getTitle() + "%"));
            }
            if (sqlFilters.getCategory() != null) {
                sqlPredicates.add(criteriaBuilder.equal(listingRoot.get("category").get("id"), sqlFilters.getCategory()));
            }
            if (sqlFilters.getSubCategory() != null) {
                sqlPredicates.add(criteriaBuilder.equal(listingRoot.get("subCategory").get("id"), sqlFilters.getSubCategory()));
            }
            if (sqlFilters.getCountry() != null && !sqlFilters.getCountry().isEmpty()) {
                sqlPredicates.add(criteriaBuilder.like(listingRoot.get("country"), "%" + sqlFilters.getCountry() + "%"));
            }
            if (sqlFilters.getCity() != null && !sqlFilters.getCity().isEmpty()) {
                sqlPredicates.add(criteriaBuilder.like(listingRoot.get("city"), "%" + sqlFilters.getCity() + "%"));
            }
            if (sqlFilters.getInvest() != null) {
                sqlPredicates.add(criteriaBuilder.equal(listingRoot.get("invest"), sqlFilters.getInvest()));
            }
            if (sqlFilters.getMinPrice() != null) {
                sqlPredicates.add(criteriaBuilder.greaterThanOrEqualTo(listingRoot.get("price"), sqlFilters.getMinPrice()));
            }
            if (sqlFilters.getMaxPrice() != null) {
                sqlPredicates.add(criteriaBuilder.lessThanOrEqualTo(listingRoot.get("price"), sqlFilters.getMaxPrice()));
            }
        }
        sqlCriteriaQuery.where(criteriaBuilder.and(sqlPredicates.toArray(new Predicate[0])));

        List<Long> sqlListingIds = Collections.emptyList();
        try {
            sqlListingIds = entityManager.createQuery(sqlCriteriaQuery).getResultList();
            logger.debug("SQL Listing IDs for count: {}", sqlListingIds);
        } catch (JDBCConnectionException e) {
            logger.error("Error connecting to the database while counting listings (SQL).", e);
            return 0;
        } catch (IllegalArgumentException e) {
            logger.error("Invalid query syntax while counting listings (SQL).", e);
            return 0;
        } catch (PersistenceException e) {
            logger.error("Unexpected persistence error while counting listings (SQL).", e);
            return 0;
        }

        if (categoryId == null || sqlListingIds.isEmpty()) {
            return sqlListingIds.size();
        }

        // Count MongoDB results based on SQL results and MongoDB filters
        Query mongoQuery = new Query(Criteria.where("categoryId").is(categoryId));
        mongoQuery.addCriteria(Criteria.where("listingId").in(sqlListingIds));
        mongoQuery = applyMongoFilters(categoryId, mongoFilters, mongoQuery);

        long mongoCount = 0;
        try {
            mongoCount = mongoTemplate.count(mongoQuery, ListingMongo.class, "listings");
            logger.debug("MongoDB count for new listings: {}", mongoCount);
        } catch (DataAccessException e) {
            logger.error("Error executing MongoDB query for counting new listings.", e);
            return 0;
        }

        return (int) mongoCount;
    }

    private Query applyMongoFilters(Long categoryId, Map<String, Object> mongoFilters, Query query) {
        if (mongoFilters != null && !mongoFilters.isEmpty()) {
            CategoryField categoryField = mongoTemplate.findOne(Query.query(Criteria.where("categoryId").is(categoryId)), CategoryField.class);

            if (categoryField == null || categoryField.getFields() == null) {
                logger.warn("Category fields not found for categoryId: {}", categoryId);
                return query;
            }

            List<Field> fields = categoryField.getFields();
            List<Criteria> allOrCriteria = new ArrayList<>();

            for (Map.Entry<String, Object> entry : mongoFilters.entrySet()) {
                String fieldName = entry.getKey();
                Object fieldValue = entry.getValue();

                Optional<Field> fieldOptional = fields.stream()
                        .filter(field -> field.getName().equals(fieldName) && field.getFilterable())
                        .findFirst();

                if (fieldOptional.isPresent()) {
                    String fieldType = fieldOptional.get().getType();
                    logger.debug("Processing MongoDB filter field: {}, type: {}, value: {}", fieldName, fieldType, fieldValue);

                    if (fieldType.equalsIgnoreCase("Integer") || fieldType.equalsIgnoreCase("Double") || fieldType.equalsIgnoreCase("Long")) {
                        if (fieldValue instanceof Map<?, ?> rangeMap) {
                            Criteria criteria = Criteria.where("fields." + fieldName);
                            if (rangeMap.containsKey("min") && rangeMap.get("min") instanceof Number) {
                                criteria.gte(((Number) rangeMap.get("min")).doubleValue());
                            }
                            if (rangeMap.containsKey("max") && rangeMap.get("max") instanceof Number) {
                                criteria.lte(((Number) rangeMap.get("max")).doubleValue());
                            }
                            query.addCriteria(criteria);
                        } else {
                            logger.warn("Expected range filter for numeric field {}, but got: {}", fieldName, fieldValue);
                        }
                    } else if (fieldType.equalsIgnoreCase("String")) {
                        if (fieldValue instanceof List<?>) {
                            List<Criteria> orCriteriaForField = new ArrayList<>();
                            for (Object value : (List<?>) fieldValue) {
                                orCriteriaForField.add(Criteria.where("fields." + fieldName).is(value));
                            }
                            if (!orCriteriaForField.isEmpty()) {
                                allOrCriteria.add(new Criteria().orOperator(orCriteriaForField.toArray(new Criteria[0])));
                            }
                        } else {
                            query.addCriteria(Criteria.where("fields." + fieldName).is(fieldValue));
                        }
                    } else if (fieldType.equalsIgnoreCase("Boolean")) {
                        if (fieldValue instanceof List<?>) {
                            List<Boolean> booleanValues = ((List<?>) fieldValue).stream()
                                    .filter(val -> val instanceof Boolean || val instanceof String)
                                    .map(val -> val instanceof Boolean ? (Boolean) val : Boolean.parseBoolean(val.toString()))
                                    .collect(Collectors.toList());
                            query.addCriteria(Criteria.where("fields." + fieldName).in(booleanValues));
                        } else {
                            boolean parsedValue = fieldValue instanceof Boolean ? (Boolean) fieldValue : Boolean.parseBoolean(fieldValue.toString());
                            query.addCriteria(Criteria.where("fields." + fieldName).is(parsedValue));
                        }
                    }
                } else {
                    logger.warn("Field {} is not filterable or not found for categoryId: {}", fieldName, categoryId);
                }
            }
            if (!allOrCriteria.isEmpty()) {
                query.addCriteria(new Criteria().andOperator(allOrCriteria.toArray(new Criteria[0])));
            }
        }
        return query;
    }
}