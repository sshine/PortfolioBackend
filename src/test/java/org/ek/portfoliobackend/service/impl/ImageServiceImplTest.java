package org.ek.portfoliobackend.service.impl;

import org.ek.portfoliobackend.dto.request.ImageUploadRequest;
import org.ek.portfoliobackend.model.ImageType;
import org.ek.portfoliobackend.repository.ImageRepository;
import org.ek.portfoliobackend.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for ImageServiceImpl to verify that unimplemented methods throw UnsupportedOperationException.
 */

@ExtendWith(MockitoExtension.class)
public class ImageServiceImplTest {

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private ProjectRepository projectRepository;

    private ImageServiceImpl imageService;

    //(in-project) dummy photo path for testing
    private final String dummyPhoto = Paths.get("src/main/resources/uploads/dummy_photo.png").toUri().toString();


    @BeforeEach
    void setUp() {
        imageService = new ImageServiceImpl(imageRepository, projectRepository);
    }

    @Test
    void uploadImage_throwsUnsupportedOperationException() {
        ImageUploadRequest request = new ImageUploadRequest(ImageType.BEFORE, false);

        assertThatThrownBy(() -> imageService.uploadImage(1L, dummyPhoto, request)) // by running this method
                .isInstanceOf(UnsupportedOperationException.class) // expect this exception
                .hasMessage("Not implemented yet"); // with this message
    }

    @Test
    void getImageById_throwsUnsupportedOperationException() {
        assertThatThrownBy(() -> imageService.getImageById(1L))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessage("Not implemented yet");
    }

    @Test
    void getImagesByProjectId_throwsUnsupportedOperationException() {
        assertThatThrownBy(() -> imageService.getImagesByProjectId(1L))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessage("Not implemented yet");
    }

    @Test
    void deleteImage_throwsUnsupportedOperationException() {
        assertThatThrownBy(() -> imageService.deleteImage(1L))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessage("Not implemented yet");
    }

    @Test
    void setFeatured_throwsUnsupportedOperationException() {
        assertThatThrownBy(() -> imageService.setFeatured(1L, true))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessage("Not implemented yet");
    }

    @Test
    void getFeaturedImages_throwsUnsupportedOperationException() {
        assertThatThrownBy(() -> imageService.getFeaturedImages())
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessage("Not implemented yet");
    }

    @Test
    void getFeaturedImagesByProjectId_throwsUnsupportedOperationException() {
        assertThatThrownBy(() -> imageService.getFeaturedImagesByProjectId(1L))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessage("Not implemented yet");
    }

    @Test
    void getImagesByType_throwsUnsupportedOperationException() {
        assertThatThrownBy(() -> imageService.getImagesByType(ImageType.BEFORE))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessage("Not implemented yet");
    }
}