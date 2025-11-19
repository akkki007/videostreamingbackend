package com.videostreamingapp.videostreamingapp.repository;

import com.videostreamingapp.videostreamingapp.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {
    Page<Comment> findByVideoIdOrderByCreatedAtDesc(String videoId, Pageable pageable);
    List<Comment> findByVideoId(String videoId);
    Optional<Comment> findByIdAndUserId(String id, String userId);
    long countByVideoId(String videoId);
}

