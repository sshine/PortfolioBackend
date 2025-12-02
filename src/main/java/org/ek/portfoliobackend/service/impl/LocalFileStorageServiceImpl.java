package org.ek.portfoliobackend.service.impl;

import org.ek.portfoliobackend.service.ImageStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * Local filesystem implementation of ImageStorageService.
 * Stores uploaded images in a local directory.
 */
@Service
public class LocalFileStorageServiceImpl implements ImageStorageService {

    private final Path uploadPath;

    public LocalFileStorageServiceImpl(@Value("${file.upload-dir:./uploads}") String uploadDir) {
        this.uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadPath);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory: " + uploadDir, e);
        }
    }

    @Override
    public String store(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Cannot store empty file");
        }

        try {
            // Generate unique filename to avoid conflicts
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : "";
            String filename = UUID.randomUUID() + extension;

            // Resolve the file path
            Path destinationFile = this.uploadPath.resolve(filename).normalize();

            // Security check: ensure the file is stored within the upload directory
            if (!destinationFile.getParent().equals(this.uploadPath)) {
                throw new SecurityException("Cannot store file outside upload directory");
            }

            // Copy file to destination
            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);

            // Return the relative URL/path
            return "/uploads/" + filename;

        } catch (IOException e) {
            throw new RuntimeException("Failed to store file: " + file.getOriginalFilename(), e);
        }
    }

    @Override
    public void delete(String url) {
        try {
            // Extract filename from URL (e.g., "/uploads/abc-123.jpg" -> "abc-123.jpg")
            String filename = url.substring(url.lastIndexOf("/") + 1);
            Path filePath = this.uploadPath.resolve(filename).normalize();

            // Security check: ensure the file is within the upload directory
            if (!filePath.getParent().equals(this.uploadPath)) {
                throw new SecurityException("Cannot delete file outside upload directory");
            }

            // Delete the file if it exists
            Files.deleteIfExists(filePath);

        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file: " + url, e);
        }
    }

    /**
     * Get the upload directory path (useful for testing or configuration)
     */
    public Path getUploadPath() {
        return uploadPath;
    }
}