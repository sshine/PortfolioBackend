package org.ek.portfoliobackend.service.impl;

import org.ek.portfoliobackend.dto.request.CreateProjectRequest;
import org.ek.portfoliobackend.dto.request.ImageUploadRequest;
import org.ek.portfoliobackend.dto.request.UpdateProjectRequest;
import org.ek.portfoliobackend.dto.response.ProjectResponse;
import org.ek.portfoliobackend.mapper.ProjectMapper;
import org.ek.portfoliobackend.model.CustomerType;
import org.ek.portfoliobackend.model.Image;
import org.ek.portfoliobackend.model.ImageType;
import org.ek.portfoliobackend.model.Project;
import org.ek.portfoliobackend.model.WorkType;
import org.ek.portfoliobackend.repository.ImageRepository;
import org.ek.portfoliobackend.repository.ProjectRepository;
import org.ek.portfoliobackend.service.ImageStorageService;
import org.ek.portfoliobackend.service.ProjectService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ImageRepository imageRepository;
    private final ImageStorageService imageStorageService;
    private final ProjectMapper projectMapper;

    public ProjectServiceImpl(ProjectRepository projectRepository,
                              ImageRepository imageRepository,
                              ImageStorageService imageStorageService,
                              ProjectMapper projectMapper) {
        this.projectRepository = projectRepository;
        this.imageRepository = imageRepository;
        this.imageStorageService = imageStorageService;
        this.projectMapper = projectMapper;
    }

    @Override
    @Transactional
    public ProjectResponse createProject(CreateProjectRequest request,
                                         List<MultipartFile> images,
                                         List<ImageUploadRequest> imageMetadata) {

        // Validate input parameters
        validateInputs(images, imageMetadata);

        // Validate that at least one BEFORE and one AFTER image is provided
        validateImageTypes(imageMetadata);

        // Create project entity from request
        Project project = projectMapper.toProjectEntity(request);

        // Save project first to get the ID for image references
        project = projectRepository.save(project);

        // Process and store images
        List<Image> savedImages = new ArrayList<>();
        try {
            for (int i = 0; i < images.size(); i++) {
                MultipartFile imageFile = images.get(i);
                ImageUploadRequest metadata = imageMetadata.get(i);

                // Store the image file and get the URL
                String imageUrl = imageStorageService.store(imageFile);

                // Create image entity
                Image image = projectMapper.toImage(
                        imageUrl,
                        metadata.getImageType(),
                        metadata.isFeatured(),
                        project
                );

                // Save image entity
                Image savedImage = imageRepository.save(image);
                savedImages.add(savedImage);
            }

            // Add images to project
            project.setImages(savedImages);

            // Convert to response DTO
            return projectMapper.toResponse(project);

        } catch (Exception e) {
            // If any image storage fails, clean up already stored images
            for (Image savedImage : savedImages) {
                try {
                    imageStorageService.delete(savedImage.getUrl());
                } catch (Exception cleanupException) {
                    // Log cleanup failure but don't throw
                }
            }
            throw new RuntimeException("Failed to store images: " + e.getMessage(), e);
        }
    }

    @Override
    public ProjectResponse updateProject(Long id, UpdateProjectRequest request) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public ProjectResponse getProjectById(Long id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<ProjectResponse> getAllProjects() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void deleteProject(Long id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<ProjectResponse> getProjectsByServiceCategory(WorkType workType) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<ProjectResponse> getProjectsByCustomerType(CustomerType customerType) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<ProjectResponse> getProjectsByFilters(WorkType workType, CustomerType customerType) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<ProjectResponse> getProjectsByDateRange(LocalDate startDate, LocalDate endDate) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<ProjectResponse> getAllProjectsOrderedByDate() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Validate that images and metadata lists are not null and have matching sizes
     */
    private void validateInputs(List<MultipartFile> images, List<ImageUploadRequest> imageMetadata) {
        if (images == null || images.isEmpty()) {
            throw new IllegalArgumentException("At least one image must be provided");
        }

        if (imageMetadata == null || imageMetadata.isEmpty()) {
            throw new IllegalArgumentException("Image metadata must be provided");
        }

        if (images.size() != imageMetadata.size()) {
            throw new IllegalArgumentException("Number of images must match number of metadata entries");
        }
    }

    /**
     * Validate that at least one BEFORE and one AFTER image is included
     */
    private void validateImageTypes(List<ImageUploadRequest> imageMetadata) {
        boolean hasBeforeImage = false;
        boolean hasAfterImage = false;

        for (ImageUploadRequest metadata : imageMetadata) {
            if (metadata.getImageType() == ImageType.BEFORE) {
                hasBeforeImage = true;
            } else if (metadata.getImageType() == ImageType.AFTER) {
                hasAfterImage = true;
            }

            // Early exit if both types are found
            if (hasBeforeImage && hasAfterImage) {
                return;
            }
        }

        if (!hasBeforeImage) {
            throw new IllegalArgumentException("At least one BEFORE image must be provided");
        }

        if (!hasAfterImage) {
            throw new IllegalArgumentException("At least one AFTER image must be provided");
        }
    }
}