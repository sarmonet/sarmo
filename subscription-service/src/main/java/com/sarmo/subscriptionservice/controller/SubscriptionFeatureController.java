package com.sarmo.subscriptionservice.controller;

import com.sarmo.subscriptionservice.entity.SubscriptionFeature;
import com.sarmo.subscriptionservice.service.SubscriptionFeatureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/subscription/features")
public class SubscriptionFeatureController {

    private final SubscriptionFeatureService subscriptionFeatureService;

    public SubscriptionFeatureController(SubscriptionFeatureService subscriptionFeatureService) {
        this.subscriptionFeatureService = subscriptionFeatureService;
    }

    @GetMapping
    public ResponseEntity<List<SubscriptionFeature>> getAllSubscriptionFeatures() {
        List<SubscriptionFeature> features = subscriptionFeatureService.getAllSubscriptionFeatures();
        return new ResponseEntity<>(features, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubscriptionFeature> getSubscriptionFeatureById(@PathVariable Long id) {
        Optional<SubscriptionFeature> feature = subscriptionFeatureService.getSubscriptionFeatureById(id);
        return feature.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/by-name/{name}")
    public ResponseEntity<SubscriptionFeature> getSubscriptionFeatureByName(@PathVariable String name) {
        Optional<SubscriptionFeature> feature = subscriptionFeatureService.getSubscriptionFeatureByName(name);
        return feature.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<SubscriptionFeature> createSubscriptionFeature(@RequestBody SubscriptionFeature subscriptionFeature) {
        SubscriptionFeature createdFeature = subscriptionFeatureService.createSubscriptionFeature(subscriptionFeature);
        if (createdFeature != null) {
            return new ResponseEntity<>(createdFeature, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<SubscriptionFeature> updateSubscriptionFeature(@PathVariable Long id, @RequestBody SubscriptionFeature updatedFeature) {
        SubscriptionFeature updated = subscriptionFeatureService.updateSubscriptionFeature(id, updatedFeature);
        if (updated != null) {
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubscriptionFeature(@PathVariable Long id) {
        subscriptionFeatureService.deleteSubscriptionFeature(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}