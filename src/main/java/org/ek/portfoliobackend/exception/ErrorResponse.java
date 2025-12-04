package org.ek.portfoliobackend.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Standard fejlresponse format som returneres til frontend ved alle HTTP fejl.

 * Indeholder tracking ID til at spore fejl i logs, timestamp, HTTP status,
 * fejlbesked, request path og optionelle feltspecifikke valideringsfejl.

 * @JsonInclude sikrer at null felter ikke inkluderes i JSON response.
 */

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL) // Skjuler null felter
public class ErrorResponse {

    private final String trackingId;         // Unikt ID til at tracke fejlen i logs
    private final LocalDateTime timestamp;   // Tidspunkt for fejlen
    private final int status;                // HTTP status code (404, 400, 500, etc.)
    private final String error;              // HTTP status tekst ("Not Found", "Bad Request")
    private final String message;            // Brugervenlig fejlbesked
    private final String path;               // API endpoint hvor fejlen skete

    /**
     * Feltspecifikke valideringsfejl (fieldName -> errorMessage).

     * Sættes kun ved Bean Validation fejl fra @Valid annotation.
     * Gør det muligt for frontend at matche fejlbeskeder til specifikke input felter.

     * Eksempel: {"name": "må ikke være tomt", "billeder": "skal have før og efter"}

     * Null ved andre fejltyper og ekskluderes automatisk fra JSON response.
     * @see GlobalExceptionHandler#handleMethodArgumentValidException
     */
    @Setter
    private Map<String, String> validationErrors;


    /**
     * Constructor til at oprette en ErrorResponse.
     * Genererer automatisk tracking ID og timestamp.

     * @param status HTTP status code
     * @param error HTTP status tekst
     * @param message Brugervenlig fejlbesked
     * @param path API endpoint hvor fejlen skete
     */
    public ErrorResponse(int status, String error, String message, String path) {
        this.trackingId = UUID.randomUUID().toString();
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

}
