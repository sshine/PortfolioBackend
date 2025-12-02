package org.ek.portfoliobackend.service;

import org.ek.portfoliobackend.repository.ImageRepository;
import org.ek.portfoliobackend.repository.ProjectRepository;
import org.ek.portfoliobackend.service.impl.ProjectServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test to verify that the service layer and repositories are correctly wired in the Spring context.
 */

@SpringBootTest
public class ServiceLayerIntegrationTest {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Test
    void contextLoads() {
        // verify Spring context loads with all required beans
    }

    @Test
    void projectServiceIsInjected() {
        assertThat(projectService).isNotNull();
        assertThat(projectService).isInstanceOf(ProjectServiceImpl.class);
    }

    @Test
    void imageServiceIsInjected() {
        assertThat(imageService).isNotNull();
        assertThat(imageService).isInstanceOf(ImageService.class);
    }

    @Test
    void repositoriesAreInjected() {
        assertThat(projectRepository).isNotNull();
        assertThat(imageRepository).isNotNull();
    }
}
