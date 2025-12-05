# Exception Handling Documentation
## AlgeNord Portfolio Backend

Dette dokument beskriver exception handling systemet i AlgeNord Portfolio Backend - et digitalt porteføljeværktøj til præsentation af rengøringsprojekter.

---

## Pakkestruktur

```
src/main/java/org/ek/portfoliobackend/
└── exception/
    ├── ErrorResponse.java                 # Standard fejlformat til frontend
    ├── GlobalExceptionHandler.java        # Centraliseret exception handling
    └── custom/
        ├── ResourceNotFoundException.java # HTTP 404 - Projekt/billede ikke fundet
        └── ValidationException.java       # HTTP 400 - Valideringsfejl (manglende data)
```

---

## System Oversigt

**AlgeNord Portfolio består af:**
- **Admin interface** → AlgeNord kan logge ind, uploade projektcases, administrere portfolio
- **Kunde interface** → Potentielle kunder kan browse projekter (read-only, ingen login)
- **Projekt cases** → Før/efter billeder, beskrivelser, kategorier (facade, fliser, tag, osv.)

---

## Hvordan det virker

```
┌─────────────┐
│  Controller │ ← HTTP request (fra admin eller kunde)
└──────┬──────┘
       │
       ▼
┌─────────────┐
│   Service   │ ← Kaster exceptions når noget går galt
└──────┬──────┘   (projekt ikke fundet, manglende billeder, osv.)
       │
       ▼
┌──────────────────────────┐
│ GlobalExceptionHandler   │ ← Fanger alle exceptions
└──────┬───────────────────┘
       │
       ├─→ Logger (til AlgeNord team - console/fil for debugging)
       └─→ ErrorResponse (til frontend - JSON med fejlbesked)
```

**Flow:**
1. **Controller** modtager request (f.eks. hent projekt, upload billede)
2. **Service lag** kaster exception hvis noget går galt
3. **GlobalExceptionHandler** fanger automatisk exceptions
4. **Logger** skriver fejl til console/log filer (til debugging)
5. **ErrorResponse** sendes som JSON til frontend

---

## ErrorResponse

Standard fejlformat som sendes til frontend ved alle fejl.

### JSON Response Format

```json
{
  "trackingId": "a3d5c7e9-1234-5678",
  "timestamp": "2024-12-03T12:30:45",
  "status": 404,
  "error": "Not Found",
  "message": "Project med id 42 blev ikke fundet",
  "path": "/api/projects/42"
}
```

### Felter

| Felt | Type | Beskrivelse |
|------|------|-------------|
| `trackingId` | String (UUID) | Unikt ID til at spore fejlen i logs |
| `timestamp` | LocalDateTime | Tidspunkt for fejlen |
| `status` | int | HTTP status code (404, 400, 500) |
| `error` | String | HTTP status tekst ("Not Found", "Bad Request") |
| `message` | String | Brugervenlig fejlbesked på dansk |
| `path` | String | API endpoint hvor fejlen skete |
| `validationErrors` | Map<String, String> | (Valgfri) Feltspecifikke valideringsfejl |
| `additionalInfo` | Map<String, Object> | (Valgfri) Ekstra kontekst om fejlen |

### Validation Errors Eksempel

Når AlgeNord uploader et projekt med manglende eller ugyldige data:

```json
{
  "trackingId": "abc-123",
  "timestamp": "2024-12-03T12:30:45",
  "status": 400,
  "error": "Validation Failed",
  "message": "Input validation fejlede for et eller flere felter",
  "path": "/api/projects",
  "validationErrors": {
    "name": "Projektnavn må ikke være tomt",
    "category": "Kategori er påkrævet",
    "description": "Beskrivelse skal være mellem 10 og 500 tegn"
  }
}
```

### Additional Info Eksempel

Når en ugyldig enum værdi sendes i query parameter:

