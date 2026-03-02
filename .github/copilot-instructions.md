<!-- Managed by esyfo-cli. Do not edit manually. Changes will be overwritten.
     For repo-specific customizations, create your own files without this header. -->
# aktivitetskrav-backend

## Team
- **Team**: team-esyfo, NAV IT
- **Org**: navikt

## NAV Principles
- **Team First**: Autonomous teams with circles of autonomy
- **Product Development**: Continuous development over ad hoc approaches
- **Essential Complexity**: Focus on essential, avoid accidental complexity
- **DORA Metrics**: Measure and improve team performance

## Platform & Auth
- **Platform**: NAIS (Kubernetes on GCP)
- **Auth**: Azure AD (internal users), TokenX (on-behalf-of token exchange), ID-porten (citizens), Maskinporten (machine-to-machine)
- **Observability**: Prometheus metrics, Grafana Loki logs, Tempo tracing (OpenTelemetry)

## Conventions
- English code and comments — Norwegian for user-facing text and domain terms (e.g. dialogmote, sykmelding, oppfolgingsplan)
- Use Context7 (`context7-resolve-library-id` → `context7-query-docs`) for library-specific patterns
- Check existing code patterns in the repository before writing new code
- Follow the ✅ Always / ⚠️ Ask First / 🚫 Never boundaries in agent and instruction files


## Tech Stack
- **Language**: Kotlin
- **Framework**: Spring Boot
- **Build**: Gradle (Kotlin DSL)
- **Database**: PostgreSQL (via Spring Data JDBC)
- **Messaging**: Apache Kafka
- **Testing**: Kotest, MockK
- **Auth**: Azure AD + TokenX (check NAIS manifests for which are enabled)

## Backend Patterns
- Check `build.gradle.kts` for actual dependencies before suggesting libraries
- Use Flyway for all database migrations — never modify existing migrations
- Parameterized queries always — never string interpolation in SQL
- Implement Repository pattern for database access
- Structured logging with KotlinLogging and `kv()` fields
- Follow existing code patterns in the repository

## Boundaries

### ✅ Always
- Use Flyway for database migrations
- Add Prometheus metrics for business operations
- Validate JWT issuer, audience, and expiration

### ⚠️ Ask First
- Changing database schema or Kafka event schemas
- Modifying authentication configuration
- Adding new GCP resources

### 🚫 Never
- Skip database migration versioning
- Hardcode secrets or configuration values
- Use `!!` operator without null checks
- Bypass authentication checks
