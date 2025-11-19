package com.videostreamingapp.videostreamingapp.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document("subscriptions")
public class Subscription {
    @Id
    private String id;
    private String subscriberId; // User who is subscribing
    private String subscriberUsername;
    private String channelId; // User/channel being subscribed to
    private String channelUsername;
    private Date subscribedAt;

    public Subscription() {
        this.subscribedAt = new Date();
    }

    public Subscription(String subscriberId, String subscriberUsername, String channelId, String channelUsername) {
        this.subscriberId = subscriberId;
        this.subscriberUsername = subscriberUsername;
        this.channelId = channelId;
        this.channelUsername = channelUsername;
        this.subscribedAt = new Date();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSubscriberId() {
        return subscriberId;
    }

    public void setSubscriberId(String subscriberId) {
        this.subscriberId = subscriberId;
    }

    public String getSubscriberUsername() {
        return subscriberUsername;
    }

    public void setSubscriberUsername(String subscriberUsername) {
        this.subscriberUsername = subscriberUsername;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getChannelUsername() {
        return channelUsername;
    }

    public void setChannelUsername(String channelUsername) {
        this.channelUsername = channelUsername;
    }

    public Date getSubscribedAt() {
        return subscribedAt;
    }

    public void setSubscribedAt(Date subscribedAt) {
        this.subscribedAt = subscribedAt;
    }
}