```json
{
  "trackingId": "xyz-456",
  "timestamp": "2025-12-04T17:02:02.409644",
  "status": 400,
  "error": "Invalid Parameter",
  "message": "Ugyldig værdi 'INVALID_TYPE' for parameter 'workType'. Tilladte værdier er: [PAVING_CLEANING, WOODEN_DECK_CLEANING, ROOF_CLEANING, FACADE_CLEANING]",
  "path": "/api/projects",
  "additionalInfo": {
    "parameter": "workType",
    "invalidValue": "INVALID_TYPE",
    "allowedValues": "PAVING_CLEANING, WOODEN_DECK_CLEANING, ROOF_CLEANING, FACADE_CLEANING"
  }
}
```

**Anvendelse af `additionalInfo`:**
- **Enum validering**: Viser tilladte værdier for dropdown/select felter
- **Avanceret fejl kontekst**: Kan indeholde lister, objekter, eller andre strukturerede data
- **Frontend integration**: Gør det let for frontend at vise brugervenlige fejlbeskeder
- **Debugging**: Giver ekstra information uden at overfylde `message` feltet

---

## GlobalExceptionHandler

Centraliseret exception handling der fanger alle exceptions.

### Exception Handlers

| Handler | HTTP Status | Anvendelse i AlgeNord |
|---------|-------------|------------|
| `handleResourceNotFoundException` | 404 Not Found | Projekt eller billede findes ikke |
| `handleValidationException` | 400 Bad Request | Manglende før/efter billeder, ugyldig kategori |
| `handleMethodArgumentValidException` | 400 Bad Request | Bean Validation fejler (@Valid) |
| `handleIllegalArgumentException` | 400 Bad Request | Ugyldige parametre |
| `handleMethodArgumentTypeMismatch` | 400 Bad Request | **NY:** Ugyldige enum værdier i query parameters |
| `handleGlobalException` | 500 Internal Server Error | Uventede serverfejl |

### handleMethodArgumentTypeMismatch

Denne handler fanger fejl når brugeren sender ugyldige værdier for enum-baserede query parameters.

**Eksempel scenario:**
```http
GET /api/projects?workType=INVALID_TYPE
```

**Response:**
```json
{
  "status": 400,
  "error": "Invalid Parameter",
  "message": "Ugyldig værdi 'INVALID_TYPE' for parameter 'workType'. Tilladte værdier er: [PAVING_CLEANING, WOODEN_DECK_CLEANING, ROOF_CLEANING, FACADE_CLEANING]",
  "path": "/api/projects",
  "additionalInfo": {
    "parameter": "workType",
    "invalidValue": "INVALID_TYPE",
    "allowedValues": "PAVING_CLEANING, WOODEN_DECK_CLEANING, ROOF_CLEANING, FACADE_CLEANING"
  }
}
```

**Fordele:**
- ✅ Brugervenlig fejlbesked på dansk
- ✅ Viser alle tilladte værdier
- ✅ Frontend kan bruge `additionalInfo.allowedValues` til at vise dropdown
- ✅ Konsistent fejlformat på tværs af hele API'et

**Implementation detaljer:**
```java
@ExceptionHandler(MethodArgumentTypeMismatchException.class)
public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(
        MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
    
    // Logger fejl til console
    logger.warn("Invalid parameter type - parameter: '{}', value: '{}', expectedType: '{}' - Path: {}", 
                ex.getName(), ex.getValue(), 
                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown",
                request.getRequestURI());
    
    // Bygger brugervenlig fejlbesked
    String message = buildEnumErrorMessage(ex);
    
    ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Invalid Parameter",
            message,
            request.getRequestURI()
    );
    
    // Tilføjer ekstra information hvis det er en enum
    if (ex.getRequiredType() != null && ex.getRequiredType().isEnum()) {
        Map<String, Object> additionalInfo = new HashMap<>();
        additionalInfo.put("parameter", ex.getName());
        additionalInfo.put("invalidValue", ex.getValue());
        
        // Samler alle tilladte enum værdier
        Object[] enumConstants = ex.getRequiredType().getEnumConstants();
        String allowedValues = Arrays.stream(enumConstants)
            .map(Object::toString)
            .collect(Collectors.joining(", "));
        additionalInfo.put("allowedValues", allowedValues);
        
        errorResponse.setAdditionalInfo(additionalInfo);
    }
    
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
}
```

