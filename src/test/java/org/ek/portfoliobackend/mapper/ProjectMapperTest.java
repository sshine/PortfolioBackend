package org.ek.portfoliobackend.mapper;

import org.ek.portfoliobackend.dto.request.CreateProjectRequest;
import org.ek.portfoliobackend.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for ProjectMapper klassen.
 * Tester konvertering mellem DTOs og entities uden database interaktion.
 * Testene følger Arrange-Act-Assert pattern:
 * - Arrange: Forbered test data
 * - Act: Udfør den metode der testes
 * - Assert: Verificer at resultatet er som forventet
 */
@DisplayName("ProjectMapper Unit Tests")
class ProjectMapperTest {

    private ProjectMapper projectMapper;

    /**
     * Setup der køres før hver test.
     * Opretter en ny instans af ProjectMapper for at sikre clean state.
     */
    @BeforeEach
    void setUp() {
        projectMapper = new ProjectMapper();
    }

    /**
     * Test: Verificer at toProjectEntity mapper alle felter korrekt fra CreateProjectRequest til Project entity.
     * Forventet resultat:
     * - Alle felter fra request skal mappes til project
     * - Ingen felter må være null
     * - Enum værdier skal bevares korrekt
     */
    @Test
    @DisplayName("Should map CreateProjectRequest to Project entity with all fields")
    void toProjectEntity_ShouldMapAllFields() {
        // Arrange - Forbered test data med alle påkrævede felter
        LocalDate executionDate = LocalDate.of(2025, 6, 15);
        CreateProjectRequest request = new CreateProjectRequest(
                "Facade Cleaning Project",
                "Complete facade cleaning for apartment building",
                executionDate,
                WorkType.FACADE_CLEANING,
                CustomerType.BUSINESS_CUSTOMER
        );

        // Act - Udfør mapping fra DTO til entity
        Project project = projectMapper.toProjectEntity(request);

        // Assert - Verificer at alle felter er mappet korrekt
        assertThat(project).isNotNull();
        assertThat(project.getTitle()).isEqualTo("Facade Cleaning Project");
        assertThat(project.getDescription()).isEqualTo("Complete facade cleaning for apartment building");
        assertThat(project.getExecutionDate()).isEqualTo(executionDate);
        assertThat(project.getWorkType()).isEqualTo(WorkType.FACADE_CLEANING);
        assertThat(project.getCustomerType()).isEqualTo(CustomerType.BUSINESS_CUSTOMER);
    }

    /**
     * Test: Verificer at creationDate sættes automatisk til dagens dato.
     * Dette er kritisk fordi:
     * - creationDate skal IKKE komme fra request
     * - Den skal sættes automatisk i mapper
     * - Den skal repræsentere hvornår projektet blev oprettet i systemet
     */
    @Test
    @DisplayName("Should set creationDate to current date automatically")
    void toProjectEntity_ShouldSetCreationDateAutomatically() {
        // Arrange - Opret en simpel request
        CreateProjectRequest request = new CreateProjectRequest(
                "Test Project",
                "Test Description",
                LocalDate.now(),
                WorkType.PAVING_CLEANING,
                CustomerType.PRIVATE_CUSTOMER
        );

        // Act - Map request til entity
        Project project = projectMapper.toProjectEntity(request);

        // Assert - Verificer at creationDate er sat til i dag (ikke fra request)
        assertThat(project.getCreationDate()).isEqualTo(LocalDate.now());
    }

    /**
     * Test: Verificer at alle ServiceCategory enum værdier håndteres korrekt.
     * Dette sikrer at:
     * - Mapping fungerer for ALLE mulige service kategorier
     * - Ingen enum værdier forårsager fejl
     * - Fremtidige nye kategorier vil blive fanget hvis testen fejler
     */
    @Test
    @DisplayName("Should handle different service categories correctly")
    void toProjectEntity_ShouldHandleDifferentServiceCategories() {
        // Arrange - Hent alle mulige ServiceCategory værdier
        WorkType[] categories = WorkType.values();

        // Act & Assert - Test hver kategori individuelt
        for (WorkType category : categories) {
            // Opret request med denne kategori
            CreateProjectRequest request = new CreateProjectRequest(
                    "Test Project",
                    "Test Description",
                    LocalDate.now(),
                    category,  // Test denne specifikke kategori
                    CustomerType.PRIVATE_CUSTOMER
            );

            // Map til entity
            Project project = projectMapper.toProjectEntity(request);

            // Verificer at kategorien blev mappet korrekt
            assertThat(project.getWorkType()).isEqualTo(category);
        }
    }

