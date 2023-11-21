package no.nav.syfo.persistence

import no.nav.syfo.api.dto.AktivitetspliktStatus
import no.nav.syfo.kafka.consumer.domain.KAktivitetskravVurdering
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.*

const val FNR_1 = "12345678901"
const val FNR_2 = "23456789012"

fun generateKAktivitetkravVurdering(personIdent: String = FNR_1) =
    KAktivitetskravVurdering(
        uuid = UUID.randomUUID(),
        personIdent = personIdent,
        createdAt = OffsetDateTime.now(),
        status = AktivitetspliktStatus.NY.name,
        beskrivelse = "beskrivelse",
        arsaker = listOf("arsak1", "arsak2"),
        stoppunktAt = LocalDate.now(),
        updatedBy = "oppdatert av",
        sisteVurderingUuid = UUID.randomUUID(),
        sistVurdert = OffsetDateTime.now(),
        frist = LocalDate.now().plusDays(7L),
    )
