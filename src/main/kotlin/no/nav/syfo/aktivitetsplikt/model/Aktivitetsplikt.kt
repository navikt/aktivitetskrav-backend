package no.nav.syfo.aktivitetsplikt.model

import java.time.LocalDateTime

enum class AktivitetspliktStatus {
    NY, UNNTAK, OPPFYLT, FORHANDSVARSEL, IKKE_OPPFYLT, IKKE_AKTUELL
}

enum class AktivitetspliktArsaker {
    MEDISINSKE_GRUNNER, TILRETTELEGGING_IKKE_MULIG, SJOMENN_UTENRIKS, FRISKMELDT, GRADERT, IKKE_AKTUELL
}

data class Aktivitetsplikt(
    val status: AktivitetspliktStatus,
    val arsaker: AktivitetspliktArsaker?,
    val sistVurdert: LocalDateTime?,
    val fristDato: LocalDateTime?,
    val journalpostId: String?
)
