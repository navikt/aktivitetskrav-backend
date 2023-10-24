package no.nav.syfo.service.domain

import no.nav.syfo.kafka.consumer.domain.DocumentComponentDTO
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.*

data class AktivitetskravVarsel(
    val personIdent: String,
    val aktivitetskravUuid: UUID,
    val vurderingUuid: UUID,
    val varselUuid: UUID,
    val createdAt: OffsetDateTime,
    val journalpostId: String,
    val svarfrist: LocalDate,
    val document: List<DocumentComponentDTO>
)
