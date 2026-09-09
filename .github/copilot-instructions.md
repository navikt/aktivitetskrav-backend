# aktivitetskrav-backend

- `mise verify` runs lint and tests; `./gradlew build` runs the full build.
- Tests use H2 in PostgreSQL mode and embedded Kafka. They do not prove
  compatibility with production PostgreSQL or Aiven.
- Citizen APIs derive the person from the TokenX `pid` claim. Preserve both
  the high-assurance `acr` check and the permitted `client_id` check.
- Vurdering events and varsel events have different handling: only
  `FORHANDSVARSEL_STANS_AV_SYKEPENGER` varsler are stored and forwarded.
- Kafka acknowledgement follows persistence and publication to varselbus.
  Preserve this ordering and the failure path when changing event handling.
- Aktivitetskrav assessments and document content contain health information;
  keep them and person identifiers out of ordinary logs.
