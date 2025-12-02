package org.ek.portfoliobackend.service.impl;

import org.ek.portfoliobackend.dto.request.CreateProjectRequest;
import org.ek.portfoliobackend.dto.request.UpdateProjectRequest;
import org.ek.portfoliobackend.dto.response.ProjectResponse;
import org.ek.portfoliobackend.model.CustomerType;
import org.ek.portfoliobackend.model.WorkType;
import org.ek.portfoliobackend.repository.ProjectRepository;
import org.ek.portfoliobackend.service.ProjectService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectServiceImpl(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Override
    public ProjectResponse createProject(CreateProjectRequest request) {
        throw new UnsupportedOperationException("Not implemented yet");
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
}
