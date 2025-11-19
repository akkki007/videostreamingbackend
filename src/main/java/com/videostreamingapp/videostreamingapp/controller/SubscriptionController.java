package com.videostreamingapp.videostreamingapp.controller;

import com.videostreamingapp.videostreamingapp.model.Subscription;
import com.videostreamingapp.videostreamingapp.service.SubscriptionService;
import com.videostreamingapp.videostreamingapp.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/subscriptions")
@CrossOrigin(origins = "*")
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private UserService userService;

    @PostMapping("/channel/{channelId}")
    public ResponseEntity<?> toggleSubscription(@PathVariable String channelId, HttpServletRequest request) {
        try {
            Authentication authentication = org.springframework.security.core.context.SecurityContextHolder
                    .getContext().getAuthentication();
            String username = authentication.getName();

            Optional<com.videostreamingapp.videostreamingapp.model.User> subscriberOpt = 
                    userService.findByUsername(username);
            if (subscriberOpt.isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "User not found");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }

            Optional<com.videostreamingapp.videostreamingapp.model.User> channelOpt = 
                    userService.findById(channelId);
            if (channelOpt.isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Channel not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }

            com.videostreamingapp.videostreamingapp.model.User subscriber = subscriberOpt.get();
            com.videostreamingapp.videostreamingapp.model.User channel = channelOpt.get();

            boolean isSubscribed = subscriptionService.toggleSubscription(
                    channelId, channel.getUsername(), 
                    subscriber.getId(), subscriber.getUsername());

            Map<String, Object> response = new HashMap<>();
            response.put("isSubscribed", isSubscribed);
            response.put("subscriberCount", subscriptionService.getSubscriberCount(channelId));
            response.put("message", isSubscribed ? "Subscribed successfully" : "Unsubscribed successfully");
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to toggle subscription: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/channel/{channelId}/status")
    public ResponseEntity<?> getSubscriptionStatus(@PathVariable String channelId, HttpServletRequest request) {
        try {
            Authentication authentication = org.springframework.security.core.context.SecurityContextHolder
                    .getContext().getAuthentication();
            String username = authentication.getName();

            Optional<com.videostreamingapp.videostreamingapp.model.User> userOpt = 
                    userService.findByUsername(username);
            if (userOpt.isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "User not found");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }

            com.videostreamingapp.videostreamingapp.model.User user = userOpt.get();
            boolean isSubscribed = subscriptionService.isSubscribed(user.getId(), channelId);
            long subscriberCount = subscriptionService.getSubscriberCount(channelId);

            Map<String, Object> response = new HashMap<>();
            response.put("isSubscribed", isSubscribed);
            response.put("subscriberCount", subscriberCount);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get subscription status: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/channel/{channelId}/count")
    public ResponseEntity<?> getSubscriberCount(@PathVariable String channelId) {
        try {
            long subscriberCount = subscriptionService.getSubscriberCount(channelId);
            Map<String, Object> response = new HashMap<>();
            response.put("subscriberCount", subscriberCount);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get subscriber count: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/my-subscriptions")
    public ResponseEntity<?> getMySubscriptions(HttpServletRequest request) {
        try {
            Authentication authentication = org.springframework.security.core.context.SecurityContextHolder
                    .getContext().getAuthentication();
            String username = authentication.getName();

            Optional<com.videostreamingapp.videostreamingapp.model.User> userOpt = 
                    userService.findByUsername(username);
            if (userOpt.isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "User not found");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }

            com.videostreamingapp.videostreamingapp.model.User user = userOpt.get();
            List<Subscription> subscriptions = subscriptionService.getSubscriptions(user.getId());
            
            List<Map<String, Object>> subscriptionList = subscriptions.stream()
                    .map(sub -> {
                        Map<String, Object> subMap = new HashMap<>();
                        subMap.put("channelId", sub.getChannelId());
                        subMap.put("channelUsername", sub.getChannelUsername());
                        subMap.put("subscribedAt", sub.getSubscribedAt());
                        return subMap;
                    })
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("subscriptions", subscriptionList);
            response.put("count", subscriptionList.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get subscriptions: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/channel/{channelId}/subscribers")
    public ResponseEntity<?> getSubscribers(@PathVariable String channelId) {
        try {
            List<Subscription> subscribers = subscriptionService.getSubscribers(channelId);
            
            List<Map<String, Object>> subscriberList = subscribers.stream()
                    .map(sub -> {
                        Map<String, Object> subMap = new HashMap<>();
                        subMap.put("subscriberId", sub.getSubscriberId());
                        subMap.put("subscriberUsername", sub.getSubscriberUsername());
                        subMap.put("subscribedAt", sub.getSubscribedAt());
                        return subMap;
                    })
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("subscribers", subscriberList);
            response.put("count", subscriberList.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get subscribers: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}

