package com.sarmo.listingservice.controller;

import com.sarmo.listingservice.dto.ListingDto;
import com.sarmo.listingservice.service.ListingRandomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/listing")
public class ListingRandomController {

    private final ListingRandomService listingRandomService;

    @Autowired
    public ListingRandomController(ListingRandomService listingRandomService) {
        this.listingRandomService = listingRandomService;
    }

    @GetMapping("/random/all")
    public List<ListingDto> getRandomListings( @RequestParam int count){
        return listingRandomService.getRandomListingsLimited(count);
    }

    @GetMapping("/random")
    public List<ListingDto> getRandomListingsByCategory(
            @RequestParam Long categoryId,
            @RequestParam(required = false) Long subcategoryId,
            @RequestParam int count) {
        if (subcategoryId != null) {
            return listingRandomService.getRandomListingsByCategoryAndSubcategory(categoryId, subcategoryId, count);
        } else {
            return listingRandomService.getRandomListingsByCategory(categoryId, count);
        }
    }
}