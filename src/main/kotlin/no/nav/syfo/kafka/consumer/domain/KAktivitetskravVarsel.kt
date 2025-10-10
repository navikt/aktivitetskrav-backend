package no.nav.syfo.kafka.consumer.domain

import no.nav.syfo.kafka.producer.VarselData
import no.nav.syfo.kafka.producer.VarselDataAktivitetskrav
import no.nav.syfo.kafka.producer.VarselDataJournalpost
import java.io.Serializable
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.*

data class DocumentComponentDTO(
    val type: DocumentComponentType,
    val key: String? = null,
    val title: String?,
    val texts: List<String>
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}

enum class DocumentComponentType {
    HEADER_H1,
    HEADER_H2,
    HEADER_H3,
    PARAGRAPH,
    BULLET_POINTS,
    LINK
}

data class KAktivitetskravVarsel(
    val personIdent: String,
    val aktivitetskravUuid: UUID,
    val vurderingUuid: UUID,
    val varselUuid: UUID,
    val createdAt: OffsetDateTime,
    val journalpostId: String,
    val svarfrist: LocalDate?,
    val document: List<DocumentComponentDTO>,
    val type: String
) : Serializable,
    VarselbusEvent {

    override fun personIdent() = personIdent

    override fun varselData(): VarselData = VarselData(
        journalpost = VarselDataJournalpost(
            id = journalpostId,
            uuid = "$aktivitetskravUuid"
        ),
        aktivitetskrav = VarselDataAktivitetskrav(
            sendForhandsvarsel = true,
            enableMicrofrontend = true,
            extendMicrofrontendDuration = false
        )
    )
    companion object {
        private const val serialVersionUID = 1L
    }
}

enum class AktivitetskravVarselType {
    FORHANDSVARSEL_STANS_AV_SYKEPENGER,
}
