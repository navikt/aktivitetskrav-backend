package no.nav.syfo.api.dto

import no.nav.syfo.kafka.consumer.domain.DocumentComponentDTO
import java.time.LocalDateTime

enum class AktivitetspliktStatus {
    NY, AVVENT, UNNTAK, OPPFYLT, AUTOMATISK_OPPFYLT, FORHANDSVARSEL, IKKE_OPPFYLT, IKKE_AKTUELL
}

data class Aktivitetsplikt(
    val status: AktivitetspliktStatus,
    val arsaker: List<String>,
    val sistVurdert: LocalDateTime?,
    val fristDato: LocalDateTime?,
    val journalpostId: String?,
    val dokument: List<DocumentComponentDTO>?,
)
