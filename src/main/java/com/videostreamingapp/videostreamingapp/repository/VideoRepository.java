package com.videostreamingapp.videostreamingapp.repository;

import com.videostreamingapp.videostreamingapp.model.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoRepository extends MongoRepository<Video, String> {
    Page<Video> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String title, String description, Pageable pageable);
    Page<Video> findByUploaderId(String uploaderId, Pageable pageable);
}

