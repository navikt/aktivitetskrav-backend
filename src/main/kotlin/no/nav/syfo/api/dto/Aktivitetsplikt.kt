package no.nav.syfo.api.dto

import no.nav.syfo.kafka.consumer.domain.DocumentComponentDTO
import java.time.LocalDate
import java.time.LocalDateTime

enum class AktivitetspliktStatus {
    NY,
    NY_VURDERING,
    AVVENT,
    UNNTAK,
    OPPFYLT,
    AUTOMATISK_OPPFYLT,
    FORHANDSVARSEL,
    IKKE_OPPFYLT,
    INNSTILLING_OM_STANS,
    IKKE_AKTUELL,
    LUKKET
}

data class Aktivitetsplikt(
    val internUuid: String,
    val status: AktivitetspliktStatus,
    val arsaker: List<String>,
    val sistVurdert: LocalDateTime?,
    val createdAt: LocalDateTime,
    val fristDato: LocalDate?,
    val journalpostId: String?,
    val document: List<DocumentComponentDTO>?
)
