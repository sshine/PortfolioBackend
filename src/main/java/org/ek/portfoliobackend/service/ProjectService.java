package org.ek.portfoliobackend.service;

import org.ek.portfoliobackend.dto.request.CreateProjectRequest;
import org.ek.portfoliobackend.dto.request.UpdateProjectRequest;
import org.ek.portfoliobackend.dto.response.ProjectResponse;
import org.ek.portfoliobackend.model.CustomerType;
import org.ek.portfoliobackend.model.WorkType;

import java.time.LocalDate;
import java.util.List;

public interface ProjectService {

    //create new project
    ProjectResponse createProject(CreateProjectRequest request);

    //update existing project
    ProjectResponse updateProject(Long id, UpdateProjectRequest request);

    //get project by id
    ProjectResponse getProjectById(Long id);

    //get all projects
    List<ProjectResponse> getAllProjects();

    //delete project by id
    void deleteProject(Long id);

    //get projects by service category
    List<ProjectResponse> getProjectsByServiceCategory(WorkType workType);

    //get projects filtered by customer type
    List<ProjectResponse> getProjectsByCustomerType(CustomerType customerType);

    //get projects filtered by service category and customer type
    List<ProjectResponse> getProjectsByFilters(WorkType workType, CustomerType customerType);

    //get projects within date range
    List<ProjectResponse> getProjectsByDateRange(LocalDate startDate, LocalDate endDate);

    //get all projects ordered by creation date (newest first)
    List<ProjectResponse> getAllProjectsOrderedByDate();

}