### Logging

Alle exceptions logges til console og log filer:

**WARN** - Forventede fejl (projekt ikke fundet, valideringsfejl):
```
2024-12-03 12:30:45.123 WARN --- [nio-8080-exec-3] o.e.p.exception.GlobalExceptionHandler : Resource not found: Project med id 42 blev ikke fundet - Path: /api/projects/42
```

**WARN** - Invalid parameter (ny):
```
2025-12-04 17:02:02.409 WARN --- [nio-8080-exec-5] o.e.p.exception.GlobalExceptionHandler : Invalid parameter type - parameter: 'workType', value: 'INVALID_TYPE', expectedType: 'WorkType' - Path: /api/projects
```

**ERROR** - Uventede fejl (databaseproblemer, filupload fejl) med stack trace:
```
2024-12-03 12:35:22.891 ERROR --- [nio-8080-exec-5] o.e.p.exception.GlobalExceptionHandler : Unexpected error at /api/projects: Connection refused
java.net.ConnectException: Connection refused
    at java.base/sun.nio.ch.Net.pollConnect(Native Method)
    at org.ek.portfoliobackend.service.ProjectService.getAll(ProjectService.java:45)
    ... 47 more
```

---

## Custom Exceptions

### ResourceNotFoundException (404)

Kastes når et projekt, billede eller kategori ikke findes.

**AlgeNord use cases:**
```java
// Når kunde klikker på et projekt der ikke eksisterer
public ProjectDTO getProjectById(Long id) {
    return projectRepository.findById(id)
        .map(projectMapper::toDTO)
        .orElseThrow(() -> new ResourceNotFoundException("Project", id));
}

// Når AlgeNord prøver at redigere et ikke-eksisterende projekt
public ProjectDTO updateProject(Long id, UpdateProjectDTO dto) {
    Project project = projectRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Project", id));
    
    // Opdater projekt...
    return projectMapper.toDTO(project);
}

// Når et billede ikke findes
public ImageDTO getImageById(Long id) {
    return imageRepository.findById(id)
        .map(imageMapper::toDTO)
        .orElseThrow(() -> new ResourceNotFoundException("Image", id));
}
```

**Response til frontend:**
```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Project med id 42 blev ikke fundet",
  "path": "/api/projects/42"
}
```

---

### ValidationException (400)

Kastes ved business logic valideringsfejl specifikt for AlgeNord's krav.

**AlgeNord use cases:**
```java
// Check for før/efter billeder
public ProjectDTO createProject(CreateProjectDTO dto) {
    if (dto.getImages() == null || dto.getImages().size() < 2) {
        throw new ValidationException("Projekt skal have mindst ét før- og ét efter-billede");
    }
    
    // Check kategori er gyldig
    if (!isValidCategory(dto.getCategory())) {
        throw new ValidationException("Ugyldig projektkategori. Vælg: facade, fliser, tag, vinduer, eller trapper");
    }
    
    // Check projekt navn ikke allerede eksisterer
    if (projectRepository.existsByName(dto.getName())) {
        throw new ValidationException("Et projekt med dette navn eksisterer allerede");
    }
    
    // Opret projekt...
    Project project = projectMapper.toEntity(dto);
    project = projectRepository.save(project);
    
    return projectMapper.toDTO(project);
}

// Validering ved billedupload
public ImageDTO uploadImage(MultipartFile file) {
    if (file.isEmpty()) {
        throw new ValidationException("Billedfil må ikke være tom");
    }
    
    if (!isValidImageFormat(file)) {
        throw new ValidationException("Kun JPG, PNG og WEBP billeder er tilladt");
    }
    
    if (file.getSize() > MAX_FILE_SIZE) {
        throw new ValidationException("Billedet er for stort. Max størrelse: 5MB");
    }
    
    // Upload billede...
}
```

**Response til frontend:**
```json
{
  "status": 400,
  "error": "Validation Error",
  "message": "Projekt skal have mindst ét før- og ét efter-billede"
}
```

