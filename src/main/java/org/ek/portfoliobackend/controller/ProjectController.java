package org.ek.portfoliobackend.controller;

import org.ek.portfoliobackend.dto.request.CreateProjectRequest;
import org.ek.portfoliobackend.dto.request.ImageUploadRequest;
import org.ek.portfoliobackend.dto.response.ProjectResponse;
import org.ek.portfoliobackend.service.ProjectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * REST Controller for handling project-related HTTP requests.
 * Provides endpoints for creating, retrieving, updating, and deleting projects.
 *
 * CORS is configured globally in CorsConfig.
 */
@Slf4j
@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }


    // Retrieves a project by its ID (images included)
    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getProjectById(@PathVariable Long id) {
        ProjectResponse response = projectService.getProjectById(id);
        log.info("Fetched project with ID: {}", id);
        return ResponseEntity.ok(response);
    }

    // retrieves all projects (images included)
    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getAllProjects() {
        log.info("Received request to fetch all projects");
        List<ProjectResponse> projects = projectService.getAllProjects();
        log.info("Successfully retrieved {} projects", projects.size());
        return ResponseEntity.ok(projects);
    }

    /**
     * Creates a new project with multiple images and metadata.
     *
     * Validates that:
     * - All required fields are provided
     * - At least one BEFORE and one AFTER image are included
     * - Image files are valid
     *
     * @param request Project data (title, description, serviceCategory, customerType, executionDate)
     * @param images List of image files to upload
     * @param imageMetadata Metadata for each image (imageType, isFeatured)
     * @return ResponseEntity with the created project and HTTP 201 status
     * @throws ResponseStatusException with BAD_REQUEST if validation fails
     * @throws ResponseStatusException with INTERNAL_SERVER_ERROR if storage or persistence fails
     */
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<ProjectResponse> createProject(
            @RequestPart("data") CreateProjectRequest request,
            @RequestPart("images") List<MultipartFile> images,
            @RequestPart("imageMetadata") List<ImageUploadRequest> imageMetadata) {

        log.info("Received request to create project: {}", request.getTitle());
        log.debug("Request details - Images: {}, Metadata entries: {}",
                images.size(), imageMetadata.size());

        try {
            // Validate image and metadata list sizes match
            if (images.size() != imageMetadata.size()) {
                String errorMsg = String.format(
                        "Mismatch between images (%d) and metadata (%d) count",
                        images.size(), imageMetadata.size()
                );
                log.warn(errorMsg);
                throw new IllegalArgumentException(errorMsg);
            }

            ProjectResponse createdProject = projectService.createProject(request, images, imageMetadata);

            log.info("Successfully created project with ID: {}", createdProject.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProject);

        } catch (IllegalArgumentException e) {
            // Validation errors (e.g., missing before/after images, invalid data)
            log.warn("Validation error while creating project: {}", e.getMessage());
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Validation failed: " + e.getMessage()
            );
        } catch (Exception e) {
            // Storage or other internal errors
            log.error("Failed to create project: {}", e.getMessage(), e);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to create project due to internal error. Please try again later."
            );
        }
    }
}