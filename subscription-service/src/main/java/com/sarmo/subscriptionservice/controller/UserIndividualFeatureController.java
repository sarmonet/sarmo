package com.sarmo.subscriptionservice.controller;

import com.sarmo.subscriptionservice.entity.UserIndividualFeature;
import com.sarmo.subscriptionservice.service.UserIndividualFeatureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/subscription/user-individual-features")
public class UserIndividualFeatureController {

    private final UserIndividualFeatureService userIndividualFeatureService;

    public UserIndividualFeatureController(UserIndividualFeatureService userIndividualFeatureService) {
        this.userIndividualFeatureService = userIndividualFeatureService;
    }

    @GetMapping
    public ResponseEntity<List<UserIndividualFeature>> getAllUserIndividualFeatures() {
        List<UserIndividualFeature> features = userIndividualFeatureService.getAllUserIndividualFeatures();
        return new ResponseEntity<>(features, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserIndividualFeature> getUserIndividualFeatureById(@PathVariable Long id) {
        Optional<UserIndividualFeature> feature = userIndividualFeatureService.getUserIndividualFeatureById(id);
        return feature.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserIndividualFeature>> getUserIndividualFeaturesByUserId(@PathVariable Long userId) {
        List<UserIndividualFeature> features = userIndividualFeatureService.getUserIndividualFeaturesByUserId(userId);
        return new ResponseEntity<>(features, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<UserIndividualFeature> createUserIndividualFeature(@RequestBody UserIndividualFeature userIndividualFeature) {
        UserIndividualFeature createdFeature = userIndividualFeatureService.createUserIndividualFeature(userIndividualFeature);
        if (createdFeature != null) {
            return new ResponseEntity<>(createdFeature, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserIndividualFeature> updateUserIndividualFeature(@PathVariable Long id, @RequestBody UserIndividualFeature updatedFeature) {
        UserIndividualFeature updated = userIndividualFeatureService.updateUserIndividualFeature(id, updatedFeature);
        if (updated != null) {
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserIndividualFeature(@PathVariable Long id) {
        userIndividualFeatureService.deleteUserIndividualFeature(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}