---

## Bean Validation (@Valid)

Spring's Bean Validation håndteres automatisk af `GlobalExceptionHandler`.

### DTO med AlgeNord-specifik validering

```java
public class CreateProjectDTO {
    
    @NotBlank(message = "Projektnavn må ikke være tomt")
    @Size(max = 100, message = "Projektnavn må max være 100 tegn")
    private String name;
    
    @NotBlank(message = "Beskrivelse er påkrævet")
    @Size(min = 10, max = 500, message = "Beskrivelse skal være mellem 10 og 500 tegn")
    private String description;
    
    @NotNull(message = "Kategori er påkrævet")
    private String category; // facade, fliser, tag, vinduer, trapper
    
    @NotNull(message = "Adresse er påkrævet")
    private String address;
    
    @Size(min = 2, message = "Mindst 2 billeder påkrævet (før og efter)")
    private List<Long> imageIds;
}
```

### Controller med @Valid

```java
@RestController
@RequestMapping("/api/projects")
public class ProjectController {
    
    @PostMapping
    public ResponseEntity<ProjectDTO> createProject(@Valid @RequestBody CreateProjectDTO dto) {
        // Hvis validation fejler, håndteres det automatisk af GlobalExceptionHandler
        ProjectDTO project = projectService.createProject(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(project);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ProjectDTO> updateProject(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProjectDTO dto) {
        ProjectDTO project = projectService.updateProject(id, dto);
        return ResponseEntity.ok(project);
    }
}
```

### Response ved Bean Validation fejl

Hvis AlgeNord prøver at oprette projekt uden navn eller beskrivelse:

```json
{
  "trackingId": "xyz-789",
  "status": 400,
  "error": "Validation Failed",
  "message": "Input validation fejlede for et eller flere felter",
  "path": "/api/projects",
  "validationErrors": {
    "name": "Projektnavn må ikke være tomt",
    "description": "Beskrivelse er påkrævet",
    "imageIds": "Mindst 2 billeder påkrævet (før og efter)"
  }
}
```

---

## Query Parameter Validation (Enums)

### Controller med Enum Query Parameters

```java
@RestController
@RequestMapping("/api/projects")
public class ProjectController {
    
    /**
     * Henter projekter med optional filtrering.
     * 
     * @param workType Optional filter på arbejdstype
     * @param customerType Optional filter på kundetype
     * @return Liste af projekter
     */
    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getAllProjects(
            @RequestParam(required = false) WorkType workType,
            @RequestParam(required = false) CustomerType customerType) {
        
        List<ProjectResponse> projects = projectService.getProjectsByFilters(workType, customerType);
        return ResponseEntity.ok(projects);
    }
}
```

### Enum Definitioner

```java
public enum WorkType {
    PAVING_CLEANING("Fliserens"),
    WOODEN_DECK_CLEANING("Rens af trædæk"),
    ROOF_CLEANING("Tagrens"),
    FACADE_CLEANING("Facaderens");
    
    private final String displayName;
    
    WorkType(String displayName) {
        this.displayName = displayName;
    }
}

public enum CustomerType {
    PRIVATE_CUSTOMER("Privat kunde"),
    BUSINESS_CUSTOMER("Erhvervskunde");
    
    private final String displayName;
    
    CustomerType(String displayName) {
        this.displayName = displayName;
    }
}
```

### API Eksempler

**Valid requests:**
```http
GET /api/projects
GET /api/projects?workType=PAVING_CLEANING
GET /api/projects?customerType=PRIVATE_CUSTOMER
GET /api/projects?workType=ROOF_CLEANING&customerType=BUSINESS_CUSTOMER
```

**Invalid request:**
```http
GET /api/projects?workType=INVALID_TYPE
```

