# ============================================================================
# ALGENORD PORTFOLIO - BACKEND DOCKERFILE
# ============================================================================
# Dette er en multi-stage Dockerfile der bygger Spring Boot applikationen.
# Multi-stage betyder at vi bruger flere FROM statements - ét til at bygge
# og ét til at køre. Dette giver et mindre og mere sikkert produktions-image.
# ============================================================================


# ============================================================================
# STAGE 1: BUILDER
# ============================================================================
# Vi bruger Maven med Eclipse Temurin JDK 21 som base image.
# Eclipse Temurin er den officielle efterfølger til AdoptOpenJDK.
# "AS builder" giver dette stage et navn så vi kan referere til det senere.
#'builder' er bare et navn

FROM maven:3.9.9-eclipse-temurin-21 AS builder

# Sætter arbejdsmappen i containeren.
# Alle efterfølgende kommandoer kører relativt til denne mappe.

WORKDIR /app

# ============================================================================
# MAVEN DEPENDENCY CACHING
# ============================================================================
# Her kopierer vi FØRST kun pom.xml og downloader dependencies.
# Dette er en vigtig optimering: Docker cacher hvert lag (layer).
# Hvis pom.xml ikke ændres, genbruges dette lag ved næste build.
# Det sparer tid fordi Maven dependencies ikke skal downloades hver gang.
#
# "go-offline" downloader alle dependencies defineret i pom.xml
# uden at bygge selve projektet.
# -B = batch mode (non-interactive; Maven må ikke stille spørgsmål — vigtigt i CI/Docker)


COPY pom.xml .
RUN mvn dependency:go-offline -B

# ============================================================================
# BYGNING AF APPLIKATIONEN
# ============================================================================
# Nu kopierer vi resten af kildekoden.
# Fordi dependencies allerede er cached, skal de ikke downloades igen
# selvom kildekoden ændrer sig.

COPY src ./src

# Bygger .jar filen med Maven.
# -DskipTests: Vi springer tests over her fordi de køres i GitHub Actions.
#              Det er spild af tid at køre dem to gange.
# -B: Batch mode - mindre verbose output, bedre til CI/CD.
# package: Maven lifecycle fase der compiler og pakker til .jar.
# -B = batch mode (non-interactive; Maven må ikke stille spørgsmål — vigtigt i CI/Docker)


RUN mvn package -DskipTests -B


# ============================================================================
# STAGE 2: RUNTIME
# ============================================================================
# Her starter vi forfra med et rent, minimalt image.
# Vi bruger kun JRE (Java Runtime Environment) i stedet for JDK,
# fordi vi ikke behøver compiler-værktøjer til at KØRE applikationen.
# "eclipse-temurin:21-jre" er meget mindre end JDK versionen.
# Dette giver et mindre og mere sikkert produktions-image.

FROM eclipse-temurin:21-jre

# Sætter arbejdsmappen for runtime containeren.

WORKDIR /app

# ============================================================================
# KOPIERING AF ARTEFAKT FRA BUILDER
# ============================================================================
# --from=builder refererer til vores første stage.
# Vi kopierer KUN den færdige .jar fil - ikke Maven, ikke kildekode.
# Dette holder produktions-imaget lille og sikkert.
#
# target/*.jar matcher den byggede JAR fil.
# Vi omdøber den til app.jar for konsistens.

COPY --from=builder /app/target/*.jar app.jar

# ============================================================================
# UPLOAD DIRECTORY
# ============================================================================
# Opretter mappen hvor uploadede billeder gemmes.
# Dette er nødvendigt for AlgeNord's image upload funktionalitet.
# I produktion bør denne mappe mountes som et volume for persistens.

RUN mkdir -p /app/uploads

# ============================================================================
# EKSPONERING AF PORT
# ============================================================================
# EXPOSE dokumenterer hvilken port applikationen lytter på.
# Spring Boot bruger default port 8080.
# Bemærk: EXPOSE åbner ikke porten - det er kun dokumentation.
# Den faktiske port-mapping sker i docker-compose.yaml.

EXPOSE 8080

# ============================================================================
# STARTUP KOMMANDO
# ============================================================================
# ENTRYPOINT definerer kommandoen der køres når containeren starter.
# "java -jar app.jar" starter Spring Boot applikationen.
#
# Vi bruger ENTRYPOINT frem for CMD fordi:
# - ENTRYPOINT er sværere at override ved et uheld
# - Det signalerer at dette er den primære funktion for containeren

ENTRYPOINT ["java", "-jar", "app.jar"]