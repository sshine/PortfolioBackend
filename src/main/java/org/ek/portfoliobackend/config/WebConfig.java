package org.ek.portfoliobackend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/*
Konfiguration for at håndtering af statiske filer. I vores tilfælde kun billeder, da frontend har sige eget repo.
Denne klasse fortæller Spring Boot hvor den skal finde og gemme statiske filer, så de kan findes frem via URL'er i browseren.
 */

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // Henter upload stien fra applications.properties
    @Value("${file.upload-dir}")
    private String uploadDir;

    // Henter static fil stien fra applications.properties
    @Value("${file.static-dir}")
    private String staticDir;

        /**
         * Konfigurerer URL mappings for statiske resourcer

         * Flow for billede upload og visning:
            1. Frontend sender billede til backend
            2. Backend gemmer billedet fysisk i uploads/ mappen (f.eks. "projekt123.jpg")
            3. Backend gemmer metadata i database (filename, filepath, uploadedAt, projectId, etc.)
            4. Frontend henter projekt data fra API (inkl. filepath: "/uploads/projekt123.jpg")
            5. Frontend viser billedet via URL: http://localhost:8080/uploads/projekt123.jpg
            6. WebConfig sørger for at Spring Boot finder og server filen fra uploads/ mappen

         Bemærk:    - Databasen gemmer kun information OM filen (metadata).
                    - Den fysiske fil ligger i uploads/ mappen.
                    - WebConfig gør det muligt at tilgå den fysiske fil via URL.

         Prøv at kør applikationen og gå ind på linket http://localhost:8080/uploads/dummy_photo.png :)
         */

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Mapper URL path "/uploads/**" til den fysiske mappe defineret i uploadDir
        // ** betyder "alle undermapper og filer"
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadDir);

        // Mapper URL path "/static/**" til den fysiske static mappe
        registry.addResourceHandler("/static/**")
                .addResourceLocations("file:" + staticDir);
    }

}
