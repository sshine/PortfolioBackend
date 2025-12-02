package org.ek.portfoliobackend.repository;

import org.ek.portfoliobackend.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for ImageRepository.
 * 
 * Tester alle query metoder inklusiv:
 * - Filtrering efter image type
 * - Filtrering efter featured status
 * - Kombinerede queries
 * - Project relation
 */

@ActiveProfiles("test")
@DisplayName("ImageRepository Integration Tests")
class ImageRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private ProjectRepository projectRepository;

    private Project testProject;
    private Image beforeImage1;
    private Image beforeImage2;
    private Image afterImage1;
    private Image afterImage2;
    private Image featuredImage;

    /**
     * Setup køres før hver test.
     * Opretter test data med forskellige image types og featured status.
     */
    @BeforeEach
    void setUp() {
        // Ryd database
        imageRepository.deleteAll();
        projectRepository.deleteAll();
        entityManager.flush();

        // Opret test projekt
        testProject = createProject("Test Project", "Test Description");
        entityManager.persistAndFlush(testProject);

        // Opret test images med forskellige combinations
        beforeImage1 = createImage("before1.jpg", ImageType.BEFORE, false, testProject);
        beforeImage2 = createImage("before2.jpg", ImageType.BEFORE, false, testProject);
        afterImage1 = createImage("after1.jpg", ImageType.AFTER, false, testProject);
        afterImage2 = createImage("after2.jpg", ImageType.AFTER, true, testProject);
        featuredImage = createImage("featured.jpg", ImageType.BEFORE, true, testProject);

        entityManager.persistAndFlush(beforeImage1);
        entityManager.persistAndFlush(beforeImage2);
        entityManager.persistAndFlush(afterImage1);
        entityManager.persistAndFlush(afterImage2);
        entityManager.persistAndFlush(featuredImage);
    }

    /**
     * Test: Verificer at alle BEFORE billeder kan findes.
     */
    @Test
    @DisplayName("Should find all images by BEFORE type")
    void findByImageType_ShouldReturnAllBeforeImages() {
        // Act
        List<Image> beforeImages = imageRepository.findByImageType(ImageType.BEFORE);

        // Assert
        assertThat(beforeImages).hasSize(3);
        assertThat(beforeImages)
                .extracting(Image::getImageType)
                .containsOnly(ImageType.BEFORE);
        assertThat(beforeImages)
                .extracting(Image::getUrl)
                .contains("before1.jpg", "before2.jpg", "featured.jpg");
    }

    /**
     * Test: Verificer at alle AFTER billeder kan findes.
     */
    @Test
    @DisplayName("Should find all images by AFTER type")
    void findByImageType_ShouldReturnAllAfterImages() {
        // Act
        List<Image> afterImages = imageRepository.findByImageType(ImageType.AFTER);

        // Assert
        assertThat(afterImages).hasSize(2);
        assertThat(afterImages)
                .extracting(Image::getImageType)
                .containsOnly(ImageType.AFTER);
        assertThat(afterImages)
                .extracting(Image::getUrl)
                .contains("after1.jpg", "after2.jpg");
    }

    /**
     * Test: Verificer at alle featured billeder kan findes.
     */
    @Test
    @DisplayName("Should find all featured images")
    void findByIsFeatured_ShouldReturnAllFeaturedImages() {
        // Act
        List<Image> featuredImages = imageRepository.findByIsFeatured(true);

        // Assert
        assertThat(featuredImages).hasSize(2);
        assertThat(featuredImages)
                .extracting(Image::isFeatured)
                .containsOnly(true);
        assertThat(featuredImages)
                .extracting(Image::getUrl)
                .contains("featured.jpg", "after2.jpg");
    }

    /**
     * Test: Verificer at alle non-featured billeder kan findes.
     */
    @Test
    @DisplayName("Should find all non-featured images")
    void findByIsFeatured_ShouldReturnAllNonFeaturedImages() {
        // Act
        List<Image> nonFeaturedImages = imageRepository.findByIsFeatured(false);

        // Assert
        assertThat(nonFeaturedImages).hasSize(3);
        assertThat(nonFeaturedImages)
                .extracting(Image::isFeatured)
                .containsOnly(false);
    }

    /**
     * Test: Verificer kombineret query - featured BEFORE images.
     * 
     * Nyttigt til at finde hero images for projekt oversigt.
     */
    @Test
    @DisplayName("Should find images by both imageType and featured status")
    void findByImageTypeAndIsFeatured_ShouldReturnMatchingImages() {
        // Act - Find featured BEFORE images
        List<Image> featuredBeforeImages = imageRepository
                .findByImageTypeAndIsFeatured(ImageType.BEFORE, true);

        // Assert
        assertThat(featuredBeforeImages).hasSize(1);
        assertThat(featuredBeforeImages.get(0).getUrl()).isEqualTo("featured.jpg");
        assertThat(featuredBeforeImages.get(0).getImageType()).isEqualTo(ImageType.BEFORE);
        assertThat(featuredBeforeImages.get(0).isFeatured()).isTrue();
    }

    /**
     * Test: Verificer kombineret query - non-featured AFTER images.
     */
    @Test
    @DisplayName("Should find non-featured AFTER images")
    void findByImageTypeAndIsFeatured_ShouldFindNonFeaturedAfterImages() {
        // Act
        List<Image> nonFeaturedAfterImages = imageRepository
                .findByImageTypeAndIsFeatured(ImageType.AFTER, false);

        // Assert
        assertThat(nonFeaturedAfterImages).hasSize(1);
        assertThat(nonFeaturedAfterImages.get(0).getUrl()).isEqualTo("after1.jpg");
    }

    /**
     * Test: Verificer at tom liste returneres når ingen billeder matcher.
     */
    @Test
    @DisplayName("Should return empty list when no images match criteria")
    void findByImageTypeAndIsFeatured_ShouldReturnEmptyListWhenNoMatch() {
        // Arrange - Slet alle billeder og opret kun non-featured BEFORE
        imageRepository.deleteAll();
        Image nonFeaturedBefore = createImage("test.jpg", ImageType.BEFORE, false, testProject);
        entityManager.persistAndFlush(nonFeaturedBefore);

        // Act - Søg efter featured AFTER images (ingen eksisterer)
        List<Image> result = imageRepository
                .findByImageTypeAndIsFeatured(ImageType.AFTER, true);

        // Assert
        assertThat(result).isEmpty();
    }

    /**
     * Test: Verificer at alle billeder for et projekt kan findes.
     */
    @Test
    @DisplayName("Should find all images by project ID")
    void findByProjectId_ShouldReturnAllImagesForProject() {
        // Act
        List<Image> projectImages = imageRepository.findByProjectId(testProject.getId());

        // Assert
        assertThat(projectImages).hasSize(5);
        assertThat(projectImages)
                .extracting(image -> image.getProject().getId())
                .containsOnly(testProject.getId());
    }

    /**
     * Test: Verificer at tom liste returneres når projekt ikke har billeder.
     */
    @Test
    @DisplayName("Should return empty list when project has no images")
    void findByProjectId_ShouldReturnEmptyListWhenProjectHasNoImages() {
        // Arrange - Opret projekt uden billeder
        Project emptyProject = createProject("Empty Project", "No images");
        entityManager.persistAndFlush(emptyProject);

        // Act
        List<Image> images = imageRepository.findByProjectId(emptyProject.getId());

        // Assert
        assertThat(images).isEmpty();
    }

    /**
     * Test: Verificer at tom liste returneres når projekt ikke eksisterer.
     */
    @Test
    @DisplayName("Should return empty list when project does not exist")
    void findByProjectId_ShouldReturnEmptyListWhenProjectDoesNotExist() {
        // Act - Søg efter non-eksisterende projekt
        List<Image> images = imageRepository.findByProjectId(99999L);

        // Assert
        assertThat(images).isEmpty();
    }

    /**
     * Test: Verificer at image kan gemmes med alle felter korrekt.
     */
    @Test
    @DisplayName("Should persist image with all fields correctly")
    void save_ShouldPersistImageWithAllFields() {
        // Arrange
        Image newImage = createImage("new-image.jpg", ImageType.BEFORE, true, testProject);

        // Act
        Image savedImage = imageRepository.save(newImage);
        entityManager.flush();

        // Assert
        assertThat(savedImage.getId()).isNotNull();
        assertThat(savedImage.getUrl()).isEqualTo("new-image.jpg");
        assertThat(savedImage.getImageType()).isEqualTo(ImageType.BEFORE);
        assertThat(savedImage.isFeatured()).isTrue();
        assertThat(savedImage.getProject()).isEqualTo(testProject);
    }

    /**
     * Test: Verificer at image kan slettes via ID.
     */
    @Test
    @DisplayName("Should delete image by id")
    void deleteById_ShouldRemoveImage() {
        // Arrange
        Long imageId = beforeImage1.getId();

        // Act
        imageRepository.deleteById(imageId);
        entityManager.flush();

        // Assert
        assertThat(imageRepository.findById(imageId)).isEmpty();
        assertThat(imageRepository.findAll()).hasSize(4);
    }

    /**
     * Test: Verificer cascade delete når projekt slettes.
     * 
     * Når et projekt slettes, skal alle tilhørende billeder også slettes
     * pga. orphanRemoval = true i Project entity.
     */
    @Test
    @DisplayName("Should cascade delete images when project is deleted")
    void delete_ShouldCascadeDeleteImagesWhenProjectDeleted() {
        // Arrange
        Long projectId = testProject.getId();
        int imageCount = imageRepository.findByProjectId(projectId).size();
        assertThat(imageCount).isEqualTo(5); // Verificer vi har 5 images

        // Act
        projectRepository.deleteById(projectId);
        entityManager.flush();
        entityManager.clear(); // Clear persistence context for fresh query

        // Assert
        assertThat(imageRepository.findByProjectId(projectId)).isEmpty();
    }

    /**
     * Helper metode til at oprette test projekter.
     */
    private Project createProject(String title, String description) {
        Project project = new Project();
        project.setTitle(title);
        project.setDescription(description);
        project.setExecutionDate(LocalDate.now());
        project.setCreationDate(LocalDate.now());
        project.setServiceCategory(ServiceCategory.FACADE_CLEANING);
        project.setCustomerType(CustomerType.BUSINESS_CUSTOMER);
        return project;
    }

    /**
     * Helper metode til at oprette test images.
     */
    private Image createImage(String url, ImageType imageType, boolean isFeatured, Project project) {
        Image image = new Image();
        image.setUrl(url);
        image.setImageType(imageType);
        image.setFeatured(isFeatured);
        image.setProject(project);
        return image;
    }
}
