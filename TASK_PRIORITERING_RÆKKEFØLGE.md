# AlgeNord Portfolio - Task Prioritering og Rækkefølge

## Komplet Task Liste i Anbefalet Rækkefølge

| # | Task ID | Task Navn | Type | Epic/Story | Dependencies | Kritisk? |
|---|---------|-----------|------|------------|--------------|----------|
| **FASE 1: FUNDAMENTET** |
| 1 | S1.1 | Backend Package Structure | Infrastructure | S1 - Backend Setup | Ingen | ✅ |
| 2 | S1.2 | Application Configuration Files | Infrastructure | S1 - Backend Setup | S1.1 | ✅ |
| 3 | S1.3 | Resource Directories | Infrastructure | S1 - Backend Setup | S1.2 | ✅ |
| 4 | S1.4 | Testing Structure | Infrastructure | S1 - Backend Setup | S1.1, S1.2, S1.3 | ✅ |
| 5 | S6.1 | Frontend Repository & Basic Structure | Infrastructure | S6 - Frontend Setup | Ingen | ✅ |
| 6 | S6.2 | JavaScript Module Structure | Infrastructure | S6 - Frontend Setup | S6.1 | ✅ |
| 7 | S6.3 | CSS Structure | Infrastructure | S6 - Frontend Setup | S6.1 | ✅ |
| 8 | S6.4 | Base HTML Template | Infrastructure | S6 - Frontend Setup | S6.1, S6.3 | ✅ |
| **FASE 2: CORE INFRASTRUCTURE** |
| 9 | S2.1 | Global Exception Handler | Infrastructure | S2 - Core Infrastructure | S1.1 | ✅ |
| 10 | S2.2 | Interfaces, Service og Repository | Infrastructure | S2 - Core Infrastructure | S1.1, S2.1 | ✅ |
| 11 | S2.3 | Spring Security, JWT tokens | Infrastructure | S2 - Core Infrastructure | S1.2, S2.2 | ✅ |
| 12 | S2.4 | Profile CRUD's | Infrastructure | S2 - Core Infrastructure | S2.2, S2.3 | ✅ |
| **FASE 3: DEPLOYMENT FORBEREDELSE** |
| 13 | S3.2 | Database Setup | DevOps | S3 - Deployment | S1.2 | ⚠️ |
| 14 | S3.1 | Deployment: Docker + DigitalOcean | DevOps | S3 - Deployment | S1 (komplet) | ⚠️ |
| **FASE 4: DESIGN & ARKITEKTUR** |
| 15 | S4.1 | Wireframes | Design | S4 - Design Doc | Ingen | 📐 |
| 16 | S4.2 | ER-diagram | Design | S4 - Design Doc | Ingen | 📐 |
| 17 | S4.3 | Dataflow Diagram | Design | S4 - Design Doc | S4.2 | 📐 |
| 18 | S5.1 | JS arkitektur: api.js, views, forms | Architecture | S5 - Frontend Arch | S6 (komplet) | ✅ |
| **FASE 5: FEATURE DEVELOPMENT - EPIC 1: PROJEKTHÅNDTERING** |
| **User Story 1.1 - Create Project** |
| 19 | 1.1.1 | Entity + Enums | Backend | US 1.1 | S2.2 | ✅ |
| 20 | 1.1.2 | DTO | Backend | US 1.1 | 1.1.1 | ✅ |
| 21 | 1.1.3 | Project-mapper | Backend | US 1.1 | 1.1.1, 1.1.2 | ✅ |
| 22 | 1.1.4 | Repositories med JPA | Backend | US 1.1 | 1.1.1 | ✅ |
| 23 | 1.1.5 | Services | Backend | US 1.1 | 1.1.3, 1.1.4 | ✅ |
| 24 | 1.1.6 | Controller | Backend | US 1.1 | 1.1.5 | ✅ |
| 25 | 1.1.7 | Opret projekt formular (UI) | Frontend | US 1.1 | S5.1, 1.1.6 | ✅ |
| **User Story 1.2 - Edit Project** |
| 26 | 1.2.1 | DTO'er | Backend | US 1.2 | 1.1.2 | ✅ |
| 27 | 1.2.2 | Projectmapper updates | Backend | US 1.2 | 1.1.3, 1.2.1 | ✅ |
| 28 | 1.2.3 | ProjectService updates | Backend | US 1.2 | 1.1.5, 1.2.2 | ✅ |
| 29 | 1.2.4 | Controller updates | Backend | US 1.2 | 1.2.3 | ✅ |
| 30 | 1.2.5 | Views for læse/opdatere projekter | Frontend | US 1.2 | 1.1.7, 1.2.4 | ✅ |
| **User Story 1.3 - Delete Project** |
| 31 | 1.3.1 | ProjectService delete | Backend | US 1.3 | 1.1.5 | ✅ |
| 32 | 1.3.2 | Controller delete | Backend | US 1.3 | 1.3.1 | ✅ |
| 33 | 1.3.3 | UI for sletning | Frontend | US 1.3 | 1.2.5, 1.3.2 | ✅ |
| **FASE 6: FEATURE DEVELOPMENT - EPIC 2: PRÆSENTATIONSVISNING** |
| **User Story 2.3 - Sortering (FØRST)** |
| 34 | 2.3.1 BE | Sortering i repository | Backend | US 2.3 | 1.1.4 | 🔄 |
| 35 | 2.3.1 FE | Sorteringsmulighed UI | Frontend | US 2.3 | 1.2.5, 2.3.1 BE | 🔄 |
| **User Story 2.1 - Præsentationsvisning** |
| 36 | 2.1.1 | Præsentationsview | Frontend | US 2.1 | 2.3.1 FE, S4.1 | 📺 |
| **User Story 2.2 - Før/Efter Slider** |
| 37 | 2.2.1 | Slider implementation | Frontend | US 2.2 | 2.1.1, 1.1.1 | 🎚️ |
| **FASE 7: FEATURE DEVELOPMENT - EPIC 3: FILTRERING** |
| **User Story 3.1 - Filtrering** |
| 38 | 3.1.1 BE | Filtreringslogik | Backend | US 3.1 | 1.1.4, 2.3.1 BE | 🔍 |
| 39 | 3.1.1 FE | Filterknapper UI | Frontend | US 3.1 | 2.3.1 FE, 3.1.1 BE | 🔍 |

