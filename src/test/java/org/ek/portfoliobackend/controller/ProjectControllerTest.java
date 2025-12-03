package org.ek.portfoliobackend.controller;

import tools.jackson.databind.ObjectMapper;
import org.ek.portfoliobackend.dto.request.CreateProjectRequest;
import org.ek.portfoliobackend.dto.request.ImageUploadRequest;
import org.ek.portfoliobackend.dto.response.ImageResponse;
import org.ek.portfoliobackend.dto.response.ProjectResponse;
import org.ek.portfoliobackend.model.CustomerType;
import org.ek.portfoliobackend.model.ImageType;
import org.ek.portfoliobackend.model.WorkType;
import org.ek.portfoliobackend.service.ProjectService;
import org.ek.portfoliobackend.exception.custom.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for ProjectController endpoints.
 * Tests the REST API layer for project creation with multipart file uploads.
 */
@WebMvcTest(ProjectController.class)
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProjectService projectService;

    private CreateProjectRequest validRequest;
    private MockMultipartFile beforeImage;
    private MockMultipartFile afterImage;
    private List<ImageUploadRequest> validMetadata;
    private ProjectResponse expectedResponse;

    @BeforeEach
    void setUp() {
        // Setup valid request data
        validRequest = new CreateProjectRequest(
                "Test Facade Cleaning Project",
                "Complete cleaning of building facade",
                LocalDate.of(2024, 6, 15),
                WorkType.FACADE_CLEANING,
                CustomerType.BUSINESS_CUSTOMER
        );

        // Setup mock image files
        beforeImage = new MockMultipartFile(
                "images",
                "before.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "before image content".getBytes()
        );

        afterImage = new MockMultipartFile(
                "images",
                "after.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "after image content".getBytes()
        );

        // Setup valid metadata
        validMetadata = Arrays.asList(
                new ImageUploadRequest(ImageType.BEFORE, false),
                new ImageUploadRequest(ImageType.AFTER, true)
        );

        // Setup expected response
        expectedResponse = ProjectResponse.builder()
                .id(1L)
                .title("Test Facade Cleaning Project")
                .description("Complete cleaning of building facade")
                .executionDate(LocalDate.of(2024, 6, 15))
                .creationDate(LocalDate.now())
                .workType(WorkType.FACADE_CLEANING)
                .customerType(CustomerType.BUSINESS_CUSTOMER)
                .images(Arrays.asList(
                        ImageResponse.builder()
                                .id(1L)
                                .url("/uploads/before.jpg")
                                .imageType(ImageType.BEFORE)
                                .isFeatured(false)
                                .build(),
                        ImageResponse.builder()
                                .id(2L)
                                .url("/uploads/after.jpg")
                                .imageType(ImageType.AFTER)
                                .isFeatured(true)
                                .build()
                ))
                .build();
    }

    @Test
    @DisplayName("GET /api/projects/{id} - Success")
    void getProjectById_WithValidId_ReturnsProject() throws Exception {
        // Arrange
        ProjectResponse response = new ProjectResponse();
        response.setId(1L);
        response.setTitle("Test Project");
        response.setDescription("Test Description");
        response.setExecutionDate(LocalDate.of(2025, 4, 1));
        response.setCreationDate(LocalDate.now());
        response.setWorkType(WorkType.FACADE_CLEANING);
        response.setCustomerType(CustomerType.BUSINESS_CUSTOMER);
        response.setImages(List.of());

        when(projectService.getProjectById(1L)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/projects/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Project"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.workType").value("FACADE_CLEANING"))
                .andExpect(jsonPath("$.customerType").value("BUSINESS_CUSTOMER"));
    }

    @Test
    @DisplayName("GET /api/projects/{id} - Not Found")
    void getProjectById_WithInvalidId_ReturnsNotFound() throws Exception {
        // Arrange
        when(projectService.getProjectById(999L))
                .thenThrow(new ResourceNotFoundException("Project", 999L));

        // Act & Assert
        mockMvc.perform(get("/api/projects/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/projects - Success with multiple projects")
    void getAllProjects_ReturnsListOfProjects() throws Exception {
        // Arrange
        ProjectResponse project1 = new ProjectResponse();
        project1.setId(1L);
        project1.setTitle("Project 1");
        project1.setWorkType(WorkType.FACADE_CLEANING);
        project1.setCustomerType(CustomerType.BUSINESS_CUSTOMER);
        project1.setImages(List.of());

        ProjectResponse project2 = new ProjectResponse();
        project2.setId(2L);
        project2.setTitle("Project 2");
        project2.setWorkType(WorkType.ROOF_CLEANING);
        project2.setCustomerType(CustomerType.PRIVATE_CUSTOMER);
        project2.setImages(List.of());

        when(projectService.getAllProjects()).thenReturn(Arrays.asList(project1, project2));

        // Act & Assert
        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Project 1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].title").value("Project 2"));
    }

    @Test
    @DisplayName("GET /api/projects - Success with no projects")
    void getAllProjects_ReturnsEmptyList() throws Exception {
        // Arrange
        when(projectService.getAllProjects()).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("POST /api/projects - Success with valid data")
    void createProject_WithValidData_ReturnsCreated() throws Exception {
        // Arrange
        when(projectService.createProject(any(), anyList(), anyList()))
                .thenReturn(expectedResponse);

        String requestJson = objectMapper.writeValueAsString(validRequest);
        String metadataJson = objectMapper.writeValueAsString(validMetadata);

        MockMultipartFile requestPart = new MockMultipartFile(
                "data",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                requestJson.getBytes()
        );

        MockMultipartFile metadataPart = new MockMultipartFile(
                "imageMetadata",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                metadataJson.getBytes()
        );

        // Act & Assert
        mockMvc.perform(multipart("/api/projects")
                        .file(requestPart)
                        .file(beforeImage)
                        .file(afterImage)
                        .file(metadataPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Facade Cleaning Project"))
                .andExpect(jsonPath("$.workType").value("FACADE_CLEANING"))
                .andExpect(jsonPath("$.customerType").value("BUSINESS_CUSTOMER"))
                .andExpect(jsonPath("$.images").isArray())
                .andExpect(jsonPath("$.images.length()").value(2));
    }

    @Test
    @DisplayName("POST /api/projects - Validation error when images and metadata count mismatch")
    void createProject_WithMismatchedImageAndMetadata_ReturnsBadRequest() throws Exception {
        // Arrange
        String requestJson = objectMapper.writeValueAsString(validRequest);

        // Only one metadata entry for two images
        List<ImageUploadRequest> mismatchedMetadata = Arrays.asList(
                new ImageUploadRequest(ImageType.BEFORE, false)
        );
        String metadataJson = objectMapper.writeValueAsString(mismatchedMetadata);

        MockMultipartFile requestPart = new MockMultipartFile(
                "data",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                requestJson.getBytes()
        );

        MockMultipartFile metadataPart = new MockMultipartFile(
                "imageMetadata",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                metadataJson.getBytes()
        );

        // Act & Assert
        mockMvc.perform(multipart("/api/projects")
                        .file(requestPart)
                        .file(beforeImage)
                        .file(afterImage)
                        .file(metadataPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason(org.hamcrest.Matchers.containsString("Mismatch")));
    }

    @Test
    @DisplayName("POST /api/projects - Validation error when missing BEFORE image")
    void createProject_WithoutBeforeImage_ReturnsBadRequest() throws Exception {
        // Arrange
        when(projectService.createProject(any(), anyList(), anyList()))
                .thenThrow(new IllegalArgumentException("At least one BEFORE image must be provided"));

        String requestJson = objectMapper.writeValueAsString(validRequest);

        // Only AFTER image metadata
        List<ImageUploadRequest> afterOnlyMetadata = Arrays.asList(
                new ImageUploadRequest(ImageType.AFTER, true)
        );
        String metadataJson = objectMapper.writeValueAsString(afterOnlyMetadata);

        MockMultipartFile requestPart = new MockMultipartFile(
                "data",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                requestJson.getBytes()
        );

        MockMultipartFile metadataPart = new MockMultipartFile(
                "imageMetadata",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                metadataJson.getBytes()
        );

        // Act & Assert
        mockMvc.perform(multipart("/api/projects")
                        .file(requestPart)
                        .file(afterImage)
                        .file(metadataPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason(org.hamcrest.Matchers.containsString("BEFORE image")));
    }

    @Test
    @DisplayName("POST /api/projects - Validation error when missing AFTER image")
    void createProject_WithoutAfterImage_ReturnsBadRequest() throws Exception {
        // Arrange
        when(projectService.createProject(any(), anyList(), anyList()))
                .thenThrow(new IllegalArgumentException("At least one AFTER image must be provided"));

        String requestJson = objectMapper.writeValueAsString(validRequest);

        // Only BEFORE image metadata
        List<ImageUploadRequest> beforeOnlyMetadata = Arrays.asList(
                new ImageUploadRequest(ImageType.BEFORE, false)
        );
        String metadataJson = objectMapper.writeValueAsString(beforeOnlyMetadata);

        MockMultipartFile requestPart = new MockMultipartFile(
                "data",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                requestJson.getBytes()
        );

        MockMultipartFile metadataPart = new MockMultipartFile(
                "imageMetadata",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                metadataJson.getBytes()
        );

        // Act & Assert
        mockMvc.perform(multipart("/api/projects")
                        .file(requestPart)
                        .file(beforeImage)
                        .file(metadataPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason(org.hamcrest.Matchers.containsString("AFTER image")));
    }

    @Test
    @DisplayName("POST /api/projects - Internal server error on storage failure")
    void createProject_WithStorageFailure_ReturnsInternalServerError() throws Exception {
        // Arrange
        when(projectService.createProject(any(), anyList(), anyList()))
                .thenThrow(new RuntimeException("Failed to store images"));

        String requestJson = objectMapper.writeValueAsString(validRequest);
        String metadataJson = objectMapper.writeValueAsString(validMetadata);

        MockMultipartFile requestPart = new MockMultipartFile(
                "data",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                requestJson.getBytes()
        );

        MockMultipartFile metadataPart = new MockMultipartFile(
                "imageMetadata",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                metadataJson.getBytes()
        );

        // Act & Assert
        mockMvc.perform(multipart("/api/projects")
                        .file(requestPart)
                        .file(beforeImage)
                        .file(afterImage)
                        .file(metadataPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isInternalServerError())
                .andExpect(status().reason(org.hamcrest.Matchers.containsString("internal error")));
    }

    @Test
    @DisplayName("POST /api/projects - Success with multiple images of same type")
    void createProject_WithMultipleBeforeAndAfterImages_ReturnsCreated() throws Exception {
        // Arrange
        MockMultipartFile beforeImage2 = new MockMultipartFile(
                "images",
                "before2.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "before image 2 content".getBytes()
        );

        MockMultipartFile afterImage2 = new MockMultipartFile(
                "images",
                "after2.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "after image 2 content".getBytes()
        );

        List<ImageUploadRequest> multipleMetadata = Arrays.asList(
                new ImageUploadRequest(ImageType.BEFORE, false),
                new ImageUploadRequest(ImageType.BEFORE, false),
                new ImageUploadRequest(ImageType.AFTER, true),
                new ImageUploadRequest(ImageType.AFTER, false)
        );

        when(projectService.createProject(any(), anyList(), anyList()))
                .thenReturn(expectedResponse);

        String requestJson = objectMapper.writeValueAsString(validRequest);
        String metadataJson = objectMapper.writeValueAsString(multipleMetadata);

        MockMultipartFile requestPart = new MockMultipartFile(
                "data",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                requestJson.getBytes()
        );

        MockMultipartFile metadataPart = new MockMultipartFile(
                "imageMetadata",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                metadataJson.getBytes()
        );

        // Act & Assert
        mockMvc.perform(multipart("/api/projects")
                        .file(requestPart)
                        .file(beforeImage)
                        .file(beforeImage2)
                        .file(afterImage)
                        .file(afterImage2)
                        .file(metadataPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated());
    }
}