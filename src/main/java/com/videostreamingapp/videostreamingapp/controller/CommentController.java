package com.videostreamingapp.videostreamingapp.controller;

import com.videostreamingapp.videostreamingapp.dto.CommentRequest;
import com.videostreamingapp.videostreamingapp.dto.CommentResponse;
import com.videostreamingapp.videostreamingapp.model.Comment;
import com.videostreamingapp.videostreamingapp.service.CommentService;
import com.videostreamingapp.videostreamingapp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/comments")
@CrossOrigin(origins = "*")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    @PostMapping("/video/{videoId}")
    public ResponseEntity<?> addComment(
            @PathVariable String videoId,
            @Valid @RequestBody CommentRequest request) {
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
            Comment comment = commentService.addComment(videoId, user.getId(), user.getUsername(), request.getText());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(convertToResponse(comment));
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to add comment: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/video/{videoId}")
    public ResponseEntity<?> getComments(
            @PathVariable String videoId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Comment> comments = commentService.getCommentsByVideoId(videoId, pageable);
            Page<CommentResponse> commentResponses = comments.map(this::convertToResponse);

            Map<String, Object> response = new HashMap<>();
            response.put("comments", commentResponses.getContent());
            response.put("currentPage", commentResponses.getNumber());
            response.put("totalItems", commentResponses.getTotalElements());
            response.put("totalPages", commentResponses.getTotalPages());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to fetch comments: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<?> updateComment(
            @PathVariable String commentId,
            @Valid @RequestBody CommentRequest request) {
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
            Comment comment = commentService.updateComment(commentId, user.getId(), request.getText());
            
            return ResponseEntity.ok(convertToResponse(comment));
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to update comment: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable String commentId) {
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
            commentService.deleteComment(commentId, user.getId());
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Comment deleted successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to delete comment: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/video/{videoId}/count")
    public ResponseEntity<?> getCommentCount(@PathVariable String videoId) {
        try {
            long commentCount = commentService.getCommentCount(videoId);
            Map<String, Object> response = new HashMap<>();
            response.put("commentCount", commentCount);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get comment count: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    private CommentResponse convertToResponse(Comment comment) {
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setVideoId(comment.getVideoId());
        response.setUserId(comment.getUserId());
        response.setUsername(comment.getUsername());
        response.setText(comment.getText());
        response.setCreatedAt(comment.getCreatedAt());
        response.setUpdatedAt(comment.getUpdatedAt());
        return response;
    }
}

