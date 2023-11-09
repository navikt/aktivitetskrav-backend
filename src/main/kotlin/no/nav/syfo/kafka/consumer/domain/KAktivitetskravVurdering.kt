package no.nav.syfo.kafka.consumer.domain

import no.nav.syfo.api.dto.AktivitetspliktStatus
import no.nav.syfo.kafka.domain.HendelseType
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
    override fun eventType() = getHendelseType(this)
    override fun personIdent() = personIdent

    override fun varselData() = null
}

private fun getHendelseType(vurdering: KAktivitetskravVurdering): HendelseType {
    return when (vurdering.status) {
        AktivitetspliktStatus.NY.name -> HendelseType.SM_AKTIVITETSPLIKT_STATUS_NY
        AktivitetspliktStatus.NY_VURDERING.name -> HendelseType.SM_AKTIVITETSPLIKT_STATUS_NY_VURDERING
        AktivitetspliktStatus.AVVENT.name -> HendelseType.SM_AKTIVITETSPLIKT_STATUS_AVVENT
        AktivitetspliktStatus.UNNTAK.name -> HendelseType.SM_AKTIVITETSPLIKT_STATUS_UNNTAK
        AktivitetspliktStatus.OPPFYLT.name -> HendelseType.SM_AKTIVITETSPLIKT_STATUS_OPPFYLT
        AktivitetspliktStatus.AUTOMATISK_OPPFYLT.name -> HendelseType.SM_AKTIVITETSPLIKT_STATUS_AUTOMATISK_OPPFYLT
        AktivitetspliktStatus.FORHANDSVARSEL.name -> HendelseType.SM_AKTIVITETSPLIKT_STATUS_FORHANDSVARSEL
        AktivitetspliktStatus.IKKE_OPPFYLT.name -> HendelseType.SM_AKTIVITETSPLIKT_STATUS_IKKE_OPPFYLT
        AktivitetspliktStatus.IKKE_AKTUELL.name -> HendelseType.SM_AKTIVITETSPLIKT_STATUS_IKKE_AKTUELL
        else -> throw IllegalArgumentException("Ukjent AktivitetspliktStatus-type: ${vurdering.status}")
    }
}
