package org.ek.portfoliobackend.repository;

import org.ek.portfoliobackend.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for ImageRepository.
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("ImageRepository Integration Tests")
class ImageRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private ProjectRepository projectRepository;

    private Project project1;
    private Project project2;
    private Image beforeImage1;
    private Image afterImage1Featured;
    private Image beforeImage2;
    private Image afterImage2;

    @BeforeEach
    void setUp() {
        imageRepository.deleteAll();
        projectRepository.deleteAll();
        entityManager.flush();

        // Opret test projekter
        project1 = createProject(
                "Test Project 1",
                "First test project",
                WorkType.FACADE_CLEANING,
                CustomerType.PRIVATE_CUSTOMER
        );

        project2 = createProject(
                "Test Project 2",
                "Second test project",
                WorkType.ROOF_CLEANING,
                CustomerType.BUSINESS_CUSTOMER
        );

        entityManager.persistAndFlush(project1);
        entityManager.persistAndFlush(project2);

        // Opret test billeder for project1
        beforeImage1 = createImage(
                "/images/before1.jpg",
                ImageType.BEFORE,
                false,
                project1
        );

        afterImage1Featured = createImage(
                "/images/after1.jpg",
                ImageType.AFTER,
                true,
                project1
        );

        // Opret test billeder for project2
        beforeImage2 = createImage(
                "/images/before2.jpg",
                ImageType.BEFORE,
                false,
                project2
        );

        afterImage2 = createImage(
                "/images/after2.jpg",
                ImageType.AFTER,
                false,
                project2
        );

        entityManager.persistAndFlush(beforeImage1);
        entityManager.persistAndFlush(afterImage1Featured);
        entityManager.persistAndFlush(beforeImage2);
        entityManager.persistAndFlush(afterImage2);
    }

    @Test
    @DisplayName("Should find all images by project")
    void findByProject_ShouldReturnAllImagesForProject() {
        // Act
        List<Image> images = imageRepository.findByProject(project1);

        // Assert
        assertThat(images).hasSize(2);
        assertThat(images).extracting(Image::getUrl)
                .containsExactlyInAnyOrder("/images/before1.jpg", "/images/after1.jpg");
    }

    @Test
    @DisplayName("Should find all images by project ID")
    void findByProjectId_ShouldReturnAllImagesForProjectId() {
        // Act
        List<Image> images = imageRepository.findByProjectId(project1.getId());

        // Assert
        assertThat(images).hasSize(2);
        assertThat(images).extracting(Image::getUrl)
                .containsExactlyInAnyOrder("/images/before1.jpg", "/images/after1.jpg");
    }

    @Test
    @DisplayName("Should find only featured images")
    void findByIsFeaturedTrue_ShouldReturnOnlyFeaturedImages() {
        // Act
        List<Image> featuredImages = imageRepository.findByIsFeaturedTrue();

        // Assert
        assertThat(featuredImages).hasSize(1);
        assertThat(featuredImages.get(0).getUrl()).isEqualTo("/images/after1.jpg");
        assertThat(featuredImages.get(0).getIsFeatured()).isTrue();
    }

    @Test
    @DisplayName("Should find only non-featured images")
    void findByIsFeaturedFalse_ShouldReturnOnlyNonFeaturedImages() {
        // Act
        List<Image> nonFeaturedImages = imageRepository.findByIsFeaturedFalse();

        // Assert
        assertThat(nonFeaturedImages).hasSize(3);
        assertThat(nonFeaturedImages).allMatch(image -> !image.getIsFeatured());
    }

    @Test
    @DisplayName("Should find images by image type")
    void findByImageType_ShouldReturnImagesOfSpecificType() {
        // Act
        List<Image> beforeImages = imageRepository.findByImageType(ImageType.BEFORE);
        List<Image> afterImages = imageRepository.findByImageType(ImageType.AFTER);

        // Assert
        assertThat(beforeImages).hasSize(2);
        assertThat(beforeImages).allMatch(img -> img.getImageType() == ImageType.BEFORE);

        assertThat(afterImages).hasSize(2);
        assertThat(afterImages).allMatch(img -> img.getImageType() == ImageType.AFTER);
    }

    @Test
    @DisplayName("Should find featured images by project ID")
    void findByProjectIdAndIsFeaturedTrue_ShouldReturnFeaturedImagesForProject() {
        // Act
        List<Image> featuredImages = imageRepository.findByProjectIdAndIsFeaturedTrue(project1.getId());

        // Assert
        assertThat(featuredImages).hasSize(1);
        assertThat(featuredImages.get(0).getUrl()).isEqualTo("/images/after1.jpg");
        assertThat(featuredImages.get(0).getIsFeatured()).isTrue();
    }

    @Test
    @DisplayName("Should find non-featured images by project ID")
    void findByProjectIdAndIsFeaturedFalse_ShouldReturnNonFeaturedImagesForProject() {
        // Act
        List<Image> nonFeaturedImages = imageRepository.findByProjectIdAndIsFeaturedFalse(project1.getId());

        // Assert
        assertThat(nonFeaturedImages).hasSize(1);
        assertThat(nonFeaturedImages.get(0).getUrl()).isEqualTo("/images/before1.jpg");
        assertThat(nonFeaturedImages.get(0).getIsFeatured()).isFalse();
    }

    @Test
    @DisplayName("Should find images by type and featured status")
    void findByImageTypeAndIsFeatured_ShouldReturnMatchingImages() {
        // Act - find featured AFTER images
        List<Image> featuredAfterImages = imageRepository.findByImageTypeAndIsFeatured(ImageType.AFTER, true);

        // Assert
        assertThat(featuredAfterImages).hasSize(1);
        assertThat(featuredAfterImages.get(0).getImageType()).isEqualTo(ImageType.AFTER);
        assertThat(featuredAfterImages.get(0).getIsFeatured()).isTrue();
    }

    @Test
    @DisplayName("Should find images by project ID and image type")
    void findByProjectIdAndImageType_ShouldReturnMatchingImages() {
        // Act
        List<Image> beforeImages = imageRepository.findByProjectIdAndImageType(
                project1.getId(),
                ImageType.BEFORE
        );
        List<Image> afterImages = imageRepository.findByProjectIdAndImageType(
                project1.getId(),
                ImageType.AFTER
        );

        // Assert
        assertThat(beforeImages).hasSize(1);
        assertThat(beforeImages.get(0).getUrl()).isEqualTo("/images/before1.jpg");

        assertThat(afterImages).hasSize(1);
        assertThat(afterImages.get(0).getUrl()).isEqualTo("/images/after1.jpg");
    }

    @Test
    @DisplayName("Should return empty list for non-existent project")
    void findByProjectId_WithNonExistentProject_ShouldReturnEmptyList() {
        // Act
        List<Image> images = imageRepository.findByProjectId(999L);

        // Assert
        assertThat(images).isEmpty();
    }

    @Test
    @DisplayName("Should return empty list when no matches found")
    void findByImageTypeAndIsFeatured_WithNoMatches_ShouldReturnEmptyList() {
        // Act - try to find featured BEFORE images (none exist in test data)
        List<Image> featuredBeforeImages = imageRepository.findByImageTypeAndIsFeatured(ImageType.BEFORE, true);

        // Assert
        assertThat(featuredBeforeImages).isEmpty();
    }

    @Test
    @DisplayName("Should persist image with all fields correctly")
    void save_ShouldPersistImageWithAllFields() {
        // Arrange
        Image newImage = createImage(
                "/images/new.jpg",
                ImageType.BEFORE,
                true,
                project1
        );

        // Act
        Image savedImage = imageRepository.save(newImage);
        entityManager.flush();

        // Assert
        assertThat(savedImage.getId()).isNotNull();
        assertThat(savedImage.getUrl()).isEqualTo("/images/new.jpg");
        assertThat(savedImage.getImageType()).isEqualTo(ImageType.BEFORE);
        assertThat(savedImage.getIsFeatured()).isTrue();
        assertThat(savedImage.getProject().getId()).isEqualTo(project1.getId());
    }

    @Test
    @DisplayName("Should delete image by ID")
    void deleteById_ShouldRemoveImage() {
        // Arrange
        Long imageId = beforeImage1.getId();

        // Act
        imageRepository.deleteById(imageId);
        entityManager.flush();

        // Assert
        assertThat(imageRepository.findById(imageId)).isEmpty();
        assertThat(imageRepository.findAll()).hasSize(3);
    }

    // Helper methods
    private Project createProject(String title, String description,
                                  WorkType workType, CustomerType customerType) {
        Project project = new Project();
        project.setTitle(title);
        project.setDescription(description);
        project.setExecutionDate(LocalDate.now());
        project.setCreationDate(LocalDate.now());
        project.setWorkType(workType);
        project.setCustomerType(customerType);
        return project;
    }

    private Image createImage(String url, ImageType imageType, boolean isFeatured, Project project) {
        Image image = new Image();
        image.setUrl(url);
        image.setImageType(imageType);
        image.setIsFeatured(isFeatured);
        image.setProject(project);
        return image;
    }
}