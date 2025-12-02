package org.ek.portfoliobackend.service.impl;

import org.ek.portfoliobackend.dto.request.CreateProjectRequest;
import org.ek.portfoliobackend.dto.request.UpdateProjectRequest;
import org.ek.portfoliobackend.model.CustomerType;
import org.ek.portfoliobackend.model.WorkType;
import org.ek.portfoliobackend.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for ProjectServiceImpl to verify that unimplemented methods throw UnsupportedOperationException.
 */

@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;

    private ProjectServiceImpl projectService;

    @BeforeEach
    void setUp() {
        projectService = new ProjectServiceImpl(projectRepository);
    }

    @Test
    void createProject_throwsUnsupportedOperationException() {
        CreateProjectRequest request = new CreateProjectRequest(
                "Test", "Desc", LocalDate.now(),
                WorkType.PAVING_CLEANING, CustomerType.PRIVATE_CUSTOMER
        );

        assertThatThrownBy(() -> projectService.createProject(request)) // by running this method
                .isInstanceOf(UnsupportedOperationException.class) // expect this exception
                .hasMessage("Not implemented yet"); // with this message
    }

    @Test
    void updateProject_throwsUnsupportedOperationException() {
        UpdateProjectRequest request = new UpdateProjectRequest();

        assertThatThrownBy(() -> projectService.updateProject(1L, request))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessage("Not implemented yet");
    }

    @Test
    void getProjectById_throwsUnsupportedOperationException() {
        assertThatThrownBy(() -> projectService.getProjectById(1L))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessage("Not implemented yet");
    }

    @Test
    void getAllProjects_throwsUnsupportedOperationException() {
        assertThatThrownBy(() -> projectService.getAllProjects())
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessage("Not implemented yet");
    }

    @Test
    void deleteProject_throwsUnsupportedOperationException() {
        assertThatThrownBy(() -> projectService.deleteProject(1L))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessage("Not implemented yet");
    }

    @Test
    void getProjectsByServiceCategory_throwsUnsupportedOperationException() {
        assertThatThrownBy(() -> projectService.getProjectsByServiceCategory(WorkType.PAVING_CLEANING))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessage("Not implemented yet");
    }

    @Test
    void getProjectsByCustomerType_throwsUnsupportedOperationException() {
        assertThatThrownBy(() -> projectService.getProjectsByCustomerType(CustomerType.PRIVATE_CUSTOMER))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessage("Not implemented yet");
    }

    @Test
    void getProjectsByFilters_throwsUnsupportedOperationException() {
        assertThatThrownBy(() -> projectService.getProjectsByFilters(
                WorkType.PAVING_CLEANING, CustomerType.PRIVATE_CUSTOMER))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessage("Not implemented yet");
    }

    @Test
    void getProjectsByDateRange_throwsUnsupportedOperationException() {
        assertThatThrownBy(() -> projectService.getProjectsByDateRange(
                LocalDate.now(), LocalDate.now().plusDays(7)))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessage("Not implemented yet");
    }

    @Test
    void getAllProjectsOrderedByDate_throwsUnsupportedOperationException() {
        assertThatThrownBy(() -> projectService.getAllProjectsOrderedByDate())
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessage("Not implemented yet");
    }
}