---

## Legende

### Type Ikoner
- ✅ **Kritisk** - Skal færdiggøres før videre udvikling
- ⚠️ **DevOps** - Kan sættes op tidligt, skal være færdig til production
- 📐 **Design** - Kan arbejdes på parallelt, ideelt færdig før feature development
- 🔄 **Sortering** - Påvirker andre features
- 📺 **Præsentation** - Visningsfeatures
- 🎚️ **UI Komponent** - Avanceret UI funktionalitet
- 🔍 **Filtrering** - Søge- og filtreringsfunktionalitet

### Dependencies Forklaring
- **Ingen**: Kan startes med det samme
- **Task ID**: Skal vente på specificeret task
- **Epic komplet**: Skal vente på hele epic er færdig

---

## Parallel Work Muligheder

### Efter FASE 1 er komplet:
- **Backend team** kan arbejde på FASE 2
- **Frontend team** kan arbejde på S5.1 (Frontend arkitektur)
- **DevOps team** kan starte på S3.2 (Database)

### Efter FASE 2 er komplet:
- **Design team** kan arbejde på FASE 4 (S4.1-S4.3)
- **DevOps team** kan arbejde på S3.1 (Docker + Deployment)
- **Backend team** kan starte på 1.1.1 (Entities)

### Under FASE 5 (Feature Development):
- **Backend** arbejder 1-2 tasks foran frontend
- **Frontend** implementerer UI når backend endpoints er klar
- Design/DevOps kan finpudse deres tasks

---

## Sprint Forslag

### Sprint 0: Foundation (Uge 1-2)
- FASE 1 (S1 + S6)
- FASE 2 (S2.1-S2.4)

### Sprint 1: Infrastructure (Uge 3)
- FASE 3 (S3.1-S3.2)
- FASE 4 (S4.1-S4.3, S5.1)

### Sprint 2: Create Project (Uge 4-5)
- US 1.1 komplet (1.1.1-1.1.7)

### Sprint 3: Edit & Delete (Uge 6-7)
- US 1.2 komplet (1.2.1-1.2.5)
- US 1.3 komplet (1.3.1-1.3.3)

### Sprint 4: Præsentation (Uge 8)
- US 2.3 komplet (Sortering)
- US 2.1 komplet (Præsentationsview)
- US 2.2 komplet (Slider)

### Sprint 5: Filtrering & Polish (Uge 9)
- US 3.1 komplet (Filtrering)
- Bug fixes & polish

---

## Kritiske Blokkere at Undgå

1. ❌ **Undgå at starte 2.1.1** før 2.3.1 er færdig - præsentationen skal kunne sortere
2. ❌ **Undgå at starte 1.2.X** før 1.1.X er komplet - edit afhænger af create
3. ❌ **Undgå at starte feature development** før S2.2 (Service struktur) er på plads
4. ❌ **Undgå at deploye til production** før S2.3 (Security) er implementeret
5. ❌ **Undgå at starte frontend features** før S5.1 (arkitektur) er etableret

---

## Anbefalinger

### For Effektiv Gennemførelse:
1. **Følg rækkefølgen strengt i FASE 1-2** (fundamentet skal være solidt)
2. **Vær fleksibel i FASE 4** (design kan justeres løbende)
3. **Test hver User Story komplet** før I går videre til næste
4. **Deploy tidligt og ofte** efter S3.1 er færdig
5. **Hold design sessions** før hver ny epic starter

### For Team Koordination:
- **Daily standups** for at koordinere dependencies
- **Task board** hvor I markerer "In Progress", "Blocked", "Done"
- **Code reviews** før tasks markeres som "Done"
- **Integration tests** efter hver User Story er komplet

---

**Sidst opdateret**: 30. november 2025
