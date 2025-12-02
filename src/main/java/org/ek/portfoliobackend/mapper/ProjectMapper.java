package org.ek.portfoliobackend.mapper;

import org.ek.portfoliobackend.dto.request.CreateProjectRequest;
import org.ek.portfoliobackend.model.*;

import org.springframework.stereotype.Component;

import java.time.LocalDate;

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
}