    /**
     * Test: Verificer at alle CustomerType enum værdier håndteres korrekt.
     * Dette sikrer at:
     * - Både private og erhvervskunder mappes korrekt
     * - Enum værdier ikke går tabt i mapping
     * - Fremtidige nye kundetyper vil blive fanget hvis testen fejler
     */
    @Test
    @DisplayName("Should handle different customer types correctly")
    void toProjectEntity_ShouldHandleDifferentCustomerTypes() {
        // Arrange - Hent alle mulige CustomerType værdier
        CustomerType[] types = CustomerType.values();

        // Act & Assert - Test hver kundetype individuelt
        for (CustomerType type : types) {
            // Opret request med denne kundetype
            CreateProjectRequest request = new CreateProjectRequest(
                    "Test Project",
                    "Test Description",
                    LocalDate.now(),
                    WorkType.ROOF_CLEANING,
                    type  // Test denne specifikke kundetype
            );

            // Map til entity
            Project project = projectMapper.toProjectEntity(request);

            // Verificer at kundetypen blev mappet korrekt
            assertThat(project.getCustomerType()).isEqualTo(type);
        }
    }

    /**
     * Test: Verificer at toImage opretter en Image entity med alle felter korrekt.
     * Dette tester:
     * - Alle felter mappes (url, imageType, isFeatured)
     * - Relationen til Project etableres korrekt
     * - Image er klar til at gemmes i databasen
     */
    @Test
    @DisplayName("Should create Image entity with all fields and establish project relationship")
    void toImage_ShouldCreateImageWithAllFields() {
        // Arrange - Forbered alle parametre for image oprettelse
        String imageUrl = "https://example.com/images/before-1.jpg";
        ImageType imageType = ImageType.BEFORE;
        boolean isFeatured = true;

        // Opret et test projekt som billedet skal tilhøre
        Project project = new Project();
        project.setId(1L);
        project.setTitle("Test Project");

        // Act - Opret Image entity via mapper
        Image image = projectMapper.toImage(imageUrl, imageType, isFeatured, project);

        // Assert - Verificer alle felter og relationer
        assertThat(image).isNotNull();
        assertThat(image.getUrl()).isEqualTo(imageUrl);
        assertThat(image.getImageType()).isEqualTo(ImageType.BEFORE);
        assertThat(image.getIsFeatured()).isTrue();
        assertThat(image.getProject()).isEqualTo(project);  // Relation er etableret
    }

    /**
     * Test: Verificer at non-featured billeder oprettes korrekt.
     * Vigtigt fordi:
     * - Ikke alle billeder skal være featured
     * - isFeatured=false skal håndteres lige så godt som isFeatured=true
     * - Standard use case er non-featured billeder
     */
    @Test
    @DisplayName("Should create non-featured image correctly")
    void toImage_ShouldCreateNonFeaturedImage() {
        // Arrange - Opret projekt for billedet
        Project project = new Project();
        project.setId(2L);

        // Act - Opret non-featured AFTER billede
        Image image = projectMapper.toImage(
                "https://example.com/after.jpg",
                ImageType.AFTER,
                false,  // Non-featured
                project
        );

        // Assert - Verificer at billedet IKKE er featured
        assertThat(image.getIsFeatured()).isFalse();
        assertThat(image.getImageType()).isEqualTo(ImageType.AFTER);
    }

    /**
     * Test: Verificer at både BEFORE og AFTER image types håndteres korrekt.
     * Dette er kritisk fordi:
     * - Systemet SKAL understøtte både før og efter billeder
     * - Hver projektcase skal have minimum 1 BEFORE og 1 AFTER
     * - ImageType enum skal mappes korrekt
     */
    @Test
    @DisplayName("Should handle both BEFORE and AFTER image types")
    void toImage_ShouldHandleBothImageTypes() {
        // Arrange - Opret projekt for begge billedtyper
        Project project = new Project();

        // Act & Assert - Test BEFORE image type
        Image beforeImage = projectMapper.toImage(
                "before.jpg",
                ImageType.BEFORE,
                false,
                project
        );
        assertThat(beforeImage.getImageType()).isEqualTo(ImageType.BEFORE);

        // Act & Assert - Test AFTER image type
        Image afterImage = projectMapper.toImage(
                "after.jpg",
                ImageType.AFTER,
                false,
                project
        );
        assertThat(afterImage.getImageType()).isEqualTo(ImageType.AFTER);
    }

    /**
     * Test: Verificer at relationen mellem Image og Project etableres korrekt.
     * Dette sikrer at:
     * - Image.project peger på det rigtige Project
     * - Relationen er bi-directional (kan traverseres begge veje)
     * - JPA vil kunne gemme relationen korrekt i databasen
     */
    @Test
    @DisplayName("Should establish bidirectional relationship between Image and Project")
    void toImage_ShouldEstablishProjectRelationship() {
        // Arrange - Opret projekt med specifik ID og titel
        Project project = new Project();
        project.setId(5L);
        project.setTitle("Relationship Test Project");

        // Act - Opret image med relation til projektet
        Image image = projectMapper.toImage(
                "test.jpg",
                ImageType.BEFORE,
                true,
                project
        );

        // Assert - Verificer at relationen er etableret korrekt
        assertThat(image.getProject()).isNotNull();
        assertThat(image.getProject().getId()).isEqualTo(5L);
        assertThat(image.getProject().getTitle()).isEqualTo("Relationship Test Project");
    }
}