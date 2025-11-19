package com.videostreamingapp.videostreamingapp.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document("likes")
public class Like {
    @Id
    private String id;
    private String videoId;
    private String userId;
    private String username;
    private Date likedAt;

    public Like() {
        this.likedAt = new Date();
    }

    public Like(String videoId, String userId, String username) {
        this.videoId = videoId;
        this.userId = userId;
        this.username = username;
        this.likedAt = new Date();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getLikedAt() {
        return likedAt;
    }

    public void setLikedAt(Date likedAt) {
        this.likedAt = likedAt;
    }
}

