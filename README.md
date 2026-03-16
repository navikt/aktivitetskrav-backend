# aktivitetskrav-backend

[![Build](https://github.com/navikt/aktivitetskrav-backend/actions/workflows/build-and-deploy.yaml/badge.svg)](https://github.com/navikt/aktivitetskrav-backend/actions/workflows/build-and-deploy.yaml)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Kafka](https://img.shields.io/badge/Kafka-231F20?logo=apachekafka&logoColor=white)](https://kafka.apache.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?logo=postgresql&logoColor=white)](https://www.postgresql.org/)

## Formålet med appen

Aktivitetskrav-backend er en backend-tjeneste som håndterer **aktivitetskrav** (krav om yrkesrettet aktivitet) for sykmeldte personer i NAV.

Appen:

- **Konsumerer Kafka-events** fra `teamsykefravr` med vurderinger og varsler om aktivitetskrav
- **Lagrer data** i PostgreSQL — vurderinger med status, frister og begrunnelser, samt varsler med dokumentkomponenter
- **Eksponerer REST API** (beskyttet med TokenX) slik at sykmeldte kan se sin aktivitetsplikt-status via `esyfo-proxy`
- **Produserer events** til `team-esyfo.varselbus` for å vise varsler og dokumenter i brukerens mikrofrontend

### Dataflyt

```mermaid
flowchart LR
    subgraph Kafka
        A["teamsykefravr\n.aktivitetskrav-vurdering"]
        B["teamsykefravr\n.aktivitetskrav-varsel"]
        C["team-esyfo\n.varselbus"]
    end

    subgraph aktivitetskrav-backend
        D[Kafka Consumer]
        E[(PostgreSQL)]
        F[REST API]
        G[Kafka Producer]
    end

    H[esyfo-proxy]

    A -->|vurdering| D
    B -->|varsel| D
    D --> E
    E --> F
    F -->|TokenX| H
    D --> G
    G -->|SM_AKTIVITETSPLIKT| C
```

## API

Alle endepunkter krever TokenX-autentisering (acr: `Level4` / `idporten-loa-high`) og er kun tilgjengelig via `esyfo-proxy`.

| Metode | Sti | Beskrivelse |
|--------|-----|-------------|
| `GET` | `/api/v1/aktivitetsplikt` | Hent gjeldende aktivitetsplikt-status |
| `POST` | `/api/v1/aktivitetsplikt/les` | Marker aktivitetskrav som lest |
| `GET` | `/api/v1/aktivitetsplikt/historikk` | Hent historikk for aktivitetskrav |

> ℹ️ Det finnes ingen Swagger/OpenAPI-dokumentasjon for dette APIet.

## Utvikling

### Forutsetninger

- JDK 21
- PostgreSQL (eller bruk testprofilen med H2)

### Kjøre appen

```bash
./gradlew bootRun
```

> ⚠️ Appen forventer miljøvariabler for database (`DB_HOST`, `DB_PORT`, `DB_DATABASE`, `DB_USERNAME`, `DB_PASSWORD`), Kafka og TokenX. For lokal utvikling uten disse avhengighetene kan testene kjøres med H2 in-memory database.

### Kjøre tester

```bash
./gradlew test
```

Testene bruker H2 in-memory database (PostgreSQL-kompatibilitetsmodus) og MockOAuth2Server.

### 🧹 Kodeformatering

Vi bruker **Ktlint** (`intellij_idea`-stil) for konsistent Kotlin-formatering.

👉 Installer **Ktlint**-pluginen i IntelliJ:
- Gå til *Preferences → Plugins → Marketplace → søk "Ktlint" → Install*
- Aktiver **"Format on Save"**

Alternativt kan du kjøre:

```bash
./gradlew ktlintFormat
```

## Henvendelser

Spørsmål knyttet til koden eller prosjektet kan stilles til team-esyfo.

## For NAV-ansatte

Interne henvendelser kan sendes via Slack i kanalen #esyfo.
