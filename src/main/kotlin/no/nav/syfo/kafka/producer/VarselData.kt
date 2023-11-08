package no.nav.syfo.kafka.producer

data class VarselData(
    val journalpost: VarselDataJournalpost? = null,
    val aktivitetskrav: VarselDataAktivitetskrav? = null
)

data class VarselDataJournalpost(
    val uuid: String,
    val id: String?
)

data class VarselDataAktivitetskrav(
    val sendForhandsvarsel: Boolean,
    val enableMicrofrontend: Boolean,
    val extendMicrofrontendDuration: Boolean
)