**Response:**
```json
{
  "status": 400,
  "error": "Invalid Parameter",
  "message": "Ugyldig værdi 'INVALID_TYPE' for parameter 'workType'. Tilladte værdier er: [PAVING_CLEANING, WOODEN_DECK_CLEANING, ROOF_CLEANING, FACADE_CLEANING]",
  "path": "/api/projects",
  "additionalInfo": {
    "parameter": "workType",
    "invalidValue": "INVALID_TYPE",
    "allowedValues": "PAVING_CLEANING, WOODEN_DECK_CLEANING, ROOF_CLEANING, FACADE_CLEANING"
  }
}
```

---

## HTTP Status Codes

| Code | Status | AlgeNord Anvendelse |
|------|--------|------------|
| 200 | OK | Projekt hentet/opdateret succesfuldt |
| 201 | Created | Nyt projekt oprettet |
| 204 | No Content | Projekt slettet succesfuldt |
| 400 | Bad Request | Valideringsfejl (manglende billeder, ugyldig kategori, invalid enum) |
| 404 | Not Found | Projekt eller billede findes ikke |
| 500 | Internal Server Error | Serverfejl (database down, filupload fejl) |

---

## Sikkerhed

`handleGlobalException` viser **IKKE** tekniske fejldetaljer til brugeren.

### ❌ Dårligt (sikkerhedsrisiko):
```java
// UNDGÅ DETTE!
"Fejl: " + ex.getMessage() 
// Kan lække: database paths, filsystem stier, SQL fejl
```

### ✅ Godt (sikkert):
```java
// GØR DETTE!
"En uventet fejl opstod. Kontakt AlgeNord support hvis problemet fortsætter"
// Generisk besked til bruger, tekniske detaljer kun i logs
```

**Tracking ID** forbinder brugerens fejl med den tekniske fejl i logs, så AlgeNord teamet kan debugge uden at eksponere interne detaljer til kunder.

---

## Komplet Eksempel Flow

### Scenarie: Kunde filtrerer projekter med ugyldig workType

#### 1. Kunde request
```http
GET /api/projects?workType=INVALID_TYPE
```

#### 2. Spring prøver at parse enum
```java
// Spring forsøger automatisk at konvertere "INVALID_TYPE" til WorkType enum
// Dette fejler fordi "INVALID_TYPE" ikke er en gyldig WorkType værdi
// MethodArgumentTypeMismatchException kastes
```

#### 3. GlobalExceptionHandler fanger exception
```java
@ExceptionHandler(MethodArgumentTypeMismatchException.class)
public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(...) {
    logger.warn("Invalid parameter type - parameter: 'workType', value: 'INVALID_TYPE' - Path: /api/projects");
    
    // Bygger ErrorResponse med additionalInfo
    ErrorResponse errorResponse = new ErrorResponse(...);
    errorResponse.setAdditionalInfo(Map.of(
        "parameter", "workType",
        "invalidValue", "INVALID_TYPE",
        "allowedValues", "PAVING_CLEANING, WOODEN_DECK_CLEANING, ROOF_CLEANING, FACADE_CLEANING"
    ));
    
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
}
```

#### 4. Response til frontend (kunde ser)
```json
{
  "trackingId": "3828e029-ef9f-4580-9de9-5c0e912e87ec",
  "timestamp": "2025-12-04T17:02:02.409644",
  "status": 400,
  "error": "Invalid Parameter",
  "message": "Ugyldig værdi 'INVALID_TYPE' for parameter 'workType'. Tilladte værdier er: [PAVING_CLEANING, WOODEN_DECK_CLEANING, ROOF_CLEANING, FACADE_CLEANING]",
  "path": "/api/projects",
  "additionalInfo": {
    "parameter": "workType",
    "invalidValue": "INVALID_TYPE",
    "allowedValues": "PAVING_CLEANING, WOODEN_DECK_CLEANING, ROOF_CLEANING, FACADE_CLEANING"
  }
}
```

#### 5. Log i console (AlgeNord team ser)
```
2025-12-04 17:02:02.409 WARN --- [nio-8080-exec-3] o.e.p.exception.GlobalExceptionHandler : Invalid parameter type - parameter: 'workType', value: 'INVALID_TYPE', expectedType: 'WorkType' - Path: /api/projects
```

