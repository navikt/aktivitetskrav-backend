CREATE TABLE AKTIVITETSKRAV_VARSEL (
  uuid                     UUID               PRIMARY KEY,
  person_ident             VARCHAR(11)        NOT NULL,
  aktivitetskrav_uuid      UUID               NOT NULL,
  varsel_uuid              UUID               NOT NULL,
  created_at               TIMESTAMP          NOT NULL,
  journalpost_id           VARCHAR(50)        NOT NULL,
  svarfrist                DATE               NOT NULL,
  document                 TEXT               NOT NULL
);
