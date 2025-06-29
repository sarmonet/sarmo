package com.sarmo.subscriptionservice.controller;

import com.sarmo.subscriptionservice.entity.IndividualFeature;
import com.sarmo.subscriptionservice.service.IndividualFeatureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/subscription/individual-features")
public class IndividualFeatureController {

    private final IndividualFeatureService individualFeatureService;

    public IndividualFeatureController(IndividualFeatureService individualFeatureService) {
        this.individualFeatureService = individualFeatureService;
    }

    @GetMapping
    public ResponseEntity<List<IndividualFeature>> getAllIndividualFeatures() {
        List<IndividualFeature> features = individualFeatureService.getAllIndividualFeatures();
        return new ResponseEntity<>(features, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<IndividualFeature> getIndividualFeatureById(@PathVariable Long id) {
        Optional<IndividualFeature> feature = individualFeatureService.getIndividualFeatureById(id);
        return feature.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/by-name/{name}")
    public ResponseEntity<IndividualFeature> getIndividualFeatureByName(@PathVariable String name) {
        Optional<IndividualFeature> feature = individualFeatureService.getIndividualFeatureByName(name);
        return feature.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<IndividualFeature> createIndividualFeature(@RequestBody IndividualFeature individualFeature) {
        IndividualFeature createdFeature = individualFeatureService.createIndividualFeature(individualFeature);
        if (createdFeature != null) {
            return new ResponseEntity<>(createdFeature, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<IndividualFeature> updateIndividualFeature(@PathVariable Long id, @RequestBody IndividualFeature updatedFeature) {
        IndividualFeature updated = individualFeatureService.updateIndividualFeature(id, updatedFeature);
        if (updated != null) {
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIndividualFeature(@PathVariable Long id) {
        individualFeatureService.deleteIndividualFeature(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}