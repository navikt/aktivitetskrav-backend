CREATE TABLE AKTIVITETSKRAV_VURDERING (
  uuid                     UUID               PRIMARY KEY,
  vurdering_uuid           UUID               NOT NULL,
  person_ident             VARCHAR(11)        NOT NULL,
  created_at               TIMESTAMP          NOT NULL,
  status                   VARCHAR(40)        NOT NULL,
  beskrivelse              TEXT,
  arsaker                  TEXT               NOT NULL,
  stoppunkt_at             DATE               NOT NULL,
  updated_by               TEXT,
  sist_vurdert             TIMESTAMP,
  frist                    DATE
);
