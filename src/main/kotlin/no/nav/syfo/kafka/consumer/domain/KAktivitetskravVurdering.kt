package no.nav.syfo.kafka.consumer.domain

import no.nav.syfo.service.AktivitetskravVurdering
import java.time.LocalDate
import java.time.OffsetDateTime

data class KAktivitetskravVurdering(
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

fun KAktivitetskravVurdering.toAktivitetskravVurdering() =
    AktivitetskravVurdering(
        uuid = this.uuid,
        personIdent = this.personIdent,
        createdAt = this.createdAt,
        status = this.status,
        beskrivelse = this.beskrivelse,
        arsaker = this.arsaker,
        stoppunktAt = this.stoppunktAt,
        updatedBy = this.updatedBy,
        sistVurdert = this.sistVurdert,
        frist = this.frist
    )
