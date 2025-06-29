package com.sarmo.subscriptionservice.controller;

import com.sarmo.subscriptionservice.entity.UserSubscription;
import com.sarmo.subscriptionservice.service.UserSubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/subscription/user-subscription")
public class UserSubscriptionController {

    private final UserSubscriptionService userSubscriptionService;

    public UserSubscriptionController(UserSubscriptionService userSubscriptionService) {
        this.userSubscriptionService = userSubscriptionService;
    }

    @GetMapping
    public ResponseEntity<List<UserSubscription>> getAllUserSubscriptions() {
        List<UserSubscription> subscriptions = userSubscriptionService.getAllUserSubscriptions();
        return new ResponseEntity<>(subscriptions, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserSubscription> getUserSubscriptionById(@PathVariable Long id) {
        Optional<UserSubscription> subscription = userSubscriptionService.getUserSubscriptionById(id);
        return subscription.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserSubscription>> getUserSubscriptionsByUserId(@PathVariable Long userId) {
        List<UserSubscription> subscriptions = userSubscriptionService.getUserSubscriptionsByUserId(userId);
        return new ResponseEntity<>(subscriptions, HttpStatus.OK);
    }

    @GetMapping("/user/{userId}/active")
    public ResponseEntity<UserSubscription> findActiveUserSubscription(@PathVariable Long userId) {
        Optional<UserSubscription> activeSubscription = userSubscriptionService.findActiveUserSubscription(userId);
        return activeSubscription.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<UserSubscription> createUserSubscription(@RequestBody UserSubscription userSubscription) {
        UserSubscription createdSubscription = userSubscriptionService.createUserSubscription(userSubscription);
        if (createdSubscription != null) {
            return new ResponseEntity<>(createdSubscription, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserSubscription> updateUserSubscription(@PathVariable Long id, @RequestBody UserSubscription updatedSubscription) {
        UserSubscription updated = userSubscriptionService.updateUserSubscription(id, updatedSubscription);
        if (updated != null) {
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserSubscription(@PathVariable Long id) {
        userSubscriptionService.deleteUserSubscription(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}