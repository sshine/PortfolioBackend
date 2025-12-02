package org.ek.portfoliobackend.exception.custom;

/**
 * Kastes ved valideringsfejl (resulterer i HTTP 400 Bad Request)
 */

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
