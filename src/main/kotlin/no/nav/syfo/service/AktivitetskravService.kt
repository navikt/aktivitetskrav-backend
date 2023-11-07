package no.nav.syfo.service

import no.nav.syfo.api.dto.Aktivitetsplikt
import no.nav.syfo.kafka.consumer.domain.KAktivitetskravVarsel
import no.nav.syfo.kafka.consumer.domain.KAktivitetskravVurdering
import no.nav.syfo.kafka.consumer.domain.VarselbusEvent
import no.nav.syfo.kafka.domain.ArbeidstakerHendelse
import no.nav.syfo.kafka.producer.EsyfovarselKafkaProducer
import no.nav.syfo.logger
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
    private val log = logger()

    fun processAktivitetskravVurdering(vurdering: KAktivitetskravVurdering) {
        aktivitetskravDAO.storeAktivitetkravVurdering(vurdering)
        sendMessageToVarselbus(vurdering)
        metric.countAktivitetskravVurderingProcessed()
    }

    fun processAktivitetskravVarsel(varsel: KAktivitetskravVarsel) {
        aktivitetskravDAO.storeAktivitetkravVarsel(varsel)
        sendMessageToVarselbus(varsel)
        metric.countAktivitetskravVarselProcessed()
    }

    fun getAktivitetsplikt(fnr: String): Aktivitetsplikt? {
        return aktivitetskravDAO.getAktivitetsplikt(fnr)
    }

    fun sendMessageToVarselbus(event: VarselbusEvent) {
        val esyfovarselHendelse =
            ArbeidstakerHendelse(
                type = event.eventType(),
                ferdigstill = false,
                data = event.varselData(),
                arbeidstakerFnr = event.personIdent(),
                orgnummer = null
            )
        esyfovarselKafkaProducer.sendToEsyfovarsel(esyfovarselHendelse)
    }
}
