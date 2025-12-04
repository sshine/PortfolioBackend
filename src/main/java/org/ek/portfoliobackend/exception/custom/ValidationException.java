package org.ek.portfoliobackend.exception.custom;

/**
 * Exception der kastes ved business logic valideringsfejl.
 * Resulterer i HTTP 400 Bad Request response.

 * Bruges til custom validering i service laget, f.eks.:
 * - Projekt navn allerede eksisterer
 * - Manglende billeder på projekt
 */

public class ValidationException extends RuntimeException {

    /**
     * Constructor med valideringsfejl besked.

     * @param message Beskrivelse af valideringsfejlen
     */
    public ValidationException(String message) {
        super(message);
    }
}