package org.ek.portfoliobackend.exception.custom;

/**
 * Exception der kastes når en ressource ikke findes i databasen.
 * Resulterer i HTTP 404 Not Found response.

 * Understøtter tre forskellige constructor patterns for fleksibilitet:
 * - Custom besked
 * - Resource navn + ID (Long)
 * - Resource navn + identifier (String)
 */

public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructor med custom fejlbesked.

     * @param message Custom fejlbesked
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructor med resource navn og numerisk ID.
     * Genererer besked i format: "Project med id 42 blev ikke fundet"

     * @param resourceName Navn på ressourcen (f.eks. "Project", "Image")
     * @param id ID på ressourcen der ikke blev fundet
     */
    public ResourceNotFoundException(String resourceName, Long id) {
        super(String.format("%s med id %d blev ikke fundet", resourceName, id));
    }

    /**
     * Constructor med resource navn og string identifier.
     * Genererer besked i format: "Project 'AlgeNord Demo' blev ikke fundet"

     * @param resourceName Navn på ressourcen (f.eks. "Project", "User")
     * @param identifier String identifier (f.eks. navn, email)
     */
    public ResourceNotFoundException(String resourceName, String identifier) {
        super(String.format("%s '%s' blev ikke fundet", resourceName, identifier));
    }
}