#### 6. Frontend kan bruge additionalInfo
```javascript
// Frontend kan ekstrahere tilladte værdier og vise en dropdown
const response = await fetch('/api/projects?workType=INVALID_TYPE');
const errorData = await response.json();

if (errorData.additionalInfo?.allowedValues) {
    // Vis dropdown med gyldige værdier
    const allowedValues = errorData.additionalInfo.allowedValues.split(', ');
    // allowedValues = ["PAVING_CLEANING", "WOODEN_DECK_CLEANING", "ROOF_CLEANING", "FACADE_CLEANING"]
}
```

---

## Best Practices for AlgeNord Portfolio

### ✅ Gør dette:
- **Kast exceptions i service laget** - ikke i controllers
- **Valider før/efter billeder** - projekt skal altid have begge
- **Check kategorier er gyldige** - kun tilladte værdier (facade, fliser, osv.)
- **Giv danske fejlbeskeder** - både kunder og AlgeNord team er danske
- **Log alle fejl** - hjælper med at finde problemer i produktionen
- **Inkluder tracking ID** - gør det nemt at finde fejl i logs
- **Brug additionalInfo** - når ekstra kontekst kan hjælpe frontend

### ❌ Undgå dette:
- **Try-catch i controllers** - lad GlobalExceptionHandler håndtere det
- **Returnere null** - kast exception i stedet
- **Vise stack traces til kunder** - brug generiske beskeder
- **Glemme at validere billeder** - kvalitet er vigtig for AlgeNord's portfolio
- **Engelsk fejlbeskeder** - systemet er til danske brugere
- **Hardcode enum værdier i fejlbeskeder** - brug automatisk generering

---

## Typiske AlgeNord Scenarios

### Scenarie 1: Upload projekt uden før/efter billeder
```java
if (dto.getImages().size() < 2) {
    throw new ValidationException("Projekt skal have mindst ét før- og ét efter-billede");
}
```

### Scenarie 2: Ugyldig projektkategori
```java
List<String> validCategories = Arrays.asList("facade", "fliser", "tag", "vinduer", "trapper");
if (!validCategories.contains(dto.getCategory())) {
    throw new ValidationException("Ugyldig kategori. Vælg: facade, fliser, tag, vinduer, eller trapper");
}
```

### Scenarie 3: Duplikat projektnavn
```java
if (projectRepository.existsByName(dto.getName())) {
    throw new ValidationException("Et projekt med navnet '" + dto.getName() + "' eksisterer allerede");
}
```

### Scenarie 4: Billede fil for stor
```java
if (file.getSize() > 5_000_000) { // 5MB
    throw new ValidationException("Billedet er for stort. Max størrelse: 5MB");
}
```

### Scenarie 5: Kunde prøver at tilgå slettet projekt
```java
public ProjectDTO getProjectById(Long id) {
    return projectRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Project", id));
}
// Response: 404 - "Project med id 42 blev ikke fundet"
```

### Scenarie 6: Kunde bruger ugyldig filter værdi (NY)
```java
// Dette håndteres automatisk af Spring og GlobalExceptionHandler
// GET /api/projects?workType=INVALID_TYPE
// Response: 400 - Med liste af tilladte værdier i additionalInfo
```

---

## Fremtidige Udvidelser (OVERVEJ AT FJERNE)

Når AlgeNord Portfolio vokser, overvej:

### Authentication & Authorization
- **Spring Security** til admin login
- **JWT tokens** for API sikkerhed
- **UnauthorizedException** hvis flere admin brugere tilføjes

### Billedhåndtering
- **ImageProcessingException** ved fejl i billedoptimering
- **StorageException** ved filupload problemer til cloud storage

### Avancerede Features
- **DuplicateResourceException** ved duplikat projekter
- **RateLimitException** hvis API rate limiting implementeres
- **EmailNotificationException** hvis email notifikationer tilføjes

### Monitoring
- **Sentry integration** til real-time error tracking
- **Log aggregation** (ELK stack) for lettere debugging i produktion

---

