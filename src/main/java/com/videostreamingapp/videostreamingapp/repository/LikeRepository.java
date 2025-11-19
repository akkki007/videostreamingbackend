package com.videostreamingapp.videostreamingapp.repository;

import com.videostreamingapp.videostreamingapp.model.Like;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends MongoRepository<Like, String> {
    Optional<Like> findByVideoIdAndUserId(String videoId, String userId);
    boolean existsByVideoIdAndUserId(String videoId, String userId);
    long countByVideoId(String videoId);
}

