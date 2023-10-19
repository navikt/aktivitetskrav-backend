package no.nav.syfo.service

import no.nav.syfo.kafka.consumer.domain.KAktivitetskravVarsel
import no.nav.syfo.kafka.consumer.domain.KAktivitetskravVurdering
import no.nav.syfo.logger
import no.nav.syfo.persistence.AktivitetskravDAO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class AktivitetskravService @Autowired constructor(
    private val aktivitetskravDAO: AktivitetskravDAO
) {

    private val log = logger()

    fun processAktivitetskravVurdering(vurdering: KAktivitetskravVurdering) {
        log.info("Processing aktivitetskrav vurdering: ${vurdering.uuid}")
        aktivitetskravDAO.storeAktivitetkravVurdering(vurdering)
    }

    fun processAktivitetskravVarsel(varsel: KAktivitetskravVarsel) {
        log.info("Processing aktivitetskrav varsel: ${varsel.aktivitetskravUuid}")
        aktivitetskravDAO.storeAktivitetkravVarsel(varsel)
    }
}
