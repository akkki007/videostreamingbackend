package com.videostreamingapp.videostreamingapp.repository;

import com.videostreamingapp.videostreamingapp.model.Subscription;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends MongoRepository<Subscription, String> {
    Optional<Subscription> findBySubscriberIdAndChannelId(String subscriberId, String channelId);
    boolean existsBySubscriberIdAndChannelId(String subscriberId, String channelId);
    List<Subscription> findBySubscriberId(String subscriberId);
    List<Subscription> findByChannelId(String channelId);
    long countByChannelId(String channelId);
}

