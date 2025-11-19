package com.videostreamingapp.videostreamingapp.service;

import com.videostreamingapp.videostreamingapp.model.Video;
import com.videostreamingapp.videostreamingapp.repository.VideoRepository;
import com.videostreamingapp.videostreamingapp.util.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Service
public class VideoService {

    @Autowired
    private VideoRepository videoRepository;

    @Value("${video.upload.dir}")
    private String uploadDir;

    @Value("${thumbnail.upload.dir}")
    private String thumbnailDir;

    public Video uploadVideo(MultipartFile file, MultipartFile thumbnail, String title, String description, String uploaderId, String uploaderUsername) throws IOException {
        // Upload video file
        String videoFilename = FileUploadUtil.uploadFile(file, uploadDir);
        if (videoFilename == null) {
            throw new IOException("Failed to upload video file");
        }

        // Upload thumbnail file if provided
        String thumbnailFilename = null;
        if (thumbnail != null && !thumbnail.isEmpty()) {
            // Validate that thumbnail is an image
            if (!FileUploadUtil.isImageFile(thumbnail)) {
                throw new IllegalArgumentException("Thumbnail must be an image file");
            }
            thumbnailFilename = FileUploadUtil.uploadFile(thumbnail, thumbnailDir);
        }

        // Create video entity
        Video video = new Video();
        video.setTitle(title);
        video.setDescription(description);
        video.setVideoUrl("/api/videos/stream/" + videoFilename);
        if (thumbnailFilename != null) {
            video.setThumbnailUrl("/api/videos/thumbnail/" + thumbnailFilename);
        }
        video.setUploaderId(uploaderId);
        video.setUploaderUsername(uploaderUsername);
        video.setFileSize(file.getSize());
        video.setContentType(file.getContentType());

        return videoRepository.save(video);
    }

    public Page<Video> getAllVideos(Pageable pageable) {
        return videoRepository.findAll(pageable);
    }

    public Page<Video> searchVideos(String query, Pageable pageable) {
        return videoRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                query, query, pageable);
    }

    public Optional<Video> getVideoById(String id) {
        return videoRepository.findById(id);
    }

    public Optional<Video> getVideoByFilename(String filename) {
        return videoRepository.findAll().stream()
                .filter(v -> v.getVideoUrl().contains(filename))
                .findFirst();
    }

    public Path getVideoPath(String filename) {
        return Paths.get(uploadDir).resolve(filename);
    }

    public boolean videoFileExists(String filename) {
        Path filePath = Paths.get(uploadDir).resolve(filename);
        return Files.exists(filePath);
    }

    public Path getThumbnailPath(String filename) {
        return Paths.get(thumbnailDir).resolve(filename);
    }

    public boolean thumbnailFileExists(String filename) {
        Path filePath = Paths.get(thumbnailDir).resolve(filename);
        return Files.exists(filePath);
    }
}

