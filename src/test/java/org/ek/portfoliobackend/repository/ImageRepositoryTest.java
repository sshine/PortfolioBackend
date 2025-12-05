package org.ek.portfoliobackend.repository;

import org.ek.portfoliobackend.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("ImageRepository Tests")
class ImageRepositoryTest {

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private ProjectRepository projectRepository;

    private Project testProject1;
    private Project testProject2;

    @BeforeEach
    void setUp() {
        imageRepository.deleteAll();
        projectRepository.deleteAll();

        testProject1 = createAndSaveProject("Test Project 1");
        testProject2 = createAndSaveProject("Test Project 2");
    }

    @Test
    @DisplayName("Should find images by project")
    void testFindByProject() {
        createAndSaveImage(testProject1, ImageType.BEFORE, false);
        createAndSaveImage(testProject1, ImageType.AFTER, true);
        createAndSaveImage(testProject2, ImageType.BEFORE, false);

        List<Image> images = imageRepository.findByProject(testProject1);

        assertThat(images).hasSize(2);
        assertThat(images).allMatch(img -> img.getProject().equals(testProject1));
    }

    @Test
    @DisplayName("Should find images by project id")
    void testFindByProjectId() {
        createAndSaveImage(testProject1, ImageType.BEFORE, false);
        createAndSaveImage(testProject1, ImageType.AFTER, true);
        createAndSaveImage(testProject2, ImageType.BEFORE, false);

        List<Image> images = imageRepository.findByProjectId(testProject1.getId());

        assertThat(images).hasSize(2);
        assertThat(images).allMatch(img -> img.getProject().getId().equals(testProject1.getId()));
    }

    @Test
    @DisplayName("Should find featured images")
    void testFindByIsFeaturedTrue() {
        createAndSaveImage(testProject1, ImageType.BEFORE, false);
        createAndSaveImage(testProject1, ImageType.AFTER, true);
        createAndSaveImage(testProject2, ImageType.BEFORE, true);

        List<Image> images = imageRepository.findByIsFeaturedTrue();

        assertThat(images).hasSize(2);
        assertThat(images).allMatch(Image::getIsFeatured);
    }

    @Test
    @DisplayName("Should find non-featured images")
    void testFindByIsFeaturedFalse() {
        createAndSaveImage(testProject1, ImageType.BEFORE, false);
        createAndSaveImage(testProject1, ImageType.AFTER, true);
        createAndSaveImage(testProject2, ImageType.BEFORE, false);

        List<Image> images = imageRepository.findByIsFeaturedFalse();

        assertThat(images).hasSize(2);
        assertThat(images).allMatch(img -> !img.getIsFeatured());
    }

    @Test
    @DisplayName("Should find images by image type")
    void testFindByImageType() {
        createAndSaveImage(testProject1, ImageType.BEFORE, false);
        createAndSaveImage(testProject1, ImageType.AFTER, true);
        createAndSaveImage(testProject2, ImageType.BEFORE, false);

        List<Image> beforeImages = imageRepository.findByImageType(ImageType.BEFORE);
        List<Image> afterImages = imageRepository.findByImageType(ImageType.AFTER);

        assertThat(beforeImages).hasSize(2);
        assertThat(afterImages).hasSize(1);
        assertThat(beforeImages).allMatch(img -> img.getImageType() == ImageType.BEFORE);
        assertThat(afterImages).allMatch(img -> img.getImageType() == ImageType.AFTER);
    }

    @Test
    @DisplayName("Should find featured images by project id")
    void testFindByProjectIdAndIsFeaturedTrue() {
        createAndSaveImage(testProject1, ImageType.BEFORE, false);
        createAndSaveImage(testProject1, ImageType.AFTER, true);
        createAndSaveImage(testProject2, ImageType.BEFORE, true);

        List<Image> images = imageRepository.findByProjectIdAndIsFeaturedTrue(testProject1.getId());

        assertThat(images).hasSize(1);
        assertThat(images.get(0).getIsFeatured()).isTrue();
        assertThat(images.get(0).getProject().getId()).isEqualTo(testProject1.getId());
    }

    @Test
    @DisplayName("Should find images by project id and image type")
    void testFindByProjectIdAndImageType() {
        createAndSaveImage(testProject1, ImageType.BEFORE, false);
        createAndSaveImage(testProject1, ImageType.AFTER, true);
        createAndSaveImage(testProject1, ImageType.BEFORE, false);

        List<Image> beforeImages = imageRepository.findByProjectIdAndImageType(testProject1.getId(), ImageType.BEFORE);

        assertThat(beforeImages).hasSize(2);
        assertThat(beforeImages).allMatch(img -> img.getImageType() == ImageType.BEFORE);
        assertThat(beforeImages).allMatch(img -> img.getProject().getId().equals(testProject1.getId()));
    }

    @Test
    @DisplayName("Should save image with all fields")
    void testSaveImage() {
        Image image = new Image();
        image.setUrl("https://example.com/image.jpg");
        image.setImageType(ImageType.BEFORE);
        image.setIsFeatured(true);
        image.setProject(testProject1);

        Image saved = imageRepository.save(image);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUrl()).isEqualTo("https://example.com/image.jpg");
        assertThat(saved.getImageType()).isEqualTo(ImageType.BEFORE);
        assertThat(saved.getIsFeatured()).isTrue();
        assertThat(saved.getProject()).isEqualTo(testProject1);
    }

    @Test
    @DisplayName("Should delete image by id")
    void testDeleteImage() {
        Image image = createAndSaveImage(testProject1, ImageType.BEFORE, false);
        Long id = image.getId();

        imageRepository.deleteById(id);

        assertThat(imageRepository.findById(id)).isEmpty();
    }

    private Project createAndSaveProject(String title) {
        Project project = new Project();
        project.setTitle(title);
        project.setDescription("Test description");
        project.setExecutionDate(LocalDate.now());
        project.setCreationDate(LocalDate.now());
        project.setWorkType(WorkType.FACADE_CLEANING);
        project.setCustomerType(CustomerType.PRIVATE_CUSTOMER);
        return projectRepository.save(project);
    }

    private Image createAndSaveImage(Project project, ImageType imageType, boolean isFeatured) {
        Image image = new Image();
        image.setUrl("https://example.com/image_" + System.nanoTime() + ".jpg");
        image.setImageType(imageType);
        image.setIsFeatured(isFeatured);
        image.setProject(project);
        return imageRepository.save(image);
    }
}