package com.videostreamingapp.videostreamingapp.service;

import com.videostreamingapp.videostreamingapp.model.Video;
import com.videostreamingapp.videostreamingapp.repository.VideoRepository;
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
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.UUID;

@Service
public class VideoService {

    @Autowired
    private VideoRepository videoRepository;

    @Value("${video.upload.dir}")
    private String uploadDir;

    public Video uploadVideo(MultipartFile file, String title, String description, String uploaderId, String uploaderUsername) throws IOException {
        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".") 
            ? originalFilename.substring(originalFilename.lastIndexOf(".")) 
            : "";
        String filename = UUID.randomUUID().toString() + extension;
        Path filePath = uploadPath.resolve(filename);

        // Save file
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Create video entity
        Video video = new Video();
        video.setTitle(title);
        video.setDescription(description);
        video.setVideoUrl("/api/videos/stream/" + filename);
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
}

