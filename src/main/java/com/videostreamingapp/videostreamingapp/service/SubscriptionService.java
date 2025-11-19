package com.videostreamingapp.videostreamingapp.service;

import com.videostreamingapp.videostreamingapp.model.Subscription;
import com.videostreamingapp.videostreamingapp.model.User;
import com.videostreamingapp.videostreamingapp.repository.SubscriptionRepository;
import com.videostreamingapp.videostreamingapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SubscriptionService {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private UserRepository userRepository;

    public boolean toggleSubscription(String channelId, String channelUsername, String subscriberId, String subscriberUsername) {
        if (channelId.equals(subscriberId)) {
            throw new IllegalArgumentException("Cannot subscribe to yourself");
        }

        Optional<Subscription> existingSubscription = subscriptionRepository.findBySubscriberIdAndChannelId(subscriberId, channelId);
        
        if (existingSubscription.isPresent()) {
            // Unsubscribe: Remove the subscription
            subscriptionRepository.delete(existingSubscription.get());
            updateSubscriberCount(channelId, -1);
            return false; // Not subscribed anymore
        } else {
            // Subscribe: Create new subscription
            Subscription subscription = new Subscription(subscriberId, subscriberUsername, channelId, channelUsername);
            subscriptionRepository.save(subscription);
            updateSubscriberCount(channelId, 1);
            return true; // Now subscribed
        }
    }

    public boolean isSubscribed(String subscriberId, String channelId) {
        return subscriptionRepository.existsBySubscriberIdAndChannelId(subscriberId, channelId);
    }

    public List<Subscription> getSubscriptions(String subscriberId) {
        return subscriptionRepository.findBySubscriberId(subscriberId);
    }

    public List<Subscription> getSubscribers(String channelId) {
        return subscriptionRepository.findByChannelId(channelId);
    }

    public long getSubscriberCount(String channelId) {
        return subscriptionRepository.countByChannelId(channelId);
    }

    private void updateSubscriberCount(String channelId, long delta) {
        Optional<User> userOpt = userRepository.findById(channelId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            Long currentCount = user.getSubscriberCount() != null ? user.getSubscriberCount() : 0L;
            long newCount = currentCount + delta;
            if (newCount < 0) {
                newCount = 0;
            }
            user.setSubscriberCount(newCount);
            userRepository.save(user);
        }
    }
}

