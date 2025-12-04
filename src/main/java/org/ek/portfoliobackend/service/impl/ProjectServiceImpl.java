package org.ek.portfoliobackend.service.impl;

import org.ek.portfoliobackend.dto.request.CreateProjectRequest;
import org.ek.portfoliobackend.dto.request.ImageUploadRequest;
import org.ek.portfoliobackend.dto.request.UpdateImageRequest;
import org.ek.portfoliobackend.dto.request.UpdateProjectRequest;
import org.ek.portfoliobackend.dto.response.ImageResponse;
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
import org.hibernate.annotations.NotFound;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
import org.ek.portfoliobackend.exception.custom.ResourceNotFoundException;
import org.springframework.data.domain.Sort;
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

    // Update project
    @Override
    @Transactional
    public ProjectResponse updateProject(Long id, UpdateProjectRequest request) {

        Project project = findProjectById(id);

        // Update project fields with mapper
        projectMapper.updateProjectEntity(request, project);

        // Save updated project
        Project updatedProject = projectRepository.save(project);

        // return response DTO
        return projectMapper.toResponse(updatedProject);
    }

//    // Update image
//    @Override
//    @Transactional
//    public ImageResponse updateImage(Long imageId, UpdateImageRequest request) {
//
//        Image image = findImageById(imageId);
//
//        deleteImageUrl(image, request);
//
//        // Updates the image metadata
//        projectMapper.updateImageEntity(request, image);
//
//        // Saves new image
//        imageRepository.save(image);
//
//        return projectMapper.toImageResponse(image);
//
//    }

    @Override
    public ProjectResponse getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", id));
        return projectMapper.toResponse(project);
    }

    @Override
    public List<ProjectResponse> getAllProjects() {
        List<Project> projects = projectRepository.findAll();
        return projects.stream()
                .map(projectMapper::toResponse)
                .toList();
    }
    @Override
    @Transactional
    public ProjectResponse addImagesToProject(Long projectId,
                                              List<MultipartFile> images,
                                              List<ImageUploadRequest> imageMetadata) {
        // Validate inputs
        validateInputs(images, imageMetadata);
        // Find existing project
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", projectId));

        // store and create new image entities
        List<Image> newImages = new ArrayList<>();
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
                newImages.add(savedImage);
            }

            // Add new images to project
            project.getImages().addAll(newImages);

            // convert to response DTO
            return projectMapper.toResponse(project);
        } catch (Exception e) {
            // Cleanup stored images on failure
            for (Image savedImage : newImages) {
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
    @Transactional
    public ProjectResponse updateImageMetadata(Long projectId, Long imageId, UpdateImageRequest request) {
        // Verify project exists
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", projectId));

        // Find the image within the project
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Image", imageId));

        // Verify image belongs to this project
        if (!image.getProject().getId().equals(projectId)) {
            throw new IllegalArgumentException("Image does not belong to the specified project");
        }

        // Update image metadata using mapper
        projectMapper.updateImageEntity(request, image);

        // Save updated image
        imageRepository.save(image);

        // Return updated project
        return projectMapper.toResponse(project);
    }

    @Override
    @Transactional
    public ProjectResponse deleteImageFromProject(Long projectId, Long imageId) {
        // Verify project exists
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", projectId));

        // find the image
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Image", imageId));

        // Verify image belongs to this project
        if (!image.getProject().getId().equals(projectId)) {
            throw new IllegalArgumentException("Image does not belong to the specified project");
        }

        validateImageDeletion(project, image);

        // delete physical file from storage
        try {
            imageStorageService.delete(image.getUrl());
        } catch (Exception e) {

        }

        // remove image from project and delete from db
        project.getImages().remove(image);
        imageRepository.delete(image);

        return projectMapper.toResponse(project);
    }

    @Override
    @Transactional
    public void deleteProject(Long id) {

        Project project = findProjectById(id);

        deleteAllImages(project);

        projectRepository.delete(project);

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


    // Sort by creation date
    @Override
    public List<ProjectResponse> getAllProjectsOrderedByDate(String sortDirection) {

        // Sort object
        Sort sort = sortByDate(sortDirection);

        // Sort projects
        List<Project> projects = projectRepository.findAll(sort);

        return mapProjectsToResponse(projects);

    }

    // ------------------------------------------------ HELPER METHODS -------------------------------------------------

    // --- Helpers for create project ---

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

    // --- Helper for update project ---

    private Project findProjectById(Long id) {

        return projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id " + id));
    }

    // --- Helper for update image ---

    private Image findImageById(Long imageId) {

        return imageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Image not found with id " + imageId));
    }

    // --- Helper for delete image ---

    private void deleteImageUrl(Image image, UpdateImageRequest request) {

        // If URL changes, delete the old one
        if (request.getUrl() != null && !request.getUrl().equals(image.getUrl())) {
            imageStorageService.delete(image.getUrl());
        }

    }
    // --- Helper for delete project ---

    private void deleteAllImages(Project project) {

        for (Image image : project.getImages()) {
            if (image.getUrl() != null) {
                imageStorageService.delete(image.getUrl());
            }
        }
    }

    // --- Helper for sort by date ---
    private Sort sortByDate(String sortDirection) {

        if (sortDirection != null && sortDirection.equalsIgnoreCase("asc")) {
            return Sort.by(Sort.Direction.ASC, "creationDate");
        }

        // Default sort is the latest project first
        return Sort.by(Sort.Direction.DESC, "creationDate");
    }

    // --- Helper for mapping of project list ---

    private List<ProjectResponse> mapProjectsToResponse(List<Project> projects) {
        return projects.stream().map(projectMapper::toResponse).toList();
    }

    /**
     * Validate that deleting the image will not violate business rules
     */
    private void validateImageDeletion(Project project, Image imageToDelete) {
        long beforeCount = project.getImages().stream()
                .filter(img -> img.getImageType() == ImageType.BEFORE)
                .filter(img -> !img.getId().equals(imageToDelete.getId()))
                .count();

        long afterCount = project.getImages().stream()
                .filter(img -> img.getImageType() == ImageType.AFTER)
                .filter(img -> !img.getId().equals(imageToDelete.getId()))
                .count();

        if (beforeCount < 1) {
            throw new IllegalArgumentException("Cannot delete the last BEFORE image of the project");
        }
        if (afterCount < 1) {
            throw new IllegalArgumentException("Cannot delete the last AFTER image of the project");
        }
    }


}