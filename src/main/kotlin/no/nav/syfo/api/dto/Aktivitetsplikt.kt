package no.nav.syfo.api.dto

import java.time.LocalDateTime

enum class AktivitetspliktStatus {
    NY, NY_VURDERING, AVVENT, UNNTAK, OPPFYLT, AUTOMATISK_OPPFYLT, FORHANDSVARSEL, IKKE_OPPFYLT, IKKE_AKTUELL, LUKKET
}

data class Aktivitetsplikt(
    val status: AktivitetspliktStatus,
    val arsaker: List<String>,
    val sistVurdert: LocalDateTime?,
    val fristDato: LocalDateTime?,
    val journalpostId: String?,
)
