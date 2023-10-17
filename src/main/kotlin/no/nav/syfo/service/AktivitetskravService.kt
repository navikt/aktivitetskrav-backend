package no.nav.syfo.service

import no.nav.syfo.logger
import org.springframework.stereotype.Service

@Service
class AktivitetskravService {

    private val log = logger()

    fun processAktivitetskravVurdering(aktivitetskravVurdering: AktivitetskravVurdering) {
        log.info("Processing aktivtetskrav vurdering: ${aktivitetskravVurdering.uuid}")
    }

    fun processAktivitetskravVarsel(aktivitetskravVarsel: AktivitetskravVarsel) {
        log.info("Processing aktivtetskrav vurdering: ${aktivitetskravVarsel.aktivitetskravUuid}")
    }
}
