package com.videostreamingapp.videostreamingapp.service;

import com.videostreamingapp.videostreamingapp.model.Comment;
import com.videostreamingapp.videostreamingapp.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    public Comment addComment(String videoId, String userId, String username, String text) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Comment text cannot be empty");
        }
        Comment comment = new Comment(videoId, userId, username, text.trim());
        return commentRepository.save(comment);
    }

    public Page<Comment> getCommentsByVideoId(String videoId, Pageable pageable) {
        return commentRepository.findByVideoIdOrderByCreatedAtDesc(videoId, pageable);
    }

    public Optional<Comment> getCommentById(String commentId) {
        return commentRepository.findById(commentId);
    }

    public Comment updateComment(String commentId, String userId, String newText) {
        Optional<Comment> commentOpt = commentRepository.findByIdAndUserId(commentId, userId);
        if (commentOpt.isEmpty()) {
            throw new RuntimeException("Comment not found or you don't have permission to edit it");
        }
        
        if (newText == null || newText.trim().isEmpty()) {
            throw new IllegalArgumentException("Comment text cannot be empty");
        }
        
        Comment comment = commentOpt.get();
        comment.setText(newText.trim());
        return commentRepository.save(comment);
    }

    public void deleteComment(String commentId, String userId) {
        Optional<Comment> commentOpt = commentRepository.findByIdAndUserId(commentId, userId);
        if (commentOpt.isEmpty()) {
            throw new RuntimeException("Comment not found or you don't have permission to delete it");
        }
        commentRepository.delete(commentOpt.get());
    }

    public long getCommentCount(String videoId) {
        return commentRepository.countByVideoId(videoId);
    }
}

