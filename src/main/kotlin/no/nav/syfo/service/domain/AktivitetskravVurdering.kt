package no.nav.syfo.service.domain

import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.UUID

data class AktivitetskravVurdering(
    val uuid: UUID,
    val personIdent: String,
    val createdAt: OffsetDateTime,
    val status: String,
    val beskrivelse: String?,
    val arsaker: List<String>,
    val stoppunktAt: LocalDate,
    val updatedBy: String?,
    val sistVurdert: OffsetDateTime?,
    val frist: LocalDate?,
    val internUuid: UUID
)
