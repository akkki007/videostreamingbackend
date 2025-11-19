package com.videostreamingapp.videostreamingapp.controller;

import com.videostreamingapp.videostreamingapp.service.LikeService;
import com.videostreamingapp.videostreamingapp.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/likes")
@CrossOrigin(origins = "*")
public class LikeController {

    @Autowired
    private LikeService likeService;

    @Autowired
    private UserService userService;

    @PostMapping("/video/{videoId}")
    public ResponseEntity<?> toggleLike(@PathVariable String videoId, HttpServletRequest request) {
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
            boolean isLiked = likeService.toggleLike(videoId, user.getId(), user.getUsername());

            Map<String, Object> response = new HashMap<>();
            response.put("isLiked", isLiked);
            response.put("likeCount", likeService.getLikeCount(videoId));
            response.put("message", isLiked ? "Video liked" : "Video unliked");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to toggle like: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/video/{videoId}/status")
    public ResponseEntity<?> getLikeStatus(@PathVariable String videoId, HttpServletRequest request) {
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
            boolean isLiked = likeService.isLiked(videoId, user.getId());
            long likeCount = likeService.getLikeCount(videoId);

            Map<String, Object> response = new HashMap<>();
            response.put("isLiked", isLiked);
            response.put("likeCount", likeCount);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get like status: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/video/{videoId}/count")
    public ResponseEntity<?> getLikeCount(@PathVariable String videoId) {
        try {
            long likeCount = likeService.getLikeCount(videoId);
            Map<String, Object> response = new HashMap<>();
            response.put("likeCount", likeCount);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to get like count: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}

