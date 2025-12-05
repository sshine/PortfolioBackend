package org.ek.portfoliobackend.mapper;

import org.ek.portfoliobackend.dto.request.CreateProjectRequest;
import org.ek.portfoliobackend.dto.request.UpdateImageRequest;
import org.ek.portfoliobackend.dto.request.UpdateProjectRequest;
import org.ek.portfoliobackend.dto.response.ImageResponse;
import org.ek.portfoliobackend.dto.response.ProjectResponse;
import org.ek.portfoliobackend.model.*;
import java.util.List;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.stream.Collectors;

@Component
public class ProjectMapper {

    /**
     * Konverterer CreateProjectRequest DTO til Project entity.
     * creationDate sættes automatisk til nuværende tidspunkt.
     *
     * @param request CreateProjectRequest DTO med projektdata
     * @return Project entity klar til at gemmes i databasen
     */
    public Project toProjectEntity(CreateProjectRequest request) {
        Project project = new Project();
        project.setTitle(request.getTitle());
        project.setDescription(request.getDescription());
        project.setExecutionDate(request.getExecutionDate());
        project.setWorkType(request.getServiceCategory());
        project.setCustomerType(request.getCustomerType());
        project.setCreationDate(LocalDate.now());

        return project;
    }

    /**
     * Opretter en Image entity med alle nødvendige felter og etablerer
     * relationen til det tilhørende Project.
     *
     * @param url URL til billedet
     * @param imageType Type af billede (BEFORE eller AFTER)
     * @param isFeatured Om billedet skal fremhæves
     * @param project Det projekt billedet tilhører
     * @return Image entity klar til at gemmes i databasen
     */
    public Image toImage(String url, ImageType imageType, boolean isFeatured, Project project) {
        Image image = new Image();
        image.setUrl(url);
        image.setImageType(imageType);
        image.setIsFeatured(isFeatured);
        image.setProject(project);

        return image;
    }


    // Konverterer project til responseDTO

    public ProjectResponse toProjectResponse(Project project) {

        ProjectResponse response = new ProjectResponse();
        response.setId(project.getId());
        response.setTitle(project.getTitle());
        response.setDescription(project.getDescription());
        response.setExecutionDate(project.getExecutionDate());
        response.setCreationDate(project.getCreationDate());
        response.setWorkType(project.getWorkType());
        response.setCustomerType(project.getCustomerType());

        // Map alle billeder
        List<ImageResponse> imageDtos = project.getImages()
                .stream()
                .map(this::toImageResponse)
                .toList();

        response.setImages(imageDtos);

        return response;
    }

    // Patch-opdaterer et Project med de felter der er sat i request (null = ignorer).

    public void updateProjectEntity(UpdateProjectRequest request, Project project) {

        if (request.getTitle() != null) {
            project.setTitle(request.getTitle());
        }

        if(request.getDescription() != null) {
            project.setDescription(request.getDescription());
        }

        if (request.getWorkType() != null) {
            project.setWorkType(request.getWorkType());
        }

        if (request.getCustomerType() != null) {
            project.setCustomerType(request.getCustomerType());
        }

        if (request.getExecutionDate() != null) {
            project.setExecutionDate(request.getExecutionDate());
        }
    }

    // Patch-opdaterer et Image med de felter der er sat i request (null = ignorer).
    public void updateImageEntity(UpdateImageRequest request, Image image) {

        if (request.getUrl() != null) {
            image.setUrl(request.getUrl());
        }

        if (request.getImageType() != null) {
            image.setImageType(request.getImageType());
        }

        if (request.getIsFeatured() != null) {
            image.setIsFeatured(request.getIsFeatured());
        }
    }






    /**
     * Konverterer Project entity til ProjectResponse DTO.
     * Inkluderer alle projekt felter og mapper images til response format.
     *
     * @param project Project entity fra databasen
     * @return ProjectResponse DTO til API response
     */
    public ProjectResponse toResponse(Project project) {
        ProjectResponse response = new ProjectResponse();
        response.setId(project.getId());
        response.setTitle(project.getTitle());
        response.setDescription(project.getDescription());
        response.setCreationDate(project.getCreationDate());
        response.setExecutionDate(project.getExecutionDate());
        response.setWorkType(project.getWorkType());
        response.setCustomerType(project.getCustomerType());

        // Map images if present
        if (project.getImages() != null && !project.getImages().isEmpty()) {
            response.setImages(
                    project.getImages().stream()
                            .map(this::toImageResponse)
                            .collect(Collectors.toList())
            );
        }

        return response;
    }

    /**
     * Konverterer Image entity til ImageResponse DTO.
     *
     * @param image Image entity fra databasen
     * @return ImageResponse DTO
     */
    public ImageResponse toImageResponse(Image image) {
        ImageResponse response = new ImageResponse();
        response.setId(image.getId());
        response.setUrl(image.getUrl());
        response.setImageType(image.getImageType());
        response.setIsFeatured(image.getIsFeatured());
        return response;
    }
}