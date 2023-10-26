package no.nav.syfo.service

import no.nav.syfo.api.dto.Aktivitetsplikt
import no.nav.syfo.api.dto.AktivitetspliktStatus
import no.nav.syfo.kafka.consumer.domain.KAktivitetskravVarsel
import no.nav.syfo.kafka.consumer.domain.KAktivitetskravVurdering
import no.nav.syfo.kafka.consumer.domain.toAktivitetskravVurdering
import no.nav.syfo.kafka.domain.ArbeidstakerHendelse
import no.nav.syfo.kafka.domain.HendelseType
import no.nav.syfo.kafka.producer.EsyfovarselKafkaProducer
import no.nav.syfo.logger
import no.nav.syfo.metric.Metric
import no.nav.syfo.persistence.AktivitetskravDAO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class AktivitetskravService @Autowired constructor(
    private val aktivitetskravDAO: AktivitetskravDAO,
    private val esyfovarselKafkaProducer: EsyfovarselKafkaProducer,
    private val metric: Metric,
) {
    private val log = logger()

    fun processAktivitetskravVurdering(kafkaAktivitetskravVurdering: KAktivitetskravVurdering) {
        aktivitetskravDAO.storeAktivitetkravVurdering(kafkaAktivitetskravVurdering)
        val vurdering = kafkaAktivitetskravVurdering.toAktivitetskravVurdering()
        val esyfovarselHendelse =
            getHendelseType(vurdering.status)?.let {
                ArbeidstakerHendelse(
                    type = it,
                    ferdigstill = false,
                    data = null,
                    arbeidstakerFnr = vurdering.personIdent,
                    orgnummer = null,
                )
            }

        esyfovarselKafkaProducer.sendToEsyfovarsel(esyfovarselHendelse!!)
        metric.countAktivitetskravVurderingProcessed()
    }

    fun processAktivitetskravVarsel(varsel: KAktivitetskravVarsel) {
        aktivitetskravDAO.storeAktivitetkravVarsel(varsel)
        metric.countAktivitetskravVarselProcessed()
    }

    fun getAktivitetsplikt(fnr: String): Aktivitetsplikt? {
        return aktivitetskravDAO.getAktivitetsplikt(fnr)
    }

    private fun getHendelseType(vurderingsStatus: String): HendelseType? {
        when (vurderingsStatus) {
            AktivitetspliktStatus.NY.name -> HendelseType.SM_AKTIVITETSPLIKT_STATUS_NY
            AktivitetspliktStatus.AVVENT.name -> HendelseType.AVVENT
            AktivitetspliktStatus.UNNTAK.name -> HendelseType.SM_AKTIVITETSPLIKT_STATUS_UNNTAK
            AktivitetspliktStatus.OPPFYLT.name -> HendelseType.SM_AKTIVITETSPLIKT_STATUS_OPPFYLT
            AktivitetspliktStatus.FORHANDSVARSEL.name -> HendelseType.SM_AKTIVITETSPLIKT_STATUS_FORHANDSVARSEL
            AktivitetspliktStatus.IKKE_OPPFYLT.name -> HendelseType.SM_AKTIVITETSPLIKT_STATUS_IKKE_OPPFYLT
            AktivitetspliktStatus.IKKE_AKTUELL.name -> HendelseType.SM_AKTIVITETSPLIKT_STATUS_IKKE_AKTUELL

//  NY, AVVENT, UNNTAK, OPPFYLT, FORHANDSVARSEL, IKKE_OPPFYLT, IKKE_AKTUELL
        }
        log.error("[EsyfovarselAK]: Error while mapping vurdering status [$vurderingsStatus] to hendelse type")
        return null
    }
}
