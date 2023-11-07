package no.nav.syfo.kafka.consumer.domain

import no.nav.syfo.kafka.domain.HendelseType
import no.nav.syfo.kafka.producer.VarselData
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
) : Serializable

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
    val svarfrist: LocalDate,
    val document: List<DocumentComponentDTO>
) : Serializable, VarselbusEvent {
    override fun eventType() = HendelseType.SM_AKTIVITETSPLIKT_STATUS_FORHANDSVARSEL
    override fun personIdent() = personIdent

    override fun varselData(): VarselData = VarselData(
        VarselDataJournalpost(
            id = journalpostId,
            uuid = "$aktivitetskravUuid"
        )
    )
}
