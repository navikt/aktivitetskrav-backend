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
        log.info("[EsyfovarselAK] processAktivitetskravVurdering record")
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
        if (vurderingsStatus.equals(AktivitetspliktStatus.NY.name, true)) {
            return HendelseType.SM_AKTIVITETSPLIKT_STATUS_NY
        } else if (vurderingsStatus.equals(AktivitetspliktStatus.AVVENT.name, true)) {
            return HendelseType.AVVENT
        } else if (vurderingsStatus.equals(AktivitetspliktStatus.UNNTAK.name, true)) {
            return HendelseType.SM_AKTIVITETSPLIKT_STATUS_UNNTAK
        } else if (vurderingsStatus.equals(AktivitetspliktStatus.OPPFYLT.name, true)) {
            return HendelseType.SM_AKTIVITETSPLIKT_STATUS_OPPFYLT
        } else if (vurderingsStatus.equals(AktivitetspliktStatus.FORHANDSVARSEL.name, true)) {
            return HendelseType.SM_AKTIVITETSPLIKT_STATUS_FORHANDSVARSEL
        } else if (vurderingsStatus.equals(AktivitetspliktStatus.IKKE_OPPFYLT.name, true)) {
            return HendelseType.SM_AKTIVITETSPLIKT_STATUS_IKKE_OPPFYLT
        } else if (vurderingsStatus.equals(AktivitetspliktStatus.IKKE_AKTUELL.name, true)) {
            return HendelseType.SM_AKTIVITETSPLIKT_STATUS_IKKE_AKTUELL
        }
        log.error("[EsyfovarselAK]: Error while mapping vurdering status [$vurderingsStatus] to hendelse type")
        log.error("[EsyfovarselAK]: Error while mapping AktivitetspliktStatus [${ AktivitetspliktStatus.OPPFYLT.name}] to hendelse type")
        return null
    }
}
