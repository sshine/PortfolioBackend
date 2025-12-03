package org.ek.portfoliobackend.mapper;

import org.ek.portfoliobackend.dto.request.CreateProjectRequest;
import org.ek.portfoliobackend.dto.request.UpdateImageRequest;
import org.ek.portfoliobackend.dto.request.UpdateProjectRequest;
import org.ek.portfoliobackend.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ProjectMapper Tests")
class ProjectMapperTest {

    private ProjectMapper projectMapper;

    @BeforeEach
    void setUp() {
        projectMapper = new ProjectMapper();
    }

    @Test
    @DisplayName("Should map CreateProjectRequest to Project entity")
    void testToProjectEntity() {
        LocalDate executionDate = LocalDate.of(2025, 6, 15);
        CreateProjectRequest request = new CreateProjectRequest(
                "Test Project",
                "Test Description",
                executionDate,
                WorkType.FACADE_CLEANING,
                CustomerType.BUSINESS_CUSTOMER
        );

        Project project = projectMapper.toProjectEntity(request);

        assertThat(project).isNotNull();
        assertThat(project.getTitle()).isEqualTo("Test Project");
        assertThat(project.getDescription()).isEqualTo("Test Description");
        assertThat(project.getExecutionDate()).isEqualTo(executionDate);
        assertThat(project.getWorkType()).isEqualTo(WorkType.FACADE_CLEANING);
        assertThat(project.getCustomerType()).isEqualTo(CustomerType.BUSINESS_CUSTOMER);
    }

    @Test
    @DisplayName("Should set creationDate automatically")
    void testCreationDateSet() {
        CreateProjectRequest request = new CreateProjectRequest(
                "Test",
                "Test",
                LocalDate.now(),
                WorkType.PAVING_CLEANING,
                CustomerType.PRIVATE_CUSTOMER
        );

        Project project = projectMapper.toProjectEntity(request);

        assertThat(project.getCreationDate()).isEqualTo(LocalDate.now());
    }

    @Test
    @DisplayName("Should handle all work types")
    void testAllWorkTypes() {
        for (WorkType workType : WorkType.values()) {
            CreateProjectRequest request = new CreateProjectRequest(
                    "Test",
                    "Test",
                    LocalDate.now(),
                    workType,
                    CustomerType.PRIVATE_CUSTOMER
            );

            Project project = projectMapper.toProjectEntity(request);

            assertThat(project.getWorkType()).isEqualTo(workType);
        }
    }

    @Test
    @DisplayName("Should handle all customer types")
    void testAllCustomerTypes() {
        for (CustomerType type : CustomerType.values()) {
            CreateProjectRequest request = new CreateProjectRequest(
                    "Test",
                    "Test",
                    LocalDate.now(),
                    WorkType.ROOF_CLEANING,
                    type
            );

            Project project = projectMapper.toProjectEntity(request);

            assertThat(project.getCustomerType()).isEqualTo(type);
        }
    }

    @Test
    @DisplayName("Should create Image entity with all fields")
    void testToImage() {
        String url = "https://example.com/image.jpg";
        Project project = new Project();
        project.setId(1L);
        project.setTitle("Test Project");

        Image image = projectMapper.toImage(url, ImageType.BEFORE, true, project);

        assertThat(image).isNotNull();
        assertThat(image.getUrl()).isEqualTo(url);
        assertThat(image.getImageType()).isEqualTo(ImageType.BEFORE);
        assertThat(image.getIsFeatured()).isTrue();
        assertThat(image.getProject()).isEqualTo(project);
    }

    @Test
    @DisplayName("Should create non-featured image")
    void testNonFeaturedImage() {
        Project project = new Project();
        Image image = projectMapper.toImage("/test.jpg", ImageType.AFTER, false, project);

        assertThat(image.getIsFeatured()).isFalse();
        assertThat(image.getImageType()).isEqualTo(ImageType.AFTER);
    }

    @Test
    @DisplayName("Should handle both image types")
    void testBothImageTypes() {
        Project project = new Project();

        Image beforeImage = projectMapper.toImage("/before.jpg", ImageType.BEFORE, false, project);
        Image afterImage = projectMapper.toImage("/after.jpg", ImageType.AFTER, false, project);

        assertThat(beforeImage.getImageType()).isEqualTo(ImageType.BEFORE);
        assertThat(afterImage.getImageType()).isEqualTo(ImageType.AFTER);
    }

    @Test
    @DisplayName("Should establish project relationship")
    void testProjectRelationship() {
        Project project = new Project();
        project.setId(5L);
        project.setTitle("Relationship Test");

        Image image = projectMapper.toImage("/test.jpg", ImageType.BEFORE, true, project);

        assertThat(image.getProject()).isNotNull();
        assertThat(image.getProject().getId()).isEqualTo(5L);
        assertThat(image.getProject().getTitle()).isEqualTo("Relationship Test");
    }


    // Opdatering af
    @Test
    void updateProjectEntity() {
        // Arrange
        Project project = new Project();
        project.setTitle("Old");
        project.setDescription("Old desc");
        project.setWorkType(WorkType.FACADE_CLEANING);
        project.setCustomerType(CustomerType.PRIVATE_CUSTOMER);
        project.setExecutionDate(LocalDate.of(2020,1,1));

        UpdateProjectRequest req = new UpdateProjectRequest();
        req.setTitle("New Title");
        req.setDescription(null);    // should be ignored
        req.setWorkType(WorkType.ROOF_CLEANING);
        req.setCustomerType(null);   // should be ignored
        req.setExecutionDate(LocalDate.of(2030,5,5));

        // Act
        projectMapper.updateProjectEntity(req, project);

        // Assert
        assertThat(project.getTitle()).isEqualTo("New Title");
        assertThat(project.getDescription()).isEqualTo("Old desc");  // unchanged
        assertThat(project.getWorkType()).isEqualTo(WorkType.ROOF_CLEANING);
        assertThat(project.getCustomerType()).isEqualTo(CustomerType.PRIVATE_CUSTOMER); // unchanged
        assertThat(project.getExecutionDate()).isEqualTo(LocalDate.of(2030,5,5));
    }

    @Test
    void updateImageEntity_shouldUpdateOnlyNonNullFields() {
        // Arrange
        Image image = new Image();
        image.setUrl("old.jpg");
        image.setImageType(ImageType.BEFORE);
        image.setIsFeatured(false);

        UpdateImageRequest req = new UpdateImageRequest();
        req.setUrl("new.jpg");
        req.setImageType(null);       // should be ignored
        req.setIsFeatured(true);

        // Act
        projectMapper.updateImageEntity(req, image);

        // Assert
        assertThat(image.getUrl()).isEqualTo("new.jpg");
        assertThat(image.getImageType()).isEqualTo(ImageType.BEFORE);   // unchanged
        assertThat(image.getIsFeatured()).isTrue();
    }

    }



