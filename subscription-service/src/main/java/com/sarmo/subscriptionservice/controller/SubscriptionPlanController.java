package com.sarmo.subscriptionservice.controller;

import com.sarmo.subscriptionservice.dto.CreateSubscriptionPlanDTO;
import com.sarmo.subscriptionservice.entity.SubscriptionPlan;
import com.sarmo.subscriptionservice.service.SubscriptionPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/subscription/plan")
public class SubscriptionPlanController {

    private final SubscriptionPlanService subscriptionPlanService;

    public SubscriptionPlanController(SubscriptionPlanService subscriptionPlanService) {
        this.subscriptionPlanService = subscriptionPlanService;
    }

    @GetMapping
    public ResponseEntity<List<SubscriptionPlan>> getAllSubscriptionPlans() {
        List<SubscriptionPlan> plans = subscriptionPlanService.getAllSubscriptionPlans();
        return new ResponseEntity<>(plans, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubscriptionPlan> getSubscriptionPlanById(@PathVariable Long id) {
        Optional<SubscriptionPlan> plan = subscriptionPlanService.getSubscriptionPlanById(id);
        return plan.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/by-name/{name}")
    public ResponseEntity<SubscriptionPlan> getSubscriptionPlanByName(@PathVariable String name) {
        Optional<SubscriptionPlan> plan = subscriptionPlanService.getSubscriptionPlanByName(name);
        return plan.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<SubscriptionPlan> createSubscriptionPlan(@RequestBody CreateSubscriptionPlanDTO subscriptionPlan) {
        SubscriptionPlan createdPlan = subscriptionPlanService.createSubscriptionPlan(subscriptionPlan);
        if (createdPlan != null) {
            return new ResponseEntity<>(createdPlan, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<SubscriptionPlan> updateSubscriptionPlan(@PathVariable Long id, @RequestBody SubscriptionPlan updatedPlan) {
        SubscriptionPlan updated = subscriptionPlanService.updateSubscriptionPlan(id, updatedPlan);
        if (updated != null) {
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubscriptionPlan(@PathVariable Long id) {
        subscriptionPlanService.deleteSubscriptionPlan(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}