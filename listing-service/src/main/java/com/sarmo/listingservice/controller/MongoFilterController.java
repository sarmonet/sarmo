package com.sarmo.listingservice.controller;

import com.sarmo.listingservice.service.MongoFilterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/listing/mongo/filter")
public class MongoFilterController {

    private final MongoFilterService mongoFilterService;

    @Autowired
    public MongoFilterController(MongoFilterService mongoFilterService) {
        this.mongoFilterService = mongoFilterService;
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<Map<String, Object>> getPopulatedFilters(@PathVariable Long categoryId) {
        Map<String, Object> populatedFilters = mongoFilterService.getPopulatedFilters(categoryId);
        return ResponseEntity.ok(populatedFilters);
    }
}