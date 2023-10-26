package no.nav.syfo.kafka.domain

import com.fasterxml.jackson.annotation.JsonTypeInfo
import java.io.Serializable

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
sealed interface EsyfovarselHendelse : Serializable {
    val type: HendelseType
    val ferdigstill: Boolean?
    var data: Any?
}

data class ArbeidstakerHendelse(
    override val type: HendelseType,
    override val ferdigstill: Boolean?,
    override var data: Any?,
    val arbeidstakerFnr: String,
    val orgnummer: String?,
) : EsyfovarselHendelse

enum class HendelseType {
    SM_AKTIVITETSPLIKT_STATUS_FORHANDSVARSEL,
    SM_AKTIVITETSPLIKT_STATUS_NY,
    SM_AKTIVITETSPLIKT_STATUS_UNNTAK, // TODO: add others
    SM_AKTIVITETSPLIKT_STATUS_OPPFYLT,
    SM_AKTIVITETSPLIKT_STATUS_IKKE_OPPFYLT,
    SM_AKTIVITETSPLIKT_STATUS_IKKE_AKTUELL,
}
