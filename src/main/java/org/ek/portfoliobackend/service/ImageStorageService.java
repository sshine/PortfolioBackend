package org.ek.portfoliobackend.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Service interface for handling file storage operations.
 * Abstracts the underlying storage implementation (local filesystem, cloud storage, etc.)
 */
public interface ImageStorageService {

    /**
     * Store an uploaded file and return its URL
     *
     * @param file the file to store
     * @return the URL/path where the file was stored
     * @throws RuntimeException if the file cannot be stored
     */
    String store(MultipartFile file);

    /**
     * Delete a file by its URL/path
     *
     * @param url the URL/path of the file to delete
     * @throws RuntimeException if the file cannot be deleted
     */
    void delete(String url);
}