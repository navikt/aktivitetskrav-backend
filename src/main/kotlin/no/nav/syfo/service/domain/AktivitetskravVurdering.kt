package no.nav.syfo.service.domain

import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.UUID

data class AktivitetskravVurdering(
    val uuid: UUID,
    val vurderingUuid: UUID,
    val personIdent: String,
    val createdAt: OffsetDateTime,
    val status: String,
    val beskrivelse: String?,
    val arsaker: String,
    val stoppunktAt: LocalDate,
    val updatedBy: String?,
    val sisteVurderingUuid: UUID?,
    val sistVurdert: OffsetDateTime?,
    val frist: LocalDate?
)
