package org.ek.portfoliobackend.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.ek.portfoliobackend.exception.custom.ResourceNotFoundException;
import org.ek.portfoliobackend.exception.custom.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Global exception handler der fanger alle exceptions og returnerer strukturerede fejlresponses.
 *
 * Denne klasse bruger @RestControllerAdvice til at centralisere al fejlhåndtering i applikationen.
 * Alle exceptions fra controllers fanges her og konverteres til pæne HTTP responses med ErrorResponse format.
 *
 * Logger bruges til at logge fejl for udviklere, mens ErrorResponse sendes til frontend/klienter.
 *
 * @see ErrorResponse
 * @see org.ek.portfoliobackend.exception.custom
 */

@RestControllerAdvice // Fanger ALLE exceptions fra ALLE controllers
public class GlobalExceptionHandler {

    // Logger til at skrive fejl til console og log filer (til udviklere)
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    /**
     * Håndterer ResourceNotFoundException når en ressource ikke findes i databasen.

     * @param ex Exception med besked om hvilken ressource der mangler
     * @param request HTTP request for at få path information
     * @return ResponseEntity med ErrorResponse og HTTP 404 status
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex, HttpServletRequest request) {


        logger.warn("Resource not found: {} - Path {}", ex.getMessage(), request.getRequestURI());
        // Format "Resource not found: Project med id 42 blev ikke fundet - Path: /api/projects/42"

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Håndterer ValidationException ved valideringsfejl i business logic.

     * @param ex Exception med valideringsfejl besked
     * @param request HTTP request for at få path information
     * @return ResponseEntity med ErrorResponse og HTTP 400 status
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            ValidationException ex, HttpServletRequest request) {

        logger.warn("Validation error: {} - Path: {}", ex.getMessage(), request.getRequestURI());
        // Format: "Validation error: Projekt navn må ikke være tomt - Path: /api/projects"

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation Error",
                ex.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Håndterer MethodArgumentNotValidException - Bean Validation fejl fra @Valid annotation.
     * Samler alle feltspecifikke valideringsfejl og returnerer dem i en struktureret format.

     * @param ex Exception med binding result og alle valideringsfejl
     * @param request HTTP request for at få path information
     * @return ResponseEntity med ErrorResponse inklusiv validationErrors map og HTTP 400 status
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentValidException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        // Map til at holde felt specifikke valideringsfejl (fieldName -> errorMessage)
        Map<String, String> validationErrors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            validationErrors.put(fieldName, errorMessage);
        });

        // Sammensæt alle validationErrors til én string til logging (f.eks. "name=må ikke være tom, billeder=skal indeholde før og efter")
        String fieldSummary = validationErrors.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining(", "));

        logger.warn("Validation failed for {} - Fields: {}", request.getRequestURI(), fieldSummary);
        // Format: "Validation failed for /api/projects - Fields: {name=må ikke være tom, billeder=skal indeholde før og efter}"

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation Failed",
                "Input validation fejlede for et eller flere felter",
                request.getRequestURI()
        );

        errorResponse.setValidationErrors(validationErrors);

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Håndterer IllegalArgumentException når metoder modtager ugyldige argumenter.

     * @param ex Exception med besked om hvad der er forkert
     * @param request HTTP request for at få path information
     * @return ResponseEntity med ErrorResponse og HTTP 400 status
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex, HttpServletRequest request) {

        logger.warn("Invalid argument: {} - Path: {}", ex.getMessage(), request.getRequestURI());

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Invalid argument",
                ex.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Håndterer MethodArgumentTypeMismatchException når query parameters har ugyldige enum værdier.
     *
     * Returnerer HTTP 400 Bad Request med information om:
     * - Hvilken parameter der er ugyldig
     * - Hvad den ugyldige værdi var
     * - Hvilke værdier der er tilladte
     *
     * Eksempel: GET /api/projects?workType=INVALID_TYPE
     *
     * @param ex Exception med type mismatch information
     * @param request HTTP request for at få path information
     * @return ResponseEntity med ErrorResponse inklusiv allowedValues og HTTP 400 status
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {

        logger.warn("Invalid parameter type - parameter: '{}', value: '{}', expectedType: '{}' - Path: {}",
                ex.getName(),
                ex.getValue(),
                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown",
                request.getRequestURI());
        // Format: "Invalid parameter type - parameter: 'workType', value: 'INVALID_TYPE', expectedType: 'WorkType' - Path: /api/projects"

        String message = buildEnumErrorMessage(ex);

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Invalid Parameter",
                message,
                request.getRequestURI()
        );

        // Tilføj ekstra information hvis det er en enum
        if (ex.getRequiredType() != null && ex.getRequiredType().isEnum()) {
            Map<String, Object> additionalInfo = new HashMap<>();
            additionalInfo.put("parameter", ex.getName());
            additionalInfo.put("invalidValue", ex.getValue());

            Object[] enumConstants = ex.getRequiredType().getEnumConstants();
            String allowedValues = Arrays.stream(enumConstants)
                    .map(Object::toString)
                    .collect(Collectors.joining(", "));
            additionalInfo.put("allowedValues", allowedValues);

            errorResponse.setAdditionalInfo(additionalInfo);
        }

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Bygger en brugervenlig fejlbesked for enum type mismatch.
     *
     * @param ex Exception med type mismatch information
     * @return Formateret fejlbesked med tilladte værdier
     */
    private String buildEnumErrorMessage(MethodArgumentTypeMismatchException ex) {
        if (ex.getRequiredType() != null && ex.getRequiredType().isEnum()) {
            Object[] enumConstants = ex.getRequiredType().getEnumConstants();
            String allowedValues = Arrays.stream(enumConstants)
                    .map(Object::toString)
                    .collect(Collectors.joining(", "));

            return String.format(
                    "Ugyldig værdi '%s' for parameter '%s'. Tilladte værdier er: [%s]",
                    ex.getValue(),
                    ex.getName(),
                    allowedValues
            );
        }

        return String.format(
                "Ugyldig værdi '%s' for parameter '%s'",
                ex.getValue(),
                ex.getName()
        );
    }

    /**
     * Håndterer alle andre uventede exceptions (HTTP 500).

     * SIKKERHED: Denne metode viser IKKE ex.getMessage() til brugeren, da det kan indeholde
     * sensitive tekniske detaljer (database paths, stack traces, etc.).
     * I stedet returneres en generisk fejlbesked.

     * Den fulde fejl med stack trace logges kun til server logs hvor udviklere kan se den.

     * @param ex Den uventede exception
     * @param request HTTP request for at få path information
     * @return ResponseEntity med generisk ErrorResponse og HTTP 500 status
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex, HttpServletRequest request) {

        logger.error("Unexpected error at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        // Format: "ERROR: Unexpected error at /api/projects: Connection refused" + STACK TRACE (ex)

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "En uventet fejl opstod. Kontakt support hvis problemet fortsætter",
                request.getRequestURI()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}