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
 * Implementering af ImageStorageService der gemmer filer lokalt på filsystemet.
 * Filer gemmes med UUID-baserede unikke filnavne for at undgå konflikter.
 * Upload-mappen konfigureres via file.upload-dir property i application.properties.
 */
@Service
public class LocalFileStorageServiceImpl implements ImageStorageService {

    /**
     * Den absolutte sti til upload-mappen hvor filer gemmes.
     */
    private final Path uploadPath;

    /**
     * Opretter en ny LocalFileStorageServiceImpl med den angivne upload-mappe.
     * Mappen oprettes automatisk hvis den ikke eksisterer.
     *
     * @param uploadDir stien til upload-mappen (standard: ./uploads)
     * @throws RuntimeException hvis upload-mappen ikke kan oprettes
     */
    public LocalFileStorageServiceImpl(@Value("${file.upload-dir:./uploads}") String uploadDir) {
        this.uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadPath);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory: " + uploadDir, e);
        }
    }

    /**
     * Gemmer en uploaded fil til filsystemet med et unikt UUID-baseret filnavn.
     * Filens originale extension bevares.
     *
     * @param file filen der skal gemmes (må ikke være tom)
     * @return den relative URL til den gemte fil (fx "/uploads/uuid.jpg")
     * @throws IllegalArgumentException hvis filen er tom
     * @throws SecurityException hvis der forsøges at gemme en fil udenfor upload-mappen
     * @throws RuntimeException hvis filen ikke kan gemmes
     */
    @Override
    public String store(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Cannot store empty file");
        }

        try {
            // Generér unikt filnavn for at undgå konflikter
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : "";
            String filename = UUID.randomUUID() + extension;

            // Bestem destinationen for filen
            Path destinationFile = this.uploadPath.resolve(filename).normalize();

            // Sikkerhedstjek: Sørg for at filen gemmes indenfor upload-mappen
            if (!destinationFile.getParent().equals(this.uploadPath)) {
                throw new SecurityException("Cannot store file outside upload directory");
            }

            // Kopiér fil til destination
            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);

            // Returnér den relative URL/sti
            return "/uploads/" + filename;

        } catch (IOException e) {
            throw new RuntimeException("Failed to store file: " + file.getOriginalFilename(), e);
        }
    }

    /**
     * Sletter en fil baseret på dens URL.
     * Håndterer gracefully hvis URL er null, tom, eller filen ikke eksisterer.
     *
     * @param url URL'en til filen der skal slettes (fx "/uploads/uuid.jpg")
     * @throws SecurityException hvis der forsøges at slette en fil udenfor upload-mappen
     * @throws RuntimeException hvis sletningen fejler
     */
    @Override
    public void delete(String url) {
        // Hvis url er null eller tom → gør ingenting
        if (url == null || url.isBlank()) {
            return;
        }

        // Udtræk filnavn fra URL'en
        int lastSlash = url.lastIndexOf('/');
        String filename = (lastSlash >= 0) ? url.substring(lastSlash + 1) : url;

        // Hvis filnavn er tomt → gør ingenting
        if (filename == null || filename.isBlank()) {
            return;
        }

        // Byg den fulde sti inde i upload-mappen
        Path filePath = uploadPath.resolve(filename).normalize();

        // Sikkerhedstjek: Må ikke slette udenfor uploadPath
        if (!filePath.startsWith(uploadPath)) {
            throw new SecurityException("Cannot delete file outside upload directory");
        }

        try {
            // Slet filen hvis den findes
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file: " + url, e);
        }
    }

    /**
     * Returnerer stien til upload-mappen.
     * Nyttig til testing og konfigurationsvalidering.
     *
     * @return den absolutte sti til upload-mappen
     */
    public Path getUploadPath() {
        return uploadPath;
    }
}