package org.ek.portfoliobackend.service.impl;

import org.ek.portfoliobackend.dto.request.CreateProjectRequest;
import org.ek.portfoliobackend.dto.request.ImageUploadRequest;
import org.ek.portfoliobackend.dto.request.UpdateImageRequest;
import org.ek.portfoliobackend.dto.request.UpdateProjectRequest;
import org.ek.portfoliobackend.dto.response.ImageResponse;
import org.ek.portfoliobackend.dto.response.ProjectResponse;
import org.ek.portfoliobackend.mapper.ProjectMapper;
import org.ek.portfoliobackend.model.*;
import org.ek.portfoliobackend.repository.ImageRepository;
import org.ek.portfoliobackend.repository.ProjectRepository;
import org.ek.portfoliobackend.service.ImageStorageService;
import org.ek.portfoliobackend.exception.custom.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ProjectServiceImpl to verify that unimplemented methods throw UnsupportedOperationException.
 */

@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private ImageStorageService imageStorageService;

    @Mock
    private ProjectMapper projectMapper;

    private ProjectServiceImpl projectService;

    // Test data for new createProject tests
    private CreateProjectRequest validRequest;
    private List<MultipartFile> validImages;
    private List<ImageUploadRequest> validMetadata;
    private Project mockProject;
    private ProjectResponse mockProjectResponse;

    // Test data for updateImage tests
    private UpdateImageRequest mockUpdateImageRequest;
    private Image existingImage;
    private ImageResponse mockImageResponse;

    @BeforeEach
    void setUp() {
        projectService = new ProjectServiceImpl(projectRepository, imageRepository,
                imageStorageService, projectMapper);

        // Setup test data for new createProject tests
        setupTestData();
    }

    private void setupTestData() {
        // Setup valid request
        validRequest = new CreateProjectRequest(
                "Test Project", "Test Description", LocalDate.now(),
                WorkType.FACADE_CLEANING, CustomerType.PRIVATE_CUSTOMER
        );

        // Setup valid images
        validImages = new ArrayList<>();
        validImages.add(new MockMultipartFile("before", "before.jpg", "image/jpeg", "before content".getBytes()));
        validImages.add(new MockMultipartFile("after", "after.jpg", "image/jpeg", "after content".getBytes()));

        // Setup valid metadata
        validMetadata = new ArrayList<>();
        ImageUploadRequest beforeMetadata = new ImageUploadRequest();
        beforeMetadata.setImageType(ImageType.BEFORE);
        beforeMetadata.setFeatured(false);
        validMetadata.add(beforeMetadata);

        ImageUploadRequest afterMetadata = new ImageUploadRequest();
        afterMetadata.setImageType(ImageType.AFTER);
        afterMetadata.setFeatured(true);
        validMetadata.add(afterMetadata);

        // Setup mock project
        mockProject = new Project();
        mockProject.setId(1L);
        mockProject.setTitle("Test Project");
        mockProject.setDescription("Test Description");
        mockProject.setCreationDate(LocalDate.now());
        mockProject.setExecutionDate(LocalDate.now());
        mockProject.setWorkType(WorkType.FACADE_CLEANING);
        mockProject.setCustomerType(CustomerType.PRIVATE_CUSTOMER);

        // Setup mock project response
        mockProjectResponse = new ProjectResponse();
        mockProjectResponse.setId(1L);
        mockProjectResponse.setTitle("Test Project");
        mockProjectResponse.setDescription("Test Description");

        // Setup mock image
        existingImage = new Image();
        existingImage.setId(10L);
        existingImage.setUrl("/uploads/old.jpg");
        existingImage.setImageType(ImageType.BEFORE);
        existingImage.setIsFeatured(false);

        // Setup mock image list
        mockProject.setImages(List.of(existingImage));
        existingImage.setProject(mockProject);

        // Setup mock update image request
        mockUpdateImageRequest = new UpdateImageRequest();
        mockUpdateImageRequest.setId(10L);

        // Setup mock image response
        mockImageResponse = new ImageResponse();
        mockImageResponse.setId(10L);
        mockImageResponse.setUrl("/uploads/new.jpg");
        mockImageResponse.setImageType(ImageType.AFTER);
        mockImageResponse.setIsFeatured(true);

    }

    // ========================================
    // NEW TESTS FOR createProject WITH IMAGES
    // ========================================

    @Test
    void createProject_WithValidData_ShouldCreateProjectSuccessfully() {
        // Arrange
        when(projectMapper.toProjectEntity(validRequest)).thenReturn(mockProject);
        when(projectRepository.save(any(Project.class))).thenReturn(mockProject);
        when(imageStorageService.store(any(MultipartFile.class)))
                .thenReturn("http://storage.com/before.jpg")
                .thenReturn("http://storage.com/after.jpg");

        Image beforeImage = new Image();
        beforeImage.setId(1L);
        beforeImage.setUrl("http://storage.com/before.jpg");
        beforeImage.setImageType(ImageType.BEFORE);
        beforeImage.setIsFeatured(false);

        Image afterImage = new Image();
        afterImage.setId(2L);
        afterImage.setUrl("http://storage.com/after.jpg");
        afterImage.setImageType(ImageType.AFTER);
        afterImage.setIsFeatured(true);

        when(projectMapper.toImage(anyString(), eq(ImageType.BEFORE), eq(false), any(Project.class)))
                .thenReturn(beforeImage);
        when(projectMapper.toImage(anyString(), eq(ImageType.AFTER), eq(true), any(Project.class)))
                .thenReturn(afterImage);
        when(imageRepository.save(any(Image.class)))
                .thenReturn(beforeImage)
                .thenReturn(afterImage);
        when(projectMapper.toResponse(any(Project.class))).thenReturn(mockProjectResponse);

        // Act
        ProjectResponse result = projectService.createProject(validRequest, validImages, validMetadata);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Project", result.getTitle());
        verify(projectMapper).toProjectEntity(validRequest);
        verify(projectRepository).save(any(Project.class));
        verify(imageStorageService, times(2)).store(any(MultipartFile.class));
        verify(imageRepository, times(2)).save(any(Image.class));
        verify(projectMapper).toResponse(any(Project.class));
    }

    @Test
    void createProject_WithNullImages_ShouldThrowIllegalArgumentException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> projectService.createProject(validRequest, null, validMetadata));

        assertEquals("At least one image must be provided", exception.getMessage());
        verify(projectRepository, never()).save(any());
    }

    @Test
    void createProject_WithEmptyImages_ShouldThrowIllegalArgumentException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> projectService.createProject(validRequest, Collections.emptyList(), validMetadata));

        assertEquals("At least one image must be provided", exception.getMessage());
        verify(projectRepository, never()).save(any());
    }

    @Test
    void createProject_WithNullMetadata_ShouldThrowIllegalArgumentException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> projectService.createProject(validRequest, validImages, null));

        assertEquals("Image metadata must be provided", exception.getMessage());
        verify(projectRepository, never()).save(any());
    }

    @Test
    void createProject_WithMismatchedImageAndMetadataCount_ShouldThrowIllegalArgumentException() {
        // Arrange
        List<ImageUploadRequest> shortMetadata = new ArrayList<>();
        shortMetadata.add(validMetadata.get(0));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> projectService.createProject(validRequest, validImages, shortMetadata));

        assertEquals("Number of images must match number of metadata entries", exception.getMessage());
        verify(projectRepository, never()).save(any());
    }

    @Test
    void createProject_WithNoBeforeImage_ShouldThrowIllegalArgumentException() {
        // Arrange
        List<ImageUploadRequest> afterOnlyMetadata = new ArrayList<>();
        ImageUploadRequest afterMetadata1 = new ImageUploadRequest();
        afterMetadata1.setImageType(ImageType.AFTER);
        afterMetadata1.setFeatured(false);
        ImageUploadRequest afterMetadata2 = new ImageUploadRequest();
        afterMetadata2.setImageType(ImageType.AFTER);
        afterMetadata2.setFeatured(true);
        afterOnlyMetadata.add(afterMetadata1);
        afterOnlyMetadata.add(afterMetadata2);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> projectService.createProject(validRequest, validImages, afterOnlyMetadata));

        assertEquals("At least one BEFORE image must be provided", exception.getMessage());
        verify(projectRepository, never()).save(any());
    }

    @Test
    void createProject_WithNoAfterImage_ShouldThrowIllegalArgumentException() {
        // Arrange
        List<ImageUploadRequest> beforeOnlyMetadata = new ArrayList<>();
        ImageUploadRequest beforeMetadata1 = new ImageUploadRequest();
        beforeMetadata1.setImageType(ImageType.BEFORE);
        beforeMetadata1.setFeatured(false);
        ImageUploadRequest beforeMetadata2 = new ImageUploadRequest();
        beforeMetadata2.setImageType(ImageType.BEFORE);
        beforeMetadata2.setFeatured(true);
        beforeOnlyMetadata.add(beforeMetadata1);
        beforeOnlyMetadata.add(beforeMetadata2);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> projectService.createProject(validRequest, validImages, beforeOnlyMetadata));

        assertEquals("At least one AFTER image must be provided", exception.getMessage());
        verify(projectRepository, never()).save(any());
    }

    @Test
    void createProject_WhenStorageFails_ShouldCleanupAndThrowException() {
        // Arrange
        when(projectMapper.toProjectEntity(validRequest)).thenReturn(mockProject);
        when(projectRepository.save(any(Project.class))).thenReturn(mockProject);
        when(imageStorageService.store(any(MultipartFile.class)))
                .thenReturn("http://storage.com/before.jpg")
                .thenThrow(new RuntimeException("Storage failure"));

        Image beforeImage = new Image();
        beforeImage.setId(1L);
        beforeImage.setUrl("http://storage.com/before.jpg");
        beforeImage.setImageType(ImageType.BEFORE);

        when(projectMapper.toImage(anyString(), eq(ImageType.BEFORE), eq(false), any(Project.class)))
                .thenReturn(beforeImage);
        when(imageRepository.save(any(Image.class))).thenReturn(beforeImage);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> projectService.createProject(validRequest, validImages, validMetadata));

        assertTrue(exception.getMessage().contains("Failed to store images"));
        verify(imageStorageService).delete("http://storage.com/before.jpg");
    }

    @Test
    void createProject_WithMultipleBeforeAndAfterImages_ShouldSucceed() {
        // Arrange
        List<MultipartFile> multipleImages = new ArrayList<>();
        multipleImages.add(new MockMultipartFile("before1", "before1.jpg", "image/jpeg", "content".getBytes()));
        multipleImages.add(new MockMultipartFile("before2", "before2.jpg", "image/jpeg", "content".getBytes()));
        multipleImages.add(new MockMultipartFile("after1", "after1.jpg", "image/jpeg", "content".getBytes()));
        multipleImages.add(new MockMultipartFile("after2", "after2.jpg", "image/jpeg", "content".getBytes()));

        List<ImageUploadRequest> multipleMetadata = new ArrayList<>();
        ImageUploadRequest meta1 = new ImageUploadRequest();
        meta1.setImageType(ImageType.BEFORE);
        meta1.setFeatured(false);
        ImageUploadRequest meta2 = new ImageUploadRequest();
        meta2.setImageType(ImageType.BEFORE);
        meta2.setFeatured(false);
        ImageUploadRequest meta3 = new ImageUploadRequest();
        meta3.setImageType(ImageType.AFTER);
        meta3.setFeatured(true);
        ImageUploadRequest meta4 = new ImageUploadRequest();
        meta4.setImageType(ImageType.AFTER);
        meta4.setFeatured(false);
        multipleMetadata.add(meta1);
        multipleMetadata.add(meta2);
        multipleMetadata.add(meta3);
        multipleMetadata.add(meta4);

        when(projectMapper.toProjectEntity(validRequest)).thenReturn(mockProject);
        when(projectRepository.save(any(Project.class))).thenReturn(mockProject);
        when(imageStorageService.store(any(MultipartFile.class)))
                .thenReturn("url1", "url2", "url3", "url4");
        when(projectMapper.toImage(anyString(), any(ImageType.class), anyBoolean(), any(Project.class)))
                .thenReturn(new Image());
        when(imageRepository.save(any(Image.class))).thenReturn(new Image());
        when(projectMapper.toResponse(any(Project.class))).thenReturn(mockProjectResponse);

        // Act
        ProjectResponse result = projectService.createProject(validRequest, multipleImages, multipleMetadata);

        // Assert
        assertNotNull(result);
        verify(imageStorageService, times(4)).store(any(MultipartFile.class));
        verify(imageRepository, times(4)).save(any(Image.class));
    }

    // ========================================
    // EXISTING TESTS - UNCHANGED
    // ========================================

    // ---- TDD tests for update ----



    // Image not found
    @Test
    void updateImage_throwsException_whenImageNotFound() {

        // Given
        when(imageRepository.findById(10L)).thenReturn(Optional.empty());

        // When + then
        assertThrows(RuntimeException.class, () -> projectService.updateImage(10L, mockUpdateImageRequest));

        verify(imageRepository, times(1)).findById(10L);
        verifyNoMoreInteractions(imageStorageService);
    }

    // When URL is not changed, keep the old
    @Test
    void updateImage_shouldNotDeleteFile_whenUrlIsUnchanged() {

        // Given
        mockUpdateImageRequest.setUrl("/uploads/old.jpg"); // Same URL as in test setup()
        when(imageRepository.findById(10L)).thenReturn(Optional.of(existingImage));
        when(projectMapper.toImageResponse(existingImage)).thenReturn(mockImageResponse);

        // When
        ImageResponse result = projectService.updateImage(10L, mockUpdateImageRequest);

        // Then
        verify(imageStorageService, never()).delete(anyString());
        verify(projectMapper).updateImageEntity(mockUpdateImageRequest, existingImage);
        verify(imageRepository).save(existingImage);
        assertNotNull(result);

    }

    // When URL is changed, replace it
    @Test
    void updateImage_shouldDeleteOldFile_whenUrlIsChanged() {

        // Given
        mockUpdateImageRequest.setUrl("/uploads/new.jpg");
        when(imageRepository.findById(10L)).thenReturn(Optional.of(existingImage));
        when(projectMapper.toImageResponse(existingImage)).thenReturn(mockImageResponse);

        // When
        projectService.updateImage(10L, mockUpdateImageRequest);

        // Then
        verify(imageStorageService).delete("/uploads/old.jpg");
        verify(projectMapper).updateImageEntity(mockUpdateImageRequest, existingImage);
        verify(imageRepository).save(existingImage);
    }

    // Update metadata
    @Test
    void updateImage_shouldUpdateMetadata() {

        // Given
        mockUpdateImageRequest.setUrl("/uploads/old.jpg");
        mockUpdateImageRequest.setImageType(ImageType.AFTER);
        mockUpdateImageRequest.setIsFeatured(true);

        when(imageRepository.findById(10L)).thenReturn(Optional.of(existingImage));
        when(projectMapper.toImageResponse(existingImage)).thenReturn(mockImageResponse);

        // When
        projectService.updateImage(10L, mockUpdateImageRequest);

        // Then
        verify(projectMapper).updateImageEntity(mockUpdateImageRequest, existingImage);
    }

    // Ignore null fields
    @Test
    void updateImage_shouldIgnoreNullFields() {

        // Given
        mockUpdateImageRequest.setUrl(null);
        mockUpdateImageRequest.setImageType(null);
        mockUpdateImageRequest.setIsFeatured(null);

        when(imageRepository.findById(10L)).thenReturn(Optional.of(existingImage));
        when(projectMapper.toImageResponse(existingImage)).thenReturn(mockImageResponse);

        // When
        projectService.updateImage(10L, mockUpdateImageRequest);

        // Then
        verify(projectMapper).updateImageEntity(mockUpdateImageRequest, existingImage);
        verify(imageStorageService, never()).delete(anyString());
    }

    // Save and return the mapped DTO
    @Test
    void updateImage_shouldReturnMappedResponse() {

        // Given
        mockUpdateImageRequest.setUrl("/uploads/new.jpg");

        when(imageRepository.findById(10L)).thenReturn(Optional.of(existingImage));
        when(projectMapper.toImageResponse(existingImage)).thenReturn(mockImageResponse);

        // When
        ImageResponse result = projectService.updateImage(10L, mockUpdateImageRequest);

        // Then
        assertEquals(10L, result.getId());
        verify(imageRepository).save(existingImage);
    }

    // ---- TDD tests for delete ----


    // Delete project and images
    @Test
    void deleteProject_shouldDeleteAllImagesAndProject() {

        existingImage.setUrl("/uploads/old.jpg");
        mockProject.setImages(List.of(existingImage));

        when(projectRepository.findById(1L)).thenReturn(Optional.of(mockProject));

        projectService.deleteProject(1L);

        verify(imageStorageService).delete("/uploads/old.jpg");
        verify(projectRepository).delete(mockProject);
    }



    // Throw exception if no project is found
    @Test
    void deleteProject_shouldThrowException_whenProjectNotFound() {

        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                projectService.deleteProject(1L)
        );

        verify(imageStorageService, never()).delete(anyString());
        verify(projectRepository, never()).delete(any());
    }



    // ---- TDD tests ends ----


    @Test
    @DisplayName("updateProject - throws ResourceNotFoundException when project not found")
    void updateProject_WithInvalidId_ThrowsResourceNotFoundException() {
        // Arrange
        UpdateProjectRequest request = new UpdateProjectRequest();
        request.setTitle("new title");

        when(projectRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> projectService.updateProject(999L, request),
                "Expected ResourceNotFoundException when project not found");

        verify(projectRepository).findById(999L);
        verify(projectMapper, never()).updateProjectEntity(any(), any());
        verify(projectRepository, never()).save(any());
    }


    @Test
    @DisplayName("getProjectById - success")
    void getProjectById_WithValidId_ReturnsProjectResponse() {
        // Arrange
        Project mockProject = new Project();
        mockProject.setId(1L);
        mockProject.setTitle("Test Project");
        mockProject.setDescription("Test Description");
        mockProject.setCreationDate(LocalDate.now());
        mockProject.setExecutionDate(LocalDate.of(2025, 1, 4));
        mockProject.setWorkType(WorkType.FACADE_CLEANING);
        mockProject.setCustomerType(CustomerType.PRIVATE_CUSTOMER);

        ProjectResponse mockResponse = new ProjectResponse();
        mockResponse.setId(1L);
        mockResponse.setTitle("Test Project");
        mockResponse.setDescription("Test Description");

        when(projectRepository.findById(1L)).thenReturn(Optional.of(mockProject));
        when(projectMapper.toResponse(mockProject)).thenReturn(mockResponse);

        // Act
        ProjectResponse result = projectService.getProjectById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Project", result.getTitle());
        verify(projectRepository).findById(1L);
        verify(projectMapper).toResponse(mockProject);
    }

    @Test
    @DisplayName("getProjectById - throws ResourceNotFoundException when project not found")
    void getProjectById_WithInvalidId_ThrowsResourceNotFoundException() {
        // Arrange
        when(projectRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> projectService.getProjectById(999L));

        assertTrue(exception.getMessage().contains("Project"));
        assertTrue(exception.getMessage().contains("999"));
        verify(projectRepository).findById(999L);
        verify(projectMapper, never()).toResponse(any());
    }


    @Test
    @DisplayName("getAllProjects - success")
    void getAllProjects_ReturnsListOfProjectResponses() {
        // Arrange
        Project project1 = new Project();
        project1.setId(1L);
        project1.setTitle("Project 1");
        project1.setDescription("Description 1");

        Project project2 = new Project();
        project2.setId(2L);
        project2.setTitle("Project 2");
        project2.setDescription("Description 2");

        ProjectResponse response1 = new ProjectResponse();
        response1.setId(1L);
        response1.setTitle("Project 1");

        ProjectResponse response2 = new ProjectResponse();
        response2.setId(2L);
        response2.setTitle("Project 2");

        when(projectRepository.findAll()).thenReturn(List.of(project1, project2));
        when(projectMapper.toResponse(project1)).thenReturn(response1);
        when(projectMapper.toResponse(project2)).thenReturn(response2);

        // Act
        List<ProjectResponse> result = projectService.getAllProjects();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
        verify(projectRepository).findAll();
        verify(projectMapper).toResponse(project1);
        verify(projectMapper).toResponse(project2);
    }

    @Test
    @DisplayName("getAllProjects - returns empty list when no projects exist")
    void getAllProjects_NoProjectsExist_ReturnsEmptyList() {
        // Arrange
        when(projectRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<ProjectResponse> result = projectService.getAllProjects();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(projectRepository).findAll();
        verify(projectMapper, never()).toResponse(any());
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

    @Test
    @DisplayName("addImagesToProject - Success")
    void addImagesToProject_Success() {
        // Arrange
        Project project = new Project();
        project.setId(1L);
        project.setImages(new ArrayList<>());

        List<MultipartFile> images = List.of(
                new MockMultipartFile("img", "img.jpg", "image/jpeg", "content".getBytes())
        );

        List<ImageUploadRequest> metadata = List.of(
                new ImageUploadRequest(ImageType.BEFORE, false)
        );

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(imageStorageService.store(any())).thenReturn("/uploads/img.jpg");
        when(projectMapper.toImage(anyString(), any(), anyBoolean(), any())).thenReturn(new Image());
        when(imageRepository.save(any())).thenReturn(new Image());
        when(projectMapper.toResponse(any())).thenReturn(new ProjectResponse());

        // Act
        ProjectResponse result = projectService.addImagesToProject(1L, images, metadata);

        // Assert
        assertNotNull(result);
        verify(imageRepository).save(any());
    }

    @Test
    @DisplayName("addImagesToProject - Project Not Found")
    void addImagesToProject_ProjectNotFound_ThrowsException() {
        // Arrange
        when(projectRepository.findById(999L)).thenReturn(Optional.empty());

        List<MultipartFile> images = List.of(
                new MockMultipartFile("img", "img.jpg", "image/jpeg", "content".getBytes())
        );

        List<ImageUploadRequest> metadata = List.of(
                new ImageUploadRequest(ImageType.BEFORE, false)
        );

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> projectService.addImagesToProject(999L, images, metadata));
    }

    @Test
    @DisplayName("updateImageMetadata - Success")
    void updateImageMetadata_Success() {
        // Arrange
        Project project = new Project();
        project.setId(1L);

        Image image = new Image();
        image.setId(1L);
        image.setProject(project);

        UpdateImageRequest request = new UpdateImageRequest();
        request.setImageType(ImageType.AFTER);
        request.setIsFeatured(true);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(imageRepository.findById(1L)).thenReturn(Optional.of(image));
        when(imageRepository.save(any())).thenReturn(image);
        when(projectMapper.toResponse(any())).thenReturn(new ProjectResponse());

        // Act
        ProjectResponse result = projectService.updateImageMetadata(1L, 1L, request);

        // Assert
        assertNotNull(result);
        verify(projectMapper).updateImageEntity(request, image);
        verify(imageRepository).save(image);
    }

    @Test
    @DisplayName("updateImageMetadata - Image Not Found")
    void updateImageMetadata_ImageNotFound() {
        // Arrange
        Project project = new Project();
        project.setId(1L);

        UpdateImageRequest request = new UpdateImageRequest();

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(imageRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> projectService.updateImageMetadata(1L, 999L, request));
    }

    @Test
    @DisplayName("deleteImageFromProject - Success")
    void deleteImageFromProject_Success() {
        // Arrange
        Project project = new Project();
        project.setId(1L);

        Image beforeImage1 = new Image();
        beforeImage1.setId(1L);
        beforeImage1.setImageType(ImageType.BEFORE);
        beforeImage1.setUrl("/uploads/before1.jpg");
        beforeImage1.setProject(project);

        Image beforeImage2 = new Image();
        beforeImage2.setId(2L);
        beforeImage2.setImageType(ImageType.BEFORE);
        beforeImage2.setUrl("/uploads/before2.jpg");
        beforeImage2.setProject(project);

        Image afterImage = new Image();
        afterImage.setId(3L);
        afterImage.setImageType(ImageType.AFTER);
        afterImage.setUrl("/uploads/after.jpg");
        afterImage.setProject(project);

        List<Image> images = new ArrayList<>();
        images.add(beforeImage1);
        images.add(beforeImage2);
        images.add(afterImage);
        project.setImages(images);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(imageRepository.findById(2L)).thenReturn(Optional.of(beforeImage2));
        when(projectMapper.toResponse(project)).thenReturn(new ProjectResponse());

        // Act
        ProjectResponse result = projectService.deleteImageFromProject(1L, 2L);

        // Assert
        assertNotNull(result);
        verify(imageStorageService).delete("/uploads/before2.jpg");
        verify(imageRepository).delete(beforeImage2);
    }

    @Test
    @DisplayName("deleteImageFromProject - Image Not Found")
    void deleteImageFromProject_ImageNotFound() {
        // Arrange
        Project project = new Project();
        project.setId(1L);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(imageRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> projectService.deleteImageFromProject(1L, 999L));

        verify(imageRepository, never()).delete(any());
    }
}