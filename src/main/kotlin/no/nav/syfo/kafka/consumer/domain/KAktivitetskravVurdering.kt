package no.nav.syfo.kafka.consumer.domain

import no.nav.syfo.api.dto.AktivitetspliktStatus
import no.nav.syfo.kafka.producer.VarselData
import no.nav.syfo.kafka.producer.VarselDataAktivitetskrav
import java.io.Serializable
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.UUID

data class KAktivitetskravVurdering(
    val uuid: UUID,
    val personIdent: String,
    val createdAt: OffsetDateTime,
    val status: String,
    val beskrivelse: String?,
    val arsaker: List<String>,
    val stoppunktAt: LocalDate,
    val updatedBy: String?,
    val sisteVurderingUuid: UUID?,
    val sistVurdert: OffsetDateTime?,
    val frist: LocalDate?,
) : Serializable, VarselbusEvent {
    override fun personIdent() = personIdent

    override fun varselData() = VarselData(
        aktivitetskrav = VarselDataAktivitetskrav(
            sendForhandsvarsel = false,
            enableMicrofrontend = shouldEnableMikrofrontend(this),
            extendMicrofrontendDuration = shouldExtendMicrofrontendDuration(this),
        ),
    )
}

private fun shouldEnableMikrofrontend(vurdering: KAktivitetskravVurdering) =
    when (vurdering.status) {
        AktivitetspliktStatus.NY.name,
        AktivitetspliktStatus.NY_VURDERING.name,
        AktivitetspliktStatus.FORHANDSVARSEL.name,
        -> true
        else -> false
    }

private fun shouldExtendMicrofrontendDuration(vurdering: KAktivitetskravVurdering) =
    when (vurdering.status) {
        AktivitetspliktStatus.NY.name,
        AktivitetspliktStatus.NY_VURDERING.name,
        AktivitetspliktStatus.IKKE_AKTUELL.name,
        AktivitetspliktStatus.UNNTAK.name,
        AktivitetspliktStatus.IKKE_OPPFYLT.name,
        AktivitetspliktStatus.AUTOMATISK_OPPFYLT.name,
        AktivitetspliktStatus.AVVENT.name,
        AktivitetspliktStatus.OPPFYLT.name,
        AktivitetspliktStatus.FORHANDSVARSEL.name,
        -> true
        else -> false
    }
