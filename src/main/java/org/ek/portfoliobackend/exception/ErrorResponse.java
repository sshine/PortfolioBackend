package org.ek.portfoliobackend.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
* Standard fejlrespons format, som returneres til frontend ved alle fejl.
* Indeholder tracking ID, timestamp, HTTP status, fejlbesked og request path
*/

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL) // Skjuler null felter
public class ErrorResponse {

    private final String trackingId;
    private final LocalDateTime timestamp;
    private final int status;
    private final String error;
    private final String message;
    private final String path;

    @Setter
    Map<String, String> validationErrors;

    public ErrorResponse(int status, String error, String message, String path) {
        this.trackingId = UUID.randomUUID().toString(); // Unikt ID til at tracke fejlen i logs
        this.timestamp = LocalDateTime.now();           // Tidspunkt for fejlen
        this.status = status;                           // HTTP status kode (404, 500, etc.)
        this.error = error;                             // HTTP fejl tekst ("Not found", "Bad Request")
        this.message = message;                         // Brugervenlig fejlbesked
        this.path = path;                               // API endpoint hvor fejlen skete
    }

}
