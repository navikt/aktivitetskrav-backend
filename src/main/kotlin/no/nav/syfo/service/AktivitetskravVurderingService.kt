package no.nav.syfo.service

import no.nav.syfo.logger
import org.springframework.stereotype.Service

@Service
class AktivitetskravVurderingService {

    private val log = logger()

    fun processAktivitetskravVurdering(aktivitetskravVurdering: AktivitetskravVurdering) {
        log.info("Processing aktivtetskrav vurdering: ${aktivitetskravVurdering.name}")
    }

}
