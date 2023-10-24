package no.nav.syfo.service

import no.nav.syfo.api.dto.Aktivitetsplikt
import no.nav.syfo.kafka.consumer.domain.KAktivitetskravVarsel
import no.nav.syfo.kafka.consumer.domain.KAktivitetskravVurdering
import no.nav.syfo.logger
import no.nav.syfo.metric.Metric
import no.nav.syfo.persistence.AktivitetskravDAO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class AktivitetskravService @Autowired constructor(
    private val aktivitetskravDAO: AktivitetskravDAO,
    private val metric: Metric
) {

    private val log = logger()

    fun processAktivitetskravVurdering(vurdering: KAktivitetskravVurdering) {
        aktivitetskravDAO.storeAktivitetkravVurdering(vurdering)
        metric.countAktivitetskravVurderingProcessed()
    }

    fun processAktivitetskravVarsel(varsel: KAktivitetskravVarsel) {
        aktivitetskravDAO.storeAktivitetkravVarsel(varsel)
        metric.countAktivitetskravVarselProcessed()
    }

    fun getAktivitetsplikt(fnr: String): Aktivitetsplikt? {
        return aktivitetskravDAO.getAktivitetsplikt(fnr)
    }
}
