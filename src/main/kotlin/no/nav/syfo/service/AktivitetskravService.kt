package no.nav.syfo.service

import no.nav.syfo.api.dto.Aktivitetsplikt
import no.nav.syfo.kafka.consumer.domain.AktivitetskravVarselType
import no.nav.syfo.kafka.consumer.domain.KAktivitetskravVarsel
import no.nav.syfo.kafka.consumer.domain.KAktivitetskravVurdering
import no.nav.syfo.kafka.consumer.domain.VarselbusEvent
import no.nav.syfo.kafka.domain.ArbeidstakerHendelse
import no.nav.syfo.kafka.domain.HendelseType
import no.nav.syfo.kafka.producer.EsyfovarselKafkaProducer
import no.nav.syfo.metric.Metric
import no.nav.syfo.persistence.AktivitetskravDAO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service

@Profile("remote")
@Service
class AktivitetskravService @Autowired constructor(
    private val aktivitetskravDAO: AktivitetskravDAO,
    private val esyfovarselKafkaProducer: EsyfovarselKafkaProducer,
    private val metric: Metric
) {

    fun processAktivitetskravVurdering(vurdering: KAktivitetskravVurdering) {
        aktivitetskravDAO.storeAktivitetkravVurdering(vurdering)
        sendFerdigstillToVarselbus(vurdering.personIdent)
        sendMessageToVarselbus(vurdering)
        metric.countAktivitetskravVurderingProcessed()
    }

    fun processAktivitetskravVarsel(varsel: KAktivitetskravVarsel) {
        if (varsel.isForhandsvarselType()) {
            aktivitetskravDAO.storeAktivitetkravVarsel(varsel)
            sendMessageToVarselbus(varsel)
            metric.countAktivitetskravVarselProcessed()
        }
    }

    fun getAktivitetsplikt(fnr: String): Aktivitetsplikt? {
        return aktivitetskravDAO.getAktivitetsplikt(fnr)
    }

    fun sendFerdigstillToVarselbus(arbeidstakerFnr: String) {
        val esyfovarselHendelse =
            ArbeidstakerHendelse(
                type = HendelseType.SM_AKTIVITETSPLIKT,
                ferdigstill = true,
                data = null,
                arbeidstakerFnr = arbeidstakerFnr,
                orgnummer = null
            )
        esyfovarselKafkaProducer.sendToEsyfovarsel(esyfovarselHendelse)
    }

    fun sendMessageToVarselbus(event: VarselbusEvent) {
        val esyfovarselHendelse =
            ArbeidstakerHendelse(
                type = HendelseType.SM_AKTIVITETSPLIKT,
                ferdigstill = false,
                data = event.varselData(),
                arbeidstakerFnr = event.personIdent(),
                orgnummer = null
            )
        esyfovarselKafkaProducer.sendToEsyfovarsel(esyfovarselHendelse)
    }

    fun getAktivitetspliktHistorikk(fnr: String): List<Aktivitetsplikt>? {
        return aktivitetskravDAO.getHistoriskAktivitetsplikt(fnr)
    }

    private fun KAktivitetskravVarsel.isForhandsvarselType() =
        this.type == AktivitetskravVarselType.FORHANDSVARSEL_STANS_AV_SYKEPENGER.name
}
