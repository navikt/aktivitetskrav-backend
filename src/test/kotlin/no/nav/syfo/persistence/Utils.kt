package no.nav.syfo.persistence

import no.nav.syfo.api.dto.AktivitetspliktStatus
import no.nav.syfo.kafka.consumer.domain.*
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.*

const val FNR_1 = "12345678901"
const val FNR_2 = "23456789012"
const val VURDERING_UUID_1 = "ee3f5b44-b6e3-4220-9afc-f8fc1f627c84"
const val SISTE_VURDERING_UUID = "ee3f5b44-b6e3-4220-9afc-f8fc1f627c86"

fun generateKAktivitetkravVurdering(personIdent: String = FNR_1, status: AktivitetspliktStatus, uuid: String = VURDERING_UUID_1) =
    KAktivitetskravVurdering(
        uuid = UUID.fromString(uuid), //  vil være felles innenfor ett aktivitetskrav, aktivitetskravUuid i varsel
        personIdent = personIdent,
        createdAt = OffsetDateTime.now(),
        status = status.name,
        beskrivelse = "beskrivelse",
        arsaker = listOf("arsak1", "arsak2"),
        stoppunktAt = LocalDate.now(),
        updatedBy = "oppdatert av",
        sisteVurderingUuid = UUID.fromString(SISTE_VURDERING_UUID),
        sistVurdert = OffsetDateTime.now(),
        frist = LocalDate.now().plusDays(7L)
    )

fun generateKAktivitetkravVarsel(personIdent: String, aktivitetskravUuid: String): KAktivitetskravVarsel =
    KAktivitetskravVarsel(
        personIdent = personIdent,
        aktivitetskravUuid = UUID.fromString(aktivitetskravUuid), // uuid i vurdering
        vurderingUuid = UUID.fromString(SISTE_VURDERING_UUID),
        varselUuid = UUID.randomUUID(),
        createdAt = OffsetDateTime.now(),
        journalpostId = "123",
        svarfrist = LocalDate.now().plusDays(14),
        document = listOf(
            DocumentComponentDTO(type = DocumentComponentType.HEADER_H1, key = "key", title = "title", texts = listOf("text1", "text2"))
        ),
        type = AktivitetskravVarselType.FORHANDSVARSEL_STANS_AV_SYKEPENGER.name
    )
