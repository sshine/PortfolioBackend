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
 * Integration tests for ProjectRepository.
 *
 * @DataJpaTest konfigurerer:
 * - H2 in-memory database
 * - Spring Data JPA repositories
 * - TestEntityManager til database setup
 * - Transactional tests (rollback efter hver test)
 *
 * @ActiveProfiles("test") sikrer at application-test.properties bruges
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("ProjectRepository Integration Tests")
class ProjectRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProjectRepository projectRepository;

    private Project project1;
    private Project project2;
    private Project project3;

    /**
     * Setup køres før hver test.
     * Opretter test data med forskellige creation dates.
     */
    @BeforeEach
    void setUp() {
        // Ryd database før hver test
        projectRepository.deleteAll();
        entityManager.flush();

        // Opret test projekter med forskellige creation dates
        project1 = createProject(
                "Oldest Project",
                "First project created",
                LocalDate.now().minusDays(10),
                LocalDate.now().minusDays(10),
                WorkType.FACADE_CLEANING,
                CustomerType.BUSINESS_CUSTOMER
        );

        project2 = createProject(
                "Middle Project",
                "Second project created",
                LocalDate.now().minusDays(5),
                LocalDate.now().minusDays(5),
                WorkType.ROOF_CLEANING,
                CustomerType.PRIVATE_CUSTOMER
        );

        project3 = createProject(
                "Newest Project",
                "Third project created",
                LocalDate.now(),
                LocalDate.now(),
                WorkType.PAVING_CLEANING,
                CustomerType.BUSINESS_CUSTOMER
        );

        // Persist til database
        entityManager.persistAndFlush(project1);
        entityManager.persistAndFlush(project2);
        entityManager.persistAndFlush(project3);
    }

    /**
     * Test: Verificer at projekter sorteres korrekt med nyeste først.
     *
     * Forventet resultat:
     * - 3 projekter returneres
     * - Nyeste projekt er først i listen
     * - Ældste projekt er sidst i listen
     */
    @Test
    @DisplayName("Should find all projects sorted by creation date descending (newest first)")
    void findAllByOrderByCreationDateDesc_ShouldReturnNewestFirst() {
        // Act - Hent projekter sorteret nyeste først
        List<Project> projects = projectRepository.findAllByOrderByCreationDateDesc();

        // Assert - Verificer sortering
        assertThat(projects).hasSize(3);
        assertThat(projects.get(0).getTitle()).isEqualTo("Newest Project");
        assertThat(projects.get(1).getTitle()).isEqualTo("Middle Project");
        assertThat(projects.get(2).getTitle()).isEqualTo("Oldest Project");
    }

    /**
     * Test: Verificer at projekter sorteres korrekt med ældste først.
     *
     * Nyttigt for historisk visning eller arkiv funktionalitet.
     */
    @Test
    @DisplayName("Should find all projects sorted by creation date ascending (oldest first)")
    void findAllByOrderByCreationDateAsc_ShouldReturnOldestFirst() {
        // Act - Hent projekter sorteret ældste først
        List<Project> projects = projectRepository.findAllByOrderByCreationDateAsc();

        // Assert - Verificer sortering
        assertThat(projects).hasSize(3);
        assertThat(projects.get(0).getTitle()).isEqualTo("Oldest Project");
        assertThat(projects.get(1).getTitle()).isEqualTo("Middle Project");
        assertThat(projects.get(2).getTitle()).isEqualTo("Newest Project");
    }

    /**
     * Test: Verificer at tom liste returneres når database er tom.
     *
     * Edge case: Sikrer at query ikke fejler på tom database.
     */
    @Test
    @DisplayName("Should return empty list when no projects exist (DESC)")
    void findAllByOrderByCreationDateDesc_ShouldReturnEmptyListWhenNoProjects() {
        // Arrange - Slet alle projekter
        projectRepository.deleteAll();

        // Act
        List<Project> projects = projectRepository.findAllByOrderByCreationDateDesc();

        // Assert
        assertThat(projects).isEmpty();
    }

    /**
     * Test: Verificer at tom liste returneres for ASC sortering.
     */
    @Test
    @DisplayName("Should return empty list when no projects exist (ASC)")
    void findAllByOrderByCreationDateAsc_ShouldReturnEmptyListWhenNoProjects() {
        // Arrange - Slet alle projekter
        projectRepository.deleteAll();

        // Act
        List<Project> projects = projectRepository.findAllByOrderByCreationDateAsc();

        // Assert
        assertThat(projects).isEmpty();
    }

    /**
     * Test: Verificer at projekter med samme creation date håndteres korrekt.
     *
     * Edge case: Når flere projekter oprettes samme dag.
     */
    @Test
    @DisplayName("Should handle projects with same creation date")
    void findAllByOrderByCreationDateDesc_ShouldHandleSameCreationDate() {
        // Arrange - Opret ekstra projekter med samme dato
        LocalDate today = LocalDate.now();
        Project project4 = createProject(
                "Same Date Project 1",
                "First with same date",
                today,
                today,
                WorkType.WOODEN_DECK_CLEANING,
                CustomerType.PRIVATE_CUSTOMER
        );
        Project project5 = createProject(
                "Same Date Project 2",
                "Second with same date",
                today,
                today,
                WorkType.FACADE_CLEANING,
                CustomerType.BUSINESS_CUSTOMER
        );

        entityManager.persistAndFlush(project4);
        entityManager.persistAndFlush(project5);

        // Act
        List<Project> projects = projectRepository.findAllByOrderByCreationDateDesc();

        // Assert - Verificer at alle projekter returneres
        assertThat(projects).hasSizeGreaterThanOrEqualTo(5);
        assertThat(projects)
                .extracting(Project::getTitle)
                .contains("Same Date Project 1", "Same Date Project 2");
    }

    /**
     * Test: Verificer at projekt kan gemmes med alle felter korrekt.
     *
     * Tester JPA persistence funktionalitet.
     */
    @Test
    @DisplayName("Should persist project with all fields correctly")
    void save_ShouldPersistProjectWithAllFields() {
        // Arrange
        Project newProject = createProject(
                "New Project",
                "Test description",
                LocalDate.now(),
                LocalDate.now(),
                WorkType.ROOF_CLEANING,
                CustomerType.PRIVATE_CUSTOMER
        );

        // Act
        Project savedProject = projectRepository.save(newProject);
        entityManager.flush();

        // Assert
        assertThat(savedProject.getId()).isNotNull();
        assertThat(savedProject.getTitle()).isEqualTo("New Project");
        assertThat(savedProject.getDescription()).isEqualTo("Test description");
        assertThat(savedProject.getWorkType()).isEqualTo(WorkType.ROOF_CLEANING);
        assertThat(savedProject.getCustomerType()).isEqualTo(CustomerType.PRIVATE_CUSTOMER);
    }

    /**
     * Test: Verificer at projekt kan slettes via ID.
     *
     * Tester delete funktionalitet.
     */
    @Test
    @DisplayName("Should delete project by id")
    void deleteById_ShouldRemoveProject() {
        // Arrange
        Long projectId = project1.getId();

        // Act
        projectRepository.deleteById(projectId);
        entityManager.flush();

        // Assert
        assertThat(projectRepository.findById(projectId)).isEmpty();
        assertThat(projectRepository.findAll()).hasSize(2);
    }

    /**
     * Helper metode til at oprette test projekter.
     *
     * @return Project entity klar til at gemmes
     */
    private Project createProject(String title, String description,
                                  LocalDate executionDate, LocalDate creationDate,
                                  WorkType workType, CustomerType customerType) {
        Project project = new Project();
        project.setTitle(title);
        project.setDescription(description);
        project.setExecutionDate(executionDate);
        project.setCreationDate(creationDate);
        project.setWorkType(workType);
        project.setCustomerType(customerType);
        return project;
    }
}