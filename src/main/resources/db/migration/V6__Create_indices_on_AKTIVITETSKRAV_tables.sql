CREATE INDEX aktivitetkrav_vurdering_index ON TABLE AKTIVITETSKRAV_VURDERING (
    person_ident,
    siste_vurdering_uuid
);

CREATE INDEX aktivitetkrav_varsel_index ON TABLE AKTIVITETSKRAV_VARSEL (
    person_ident,
    vurdering_uuid
);
