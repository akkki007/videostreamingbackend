package com.videostreamingapp.videostreamingapp.controller;

import com.videostreamingapp.videostreamingapp.dto.VideoResponse;
import com.videostreamingapp.videostreamingapp.model.Video;
import com.videostreamingapp.videostreamingapp.service.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/videos")
@CrossOrigin(origins = "*")
public class VideoController {

    @Autowired
    private VideoService videoService;

    @Autowired
    private UserService userService;

    @Autowired
    private ViewService viewService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private CommentService commentService;

    @GetMapping
    public ResponseEntity<?> getAllVideos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Video> videos;

            if (search != null && !search.trim().isEmpty()) {
                videos = videoService.searchVideos(search, pageable);
            } else {
                videos = videoService.getAllVideos(pageable);
            }

            Page<VideoResponse> videoResponses = videos.map(video -> convertToResponse(video, null));
            
            Map<String, Object> response = new HashMap<>();
            response.put("videos", videoResponses.getContent());
            response.put("currentPage", videoResponses.getNumber());
            response.put("totalItems", videoResponses.getTotalElements());
            response.put("totalPages", videoResponses.getTotalPages());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to fetch videos: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getVideoById(@PathVariable String id) {
        try {
            Optional<Video> videoOpt = videoService.getVideoById(id);
            if (videoOpt.isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Video not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
            
            // Increment view count when video is viewed
            viewService.incrementViewCount(id);
            
            // Refresh video to get updated view count
            videoOpt = videoService.getVideoById(id);
            return ResponseEntity.ok(convertToResponse(videoOpt.get(), null));
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to fetch video: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/{id}/view")
    public ResponseEntity<?> incrementView(@PathVariable String id) {
        try {
            viewService.incrementViewCount(id);
            Optional<Video> videoOpt = videoService.getVideoById(id);
            if (videoOpt.isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Video not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("viewCount", videoOpt.get().getViewCount());
            response.put("message", "View count incremented");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to increment view: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadVideo(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false, defaultValue = "") String description,
            @RequestParam(value = "thumbnail", required = false) MultipartFile thumbnail,
            HttpServletRequest request) {
        try {
            // Get user from authentication
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

            if (file.isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "File is empty");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            Video video = videoService.uploadVideo(
                    file, thumbnail, title, description, user.getId(), user.getUsername());

            return ResponseEntity.status(HttpStatus.CREATED).body(convertToResponse(video, null));
        } catch (IOException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to upload video: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Upload failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping("/stream/{filename}")
    public ResponseEntity<byte[]> streamVideo(
            @PathVariable String filename,
            @RequestHeader(value = "Range", required = false) String rangeHeader) {
        try {
            if (!videoService.videoFileExists(filename)) {
                return ResponseEntity.notFound().build();
            }

            Path videoPath = videoService.getVideoPath(filename);
            byte[] videoData = Files.readAllBytes(videoPath);
            long fileSize = videoData.length;

            // Detect content type from file
            String contentType = "video/mp4"; // default
            try {
                contentType = Files.probeContentType(videoPath);
                if (contentType == null || !contentType.startsWith("video/")) {
                    contentType = "video/mp4";
                }
            } catch (IOException e) {
                // Use default if detection fails
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));
            headers.setContentLength(fileSize);
            headers.set("Accept-Ranges", "bytes");

            // Support for range requests (for video seeking)
            if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
                String[] ranges = rangeHeader.substring(6).split("-");
                long start = Long.parseLong(ranges[0]);
                long end = ranges.length > 1 && !ranges[1].isEmpty() 
                    ? Long.parseLong(ranges[1]) 
                    : fileSize - 1;

                if (end >= fileSize) {
                    end = fileSize - 1;
                }

                long contentLength = end - start + 1;
                byte[] partialData = new byte[(int) contentLength];
                System.arraycopy(videoData, (int) start, partialData, 0, (int) contentLength);

                headers.setContentLength(contentLength);
                headers.set("Content-Range", String.format("bytes %d-%d/%d", start, end, fileSize));
                headers.set(HttpHeaders.ACCEPT_RANGES, "bytes");

                return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                        .headers(headers)
                        .body(partialData);
            }

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(videoData);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/thumbnail/{filename}")
    public ResponseEntity<byte[]> getThumbnail(@PathVariable String filename) {
        try {
            if (!videoService.thumbnailFileExists(filename)) {
                return ResponseEntity.notFound().build();
            }

            Path thumbnailPath = videoService.getThumbnailPath(filename);
            byte[] thumbnailData = Files.readAllBytes(thumbnailPath);

            // Detect content type from file
            String contentType = "image/jpeg"; // default
            try {
                contentType = Files.probeContentType(thumbnailPath);
                if (contentType == null || !contentType.startsWith("image/")) {
                    contentType = "image/jpeg";
                }
            } catch (IOException e) {
                // Use default if detection fails
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));
            headers.setContentLength(thumbnailData.length);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(thumbnailData);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private VideoResponse convertToResponse(Video video, String currentUserId) {
        VideoResponse response = new VideoResponse();
        response.setId(video.getId());
        response.setTitle(video.getTitle());
        response.setDescription(video.getDescription());
        response.setVideoUrl(video.getVideoUrl());
        response.setThumbnailUrl(video.getThumbnailUrl());
        response.setUploaderId(video.getUploaderId());
        response.setUploaderUsername(video.getUploaderUsername());
        response.setUploadDate(video.getUploadDate());
        response.setFileSize(video.getFileSize());
        response.setContentType(video.getContentType());
        
        // Set view count and like count
        response.setViewCount(video.getViewCount() != null ? video.getViewCount() : 0L);
        response.setLikeCount(video.getLikeCount() != null ? video.getLikeCount() : 0L);
        
        // Set comment count
        response.setCommentCount(commentService.getCommentCount(video.getId()));
        
        // Set isLiked status if user is authenticated
        if (currentUserId != null) {
            response.setIsLiked(likeService.isLiked(video.getId(), currentUserId));
        } else {
            // Try to get current user from security context
            try {
                Authentication authentication = org.springframework.security.core.context.SecurityContextHolder
                        .getContext().getAuthentication();
                if (authentication != null && authentication.isAuthenticated() && 
                    !authentication.getName().equals("anonymousUser")) {
                    Optional<com.videostreamingapp.videostreamingapp.model.User> userOpt = 
                            userService.findByUsername(authentication.getName());
                    if (userOpt.isPresent()) {
                        response.setIsLiked(likeService.isLiked(video.getId(), userOpt.get().getId()));
                    } else {
                        response.setIsLiked(false);
                    }
                } else {
                    response.setIsLiked(false);
                }
            } catch (Exception e) {
                response.setIsLiked(false);
            }
        }
        
        return response;
    }
}
