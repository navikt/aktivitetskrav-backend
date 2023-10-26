package no.nav.syfo.kafka.consumer.domain

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
) : Serializable
