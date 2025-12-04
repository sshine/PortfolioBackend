package org.ek.portfoliobackend.controller;

import org.ek.portfoliobackend.dto.request.UpdateImageRequest;
import org.ek.portfoliobackend.dto.request.UpdateProjectRequest;
import org.ek.portfoliobackend.exception.GlobalExceptionHandler;
import org.springframework.context.annotation.Import;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for ProjectController endpoints.
 * Tests the REST API layer for project creation with multipart file uploads.
 */
@WebMvcTest(controllers = ProjectController.class)
@Import(GlobalExceptionHandler.class)
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
        expectedResponse = new ProjectResponse();
        expectedResponse.setId(1L);
        expectedResponse.setTitle("Test Facade Cleaning Project");
        expectedResponse.setDescription("Complete cleaning of building facade");
        expectedResponse.setExecutionDate(LocalDate.of(2024, 6, 15));
        expectedResponse.setCreationDate(LocalDate.now());
        expectedResponse.setWorkType(WorkType.FACADE_CLEANING);
        expectedResponse.setCustomerType(CustomerType.BUSINESS_CUSTOMER);
        expectedResponse.setImages(List.of(
                new ImageResponse(1L, "/uploads/before.jpg", ImageType.BEFORE, false),
                new ImageResponse(2L, "/uploads/after.jpg", ImageType.AFTER, true)
        ));
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
                .andExpect(status().isBadRequest());
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
                .andExpect(status().isBadRequest());
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
                .andExpect(status().isBadRequest());
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
                .andExpect(status().isInternalServerError());
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

    @Test
    @DisplayName("PUT /api/projects/{id} - Success with full update")
    void updateProject_WithValidData_ReturnsUpdatedProject() throws Exception {
        // Arrange
        UpdateProjectRequest request = new UpdateProjectRequest();
        request.setTitle("Updated Title");
        request.setDescription("Updated Description");
        request.setWorkType(WorkType.ROOF_CLEANING);
        request.setCustomerType(CustomerType.PRIVATE_CUSTOMER);
        request.setExecutionDate(LocalDate.of(2025, 10, 31));

        ProjectResponse response = new ProjectResponse();
        response.setId(1L);
        response.setTitle("Updated Title");
        response.setDescription("Updated Description");
        response.setWorkType(WorkType.ROOF_CLEANING);
        response.setCustomerType(CustomerType.PRIVATE_CUSTOMER);
        response.setExecutionDate(LocalDate.of(2025, 10, 31));

        when(projectService.updateProject(eq(1L), any(UpdateProjectRequest.class)))
                .thenReturn(response);

        // Act & Assert
        mockMvc.perform(put("/api/projects/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.description").value("Updated Description"))
                .andExpect(jsonPath("$.workType").value("ROOF_CLEANING"))
                .andExpect(jsonPath("$.customerType").value("PRIVATE_CUSTOMER"));
    }

    @Test
    @DisplayName("PUT /api/projects/{id} - Success with partial update")
    void updateProject_WithPartialData_ReturnsUpdatedProject() throws Exception {
        // Arrange
        UpdateProjectRequest request = new UpdateProjectRequest();
        request.setTitle("New title only");

        ProjectResponse response = new ProjectResponse();
        response.setId(1L);
        response.setTitle("New title only");
        response.setDescription("OG Description");
        response.setWorkType(WorkType.FACADE_CLEANING);

        when(projectService.updateProject(eq(1L), any(UpdateProjectRequest.class)))
                .thenReturn(response);

        // Act & Assert
        mockMvc.perform(put("/api/projects/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("New title only"));
    }

    @Test
    @DisplayName("PUT /api/projects/{id} - Not Found")
    void updateProject_WithInvalidId_ReturnsNotFound() throws Exception {
        // Arrange
        UpdateProjectRequest request = new UpdateProjectRequest();
        request.setTitle("Updated Title");

        when(projectService.updateProject(eq(999L), any(UpdateProjectRequest.class)))
                .thenThrow(new ResourceNotFoundException("Project", 999L));

        // Act & Assert
        mockMvc.perform(put("/api/projects/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PATCH /api/projects/{id}/images - Success")
    void uploadProjectImages_WithValidData_ReturnsOk() throws Exception {
        // Arrange
        MockMultipartFile image = new MockMultipartFile(
                "images", "image.jpg", MediaType.IMAGE_JPEG_VALUE, "image content".getBytes()
        );

        List<ImageUploadRequest> metadata = List.of(
                new ImageUploadRequest(ImageType.BEFORE, false)
        );

        ProjectResponse response = new ProjectResponse();
        response.setId(1L);

        when(projectService.addImagesToProject(eq(1L), anyList(), anyList()))
                .thenReturn(response);

        MockMultipartFile metadataPart = new MockMultipartFile(
                "imageMetadata", "", MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(metadata)
        );

        // Act & Assert
        mockMvc.perform(multipart("/api/projects/1/images")
                        .file(image)
                        .file(metadataPart)
                        .with(request -> {
                            request.setMethod("PATCH");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("PATCH /api/projects/{id}/images - Not Found")
    void uploadProjectImages_ProjectNotFound_ReturnsNotFound() throws Exception {
        // Arrange
        MockMultipartFile image = new MockMultipartFile(
                "images", "image.jpg", MediaType.IMAGE_JPEG_VALUE, "image content".getBytes()
        );

        List<ImageUploadRequest> metadata = List.of(
                new ImageUploadRequest(ImageType.BEFORE, false)
        );

        when(projectService.addImagesToProject(eq(999L), anyList(), anyList()))
                .thenThrow(new ResourceNotFoundException("Project", 999L));

        MockMultipartFile metadataPart = new MockMultipartFile(
                "imageMetadata", "", MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(metadata)
        );

        // Act & Assert
        mockMvc.perform(multipart("/api/projects/999/images")
                        .file(image)
                        .file(metadataPart)
                        .with(request -> {
                            request.setMethod("PATCH");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PATCH /api/projects/{projectId}/images/{imageId} - Success")
    void updateImageMetadata_Success() throws Exception {
        // Arrange
        UpdateImageRequest request = new UpdateImageRequest();
        request.setImageType(ImageType.AFTER);
        request.setIsFeatured(true);

        ProjectResponse response = new ProjectResponse();
        response.setId(1L);

        when(projectService.updateImageMetadata(eq(1L), eq(1L), any(UpdateImageRequest.class)))
                .thenReturn(response);

        // Act & Assert
        mockMvc.perform(patch("/api/projects/1/images/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("PATCH /api/projects/{projectId}/images/{imageId} - Image Not Found")
    void updateImageMetadata_ImageNotFound() throws Exception {
        // Arrange
        UpdateImageRequest request = new UpdateImageRequest();
        request.setImageType(ImageType.AFTER);

        when(projectService.updateImageMetadata(eq(1L), eq(999L), any(UpdateImageRequest.class)))
                .thenThrow(new ResourceNotFoundException("Image", 999L));

        // Act & Assert
        mockMvc.perform(patch("/api/projects/1/images/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/projects/{projectId}/images/{imageId} - Success")
    void deleteImage_WithValidIds_ReturnsOk() throws Exception {
        // Arrange
        ProjectResponse response = new ProjectResponse();
        response.setId(1L);

        when(projectService.deleteImageFromProject(eq(1L), eq(1L)))
                .thenReturn(response);

        // Act & Assert
        mockMvc.perform(delete("/api/projects/1/images/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("DELETE /api/projects/{projectId}/images/{imageId} - Image Not Found")
    void deleteImage_ImageNotFound_ReturnsNotFound() throws Exception {
        // Arrange
        when(projectService.deleteImageFromProject(eq(1L), eq(999L)))
                .thenThrow(new ResourceNotFoundException("Image", 999L));

        // Act & Assert
        mockMvc.perform(delete("/api/projects/1/images/999"))
                .andExpect(status().isNotFound());
    }
}