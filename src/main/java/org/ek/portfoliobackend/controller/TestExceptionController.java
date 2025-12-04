package org.ek.portfoliobackend.controller;

import jakarta.validation.Valid;
import org.ek.portfoliobackend.exception.custom.ResourceNotFoundException;
import org.ek.portfoliobackend.exception.custom.ValidationException;
import org.springframework.web.bind.annotation.*;

/**
 * MIDLERTIDIG test controller til at verificere exception handling.
 * SLET DENNE FIL efter test!
 */
@RestController
@RequestMapping("/api/test/exceptions")
public class TestExceptionController {

    /**
     * Test ResourceNotFoundException (404)
     * GET http://localhost:8080/api/test/exceptions/not-found
     */
    @GetMapping("/not-found")
    public String testResourceNotFound() {
        throw new ResourceNotFoundException("Test Project", 999L);
    }

    /**
     * Test ValidationException (400)
     * GET http://localhost:8080/api/test/exceptions/validation
     */
    @GetMapping("/validation")
    public String testValidation() {
        throw new ValidationException("Test: Projekt skal have mindst ét før- og ét efter-billede");
    }

    /**
     * Test IllegalArgumentException (400)
     * GET http://localhost:8080/api/test/exceptions/illegal-argument
     */
    @GetMapping("/illegal-argument")
    public String testIllegalArgument() {
        throw new IllegalArgumentException("Test: Ugyldig kategori - vælg facade, fliser, tag, vinduer eller trapper");
    }

    /**
     * Test uventet exception (500)
     * GET http://localhost:8080/api/test/exceptions/unexpected
     */
    @GetMapping("/unexpected")
    public String testUnexpectedException() {
        throw new RuntimeException("Test: Simuleret database connection fejl");
    }

    /**
     * Test Bean Validation med @Valid (400)
     * POST http://localhost:8080/api/test/exceptions/validation-dto
     * Body: {"name": "", "description": "kort"}
     */
    @PostMapping("/validation-dto")
    public String testBeanValidation(@Valid @RequestBody TestProjectDTO dto) {
        return "Success: " + dto.getName();
    }

    /**
     * Simpel test DTO til Bean Validation test
     */
    public static class TestProjectDTO {

        @jakarta.validation.constraints.NotBlank(message = "Projektnavn må ikke være tomt")
        private String name;

        @jakarta.validation.constraints.Size(min = 10, max = 500, message = "Beskrivelse skal være mellem 10 og 500 tegn")
        private String description;

        // Getters og Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
}