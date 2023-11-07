package no.nav.syfo.kafka.producer

import java.time.LocalDateTime

data class VarselData(
    val journalpost: VarselDataJournalpost? = null,
    val narmesteLeder: VarselDataNarmesteLeder? = null,
    val motetidspunkt: VarselDataMotetidspunkt? = null
)
data class VarselDataJournalpost(
    val uuid: String,
    val id: String?
)
data class VarselDataNarmesteLeder(
    val navn: String?
)

data class VarselDataMotetidspunkt(
    val tidspunkt: LocalDateTime
)
