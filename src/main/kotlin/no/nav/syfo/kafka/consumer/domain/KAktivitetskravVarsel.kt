package no.nav.syfo.kafka.consumer.domain

import no.nav.syfo.service.domain.AktivitetskravVarsel
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.*

data class DocumentComponentDTO(
    val type: DocumentComponentType,
    val key: String? = null,
    val title: String?,
    val texts: List<String>
)

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
    val varselUuid: UUID,
    val createdAt: OffsetDateTime,
    val journalpostId: String,
    val svarfrist: LocalDate,
    val document: List<DocumentComponentDTO>
)

fun KAktivitetskravVarsel.toAktivitetskravVarsel() =
    AktivitetskravVarsel(
        personIdent = this.personIdent,
        aktivitetskravUuid = this.aktivitetskravUuid,
        varselUuid = this.varselUuid,
        createdAt = this.createdAt,
        journalpostId = this.journalpostId,
        svarfrist = this.svarfrist,
        document = this.document
    )
