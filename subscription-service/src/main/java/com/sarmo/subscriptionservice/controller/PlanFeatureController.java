package com.sarmo.subscriptionservice.controller;

import com.sarmo.subscriptionservice.entity.PlanFeature;
import com.sarmo.subscriptionservice.service.PlanFeatureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/subscription/plan-features")
public class PlanFeatureController {

    private final PlanFeatureService planFeatureService;

    public PlanFeatureController(PlanFeatureService planFeatureService) {
        this.planFeatureService = planFeatureService;
    }

    @GetMapping
    public ResponseEntity<List<PlanFeature>> getAllPlanFeatures() {
        List<PlanFeature> planFeatures = planFeatureService.getAllPlanFeatures();
        return new ResponseEntity<>(planFeatures, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanFeature> getPlanFeatureById(@PathVariable Long id) {
        Optional<PlanFeature> planFeature = planFeatureService.getPlanFeatureById(id);
        return planFeature.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/by-plan/{planId}")
    public ResponseEntity<List<PlanFeature>> getPlanFeaturesByPlanId(@PathVariable Long planId) {
        List<PlanFeature> planFeatures = planFeatureService.getPlanFeaturesByPlanId(planId);
        return new ResponseEntity<>(planFeatures, HttpStatus.OK);
    }

    @GetMapping("/by-plan/{planId}/feature/{featureName}")
    public ResponseEntity<PlanFeature> getPlanFeatureByPlanIdAndFeatureName(
            @PathVariable Long planId,
            @PathVariable String featureName) {
        Optional<PlanFeature> planFeature = planFeatureService.getPlanFeatureByPlanIdAndFeatureName(planId, featureName);
        return planFeature.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<PlanFeature> createPlanFeature(@RequestBody PlanFeature planFeature) {
        PlanFeature createdPlanFeature = planFeatureService.createPlanFeature(planFeature);
        if (createdPlanFeature != null) {
            return new ResponseEntity<>(createdPlanFeature, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlanFeature> updatePlanFeature(@PathVariable Long id, @RequestBody PlanFeature updatedPlanFeature) {
        PlanFeature updated = planFeatureService.updatePlanFeature(id, updatedPlanFeature);
        if (updated != null) {
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlanFeature(@PathVariable Long id) {
        planFeatureService.deletePlanFeature(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}