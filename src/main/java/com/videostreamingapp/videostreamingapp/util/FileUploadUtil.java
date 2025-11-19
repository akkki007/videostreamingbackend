package com.videostreamingapp.videostreamingapp.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

public class FileUploadUtil {

    /**
     * Uploads a file to the specified directory
     * 
     * @param file The file to upload
     * @param uploadDir The directory where the file should be saved
     * @return The generated filename (UUID + extension)
     * @throws IOException If file operations fail
     */
    public static String uploadFile(MultipartFile file, String uploadDir) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

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

        return filename;
    }

    /**
     * Validates if the file is an image
     * 
     * @param file The file to validate
     * @return true if the file is an image, false otherwise
     */
    public static boolean isImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }

        String contentType = file.getContentType();
        if (contentType == null) {
            return false;
        }

        return contentType.startsWith("image/");
    }

    /**
     * Validates if the file is a video
     * 
     * @param file The file to validate
     * @return true if the file is a video, false otherwise
     */
    public static boolean isVideoFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }

        String contentType = file.getContentType();
        if (contentType == null) {
            return false;
        }

        return contentType.startsWith("video/");
    }

    /**
     * Gets the file path for a given filename in the upload directory
     * 
     * @param filename The filename
     * @param uploadDir The upload directory
     * @return The Path to the file
     */
    public static Path getFilePath(String filename, String uploadDir) {
        return Paths.get(uploadDir).resolve(filename);
    }

    /**
     * Checks if a file exists in the upload directory
     * 
     * @param filename The filename
     * @param uploadDir The upload directory
     * @return true if the file exists, false otherwise
     */
    public static boolean fileExists(String filename, String uploadDir) {
        if (filename == null || filename.isEmpty()) {
            return false;
        }
        Path filePath = Paths.get(uploadDir).resolve(filename);
        return Files.exists(filePath);
    }

    /**
     * Deletes a file from the upload directory
     * 
     * @param filename The filename to delete
     * @param uploadDir The upload directory
     * @return true if the file was deleted, false otherwise
     */
    public static boolean deleteFile(String filename, String uploadDir) {
        if (filename == null || filename.isEmpty()) {
            return false;
        }
        try {
            Path filePath = Paths.get(uploadDir).resolve(filename);
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                return true;
            }
        } catch (IOException e) {
            // Log error if needed
            return false;
        }
        return false;
    }
}

