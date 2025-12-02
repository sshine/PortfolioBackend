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
@DisplayName("ProjectRepository Tests")
class ProjectRepositoryTest {

    @Autowired
    private ProjectRepository projectRepository;

    @BeforeEach
    void setUp() {
        projectRepository.deleteAll();
    }

    @Test
    @DisplayName("Should find all projects sorted by creation date descending")
    void testFindAllByOrderByCreationDateDesc() {
        Project project1 = createAndSaveProject("Oldest", LocalDate.now().minusDays(10));
        Project project2 = createAndSaveProject("Middle", LocalDate.now().minusDays(5));
        Project project3 = createAndSaveProject("Newest", LocalDate.now());

        List<Project> projects = projectRepository.findAllByOrderByCreationDateDesc();

        assertThat(projects).hasSize(3);
        assertThat(projects.get(0).getTitle()).isEqualTo("Newest");
        assertThat(projects.get(1).getTitle()).isEqualTo("Middle");
        assertThat(projects.get(2).getTitle()).isEqualTo("Oldest");
    }

    @Test
    @DisplayName("Should find all projects sorted by creation date ascending")
    void testFindAllByOrderByCreationDateAsc() {
        createAndSaveProject("Oldest", LocalDate.now().minusDays(10));
        createAndSaveProject("Middle", LocalDate.now().minusDays(5));
        createAndSaveProject("Newest", LocalDate.now());

        List<Project> projects = projectRepository.findAllByOrderByCreationDateAsc();

        assertThat(projects).hasSize(3);
        assertThat(projects.get(0).getTitle()).isEqualTo("Oldest");
        assertThat(projects.get(1).getTitle()).isEqualTo("Middle");
        assertThat(projects.get(2).getTitle()).isEqualTo("Newest");
    }

    @Test
    @DisplayName("Should return empty list when no projects exist")
    void testEmptyList() {
        List<Project> projects = projectRepository.findAllByOrderByCreationDateDesc();
        assertThat(projects).isEmpty();
    }

    @Test
    @DisplayName("Should save project with all fields")
    void testSaveProject() {
        Project project = new Project();
        project.setTitle("Test Project");
        project.setDescription("Test Description");
        project.setExecutionDate(LocalDate.now());
        project.setCreationDate(LocalDate.now());
        project.setWorkType(WorkType.FACADE_CLEANING);
        project.setCustomerType(CustomerType.PRIVATE_CUSTOMER);

        Project saved = projectRepository.save(project);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTitle()).isEqualTo("Test Project");
        assertThat(saved.getWorkType()).isEqualTo(WorkType.FACADE_CLEANING);
    }

    @Test
    @DisplayName("Should delete project by id")
    void testDeleteProject() {
        Project project = createAndSaveProject("To Delete", LocalDate.now());
        Long id = project.getId();

        projectRepository.deleteById(id);

        assertThat(projectRepository.findById(id)).isEmpty();
    }

    private Project createAndSaveProject(String title, LocalDate date) {
        Project project = new Project();
        project.setTitle(title);
        project.setDescription("Test description");
        project.setExecutionDate(date);
        project.setCreationDate(date);
        project.setWorkType(WorkType.FACADE_CLEANING);
        project.setCustomerType(CustomerType.PRIVATE_CUSTOMER);
        return projectRepository.save(project);
    }
}