package org.ek.portfoliobackend.controller;

import jakarta.validation.Valid;
import org.ek.portfoliobackend.dto.request.CreateProjectRequest;
import org.ek.portfoliobackend.dto.request.ImageUploadRequest;
import org.ek.portfoliobackend.dto.request.UpdateImageRequest;
import org.ek.portfoliobackend.dto.request.UpdateProjectRequest;
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

    /**
     * Uploads new images to existing project.
     * new images are added to project's existing images.
     * validates that project maintains at least one BEFORE and one AFTER image.
     *
     * @param id Project ID
     * @param images List of image files to upload
     * @param imageMetadata Metadata for each image (imageType, isFeatured)
     * @return ResponseEntity with the updated project and HTTP 200 status
     * * @throws ResourceNotFoundException if project with given ID does not exist
     * @throws IllegalArgumentException with BAD_REQUEST if validation fails
     * */
    @PatchMapping(value = "/{id}/images", consumes = "multipart/form-data")
    public ResponseEntity<ProjectResponse> uploadProjectImages(
            @PathVariable Long id,
            @RequestPart("images") List<MultipartFile> images,
            @RequestPart("imageMetadata") List<ImageUploadRequest> imageMetadata) {
        log.info("Received request to upload images for project ID: {}", id);

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

            ProjectResponse updatedProject = projectService.addImagesToProject(id, images, imageMetadata);

            log.info("Successfully uploaded {} images to project ID: {}", images.size(), id);
            return ResponseEntity.ok(updatedProject);

        } catch (IllegalArgumentException e) {
            // Validation errors (missing before/after images, invalid data)
            log.warn("Validation error while uploading images to project ID {}: {}", id, e.getMessage());
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Validation failed: " + e.getMessage()
            );
        } catch (Exception e) {
            log.error("Failed to upload images to project ID {}: {}", id, e.getMessage(), e);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to upload images due to internal error. Please try again later."
            );
        }
    }

    /**
     * Updates metadata of an existing image within a project.
     * only updates the fields provided in the request (imageType, isFeatured).
     *
     * @param projectId Project ID
     * @param imageId Image ID of image to update
     * @param request Metadata fields to update (imageType, isFeatured)
     * @return ResponseEntity with the updated project and HTTP 200 status
     * @throws ResourceNotFoundException if project or image not found
     * */
    @PatchMapping("/{projectId}/images/{imageId}")
    public ResponseEntity<ProjectResponse> updateImageMetadata(@PathVariable Long projectId,
                                                       @PathVariable Long imageId,
                                                       @Valid @RequestBody UpdateImageRequest request) {
        log.info("Received request to update image metadata for image ID: {} in project ID: {}", imageId, projectId);

        ProjectResponse updatedProject = projectService.updateImageMetadata(projectId, imageId, request);

        log.info("Successfully updated image metadata");
        return ResponseEntity.ok(updatedProject);
    }

    // Updates an existing project's details
    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponse> updateProject(@PathVariable Long id,
                                                        @Valid @RequestBody UpdateProjectRequest request) {
        log.info("Received request to update project with ID: {}", id);
        log.debug("Update details - Title: {}, Description: {}", request.getTitle(), request.getDescription());

        ProjectResponse updatedProject = projectService.updateProject(id, request);

        log.info("Successfully updated project with ID: {}", id);
        return ResponseEntity.ok(updatedProject);
    }

    // Deletes an image from a project by image ID
    @DeleteMapping("/{projectId}/images/{imageId}")
    public ResponseEntity<ProjectResponse> deleteImage(@PathVariable Long projectId,
                                                   @PathVariable Long imageId) {
        log.info("Received request to delete image ID: {} from project ID: {}", imageId, projectId);

        try {
            ProjectResponse updatedProject = projectService.deleteImageFromProject(projectId, imageId);
            log.info("Successfully deleted image ID: {} from project ID: {}", imageId, projectId);
            return ResponseEntity.ok(updatedProject);

        } catch (IllegalArgumentException e) {
            log.warn("Validation error while deleting image ID {} from project ID {}: {}", imageId, projectId, e.getMessage());
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Validation failed: " + e.getMessage()
            );
        } catch (Exception e) {
            log.error("Failed to delete image ID {} from project ID {}: {}", imageId, projectId, e.getMessage(), e);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to delete image due to internal error. Please try again later."
            );
        }
    }

    // Deletes project and all associated images
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        log.info("Received request to delete project with ID: {}", id);

        projectService.deleteProject(id);

        log.info("Successfully deleted project with ID: {}", id);
        return ResponseEntity.noContent().build();
    }
}