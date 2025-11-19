package com.videostreamingapp.videostreamingapp.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document("videos")
public class Video {
    @Id
    private String id;
    private String title;
    private String description;
    private String videoUrl; // Path to video file on filesystem
    private String thumbnailUrl; // Path to thumbnail image on filesystem
    private String uploaderId;
    private String uploaderUsername;
    private Date uploadDate;
    private Long fileSize;
    private String contentType;
    private Long viewCount;
    private Long likeCount;

    public Video() {
        this.uploadDate = new Date();
        this.viewCount = 0L;
        this.likeCount = 0L;
    }

    public Video(String title, String description, String videoUrl, String uploaderId, String uploaderUsername) {
        this.title = title;
        this.description = description;
        this.videoUrl = videoUrl;
        this.uploaderId = uploaderId;
        this.uploaderUsername = uploaderUsername;
        this.uploadDate = new Date();
        this.viewCount = 0L;
        this.likeCount = 0L;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getUploaderId() {
        return uploaderId;
    }

    public void setUploaderId(String uploaderId) {
        this.uploaderId = uploaderId;
    }

    public String getUploaderUsername() {
        return uploaderUsername;
    }

    public void setUploaderUsername(String uploaderUsername) {
        this.uploaderUsername = uploaderUsername;
    }

    public Date getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public Long getViewCount() {
        return viewCount;
    }

    public void setViewCount(Long viewCount) {
        this.viewCount = viewCount;
    }

    public Long getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Long likeCount) {
        this.likeCount = likeCount;
    }
}

