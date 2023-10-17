package no.nav.syfo.service

import java.time.LocalDate
import java.time.OffsetDateTime

data class AktivitetskravVurdering(
    val uuid: String,
    val personIdent: String,
    val createdAt: OffsetDateTime,
    val status: String,
    val beskrivelse: String?,
    val arsaker: List<String>,
    val stoppunktAt: LocalDate,
    val updatedBy: String?,
    val sistVurdert: OffsetDateTime?,
    val frist: LocalDate?
)
