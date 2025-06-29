package com.sarmo.listingservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sarmo.listingservice.dto.CountNewListingsRequestDto;
import com.sarmo.listingservice.dto.FilteredListingsDto;
import com.sarmo.listingservice.dto.ListingFilterDto;
import com.sarmo.listingservice.entity.Listing;
import com.sarmo.listingservice.service.ListingFilterService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/listing/filter")
public class ListingFilterController {
    private final ListingFilterService listingFilterService;
    private final ObjectMapper objectMapper;
    private static final Logger logger = LoggerFactory.getLogger(ListingFilterController.class);

    public ListingFilterController(ListingFilterService listingFilterService, ObjectMapper objectMapper) {
        this.listingFilterService = listingFilterService;
        this.objectMapper = objectMapper;
    }

    @PostMapping
    public ResponseEntity<FilteredListingsDto> filterListings(@Valid @RequestBody ListingFilterDto filterDto) {
        FilteredListingsDto filteredListingsDto = listingFilterService.filterListings(filterDto);
        return ResponseEntity.ok(filteredListingsDto);
    }

    @PostMapping("/count-new")
    public ResponseEntity<Integer> countNewListings(@RequestBody CountNewListingsRequestDto requestDto) throws JsonProcessingException {
        LocalDateTime from = requestDto.getFrom();
        LocalDateTime to = requestDto.getTo();
        Object filters = requestDto.getFilters();
        int count = listingFilterService.countNewListings(objectMapper.writeValueAsString(filters), from, to);
        return ResponseEntity.ok(count);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralExceptions(Exception ex) {
        logger.error("An unexpected error occurred: {}", ex.getMessage(), ex);
        return ResponseEntity.internalServerError().body("An unexpected error occurred.");
    }
}
