package no.nav.syfo.service

import no.nav.syfo.logger

class AktivitetskravVurderingService {

    private val log = logger()

    fun processAktivitetskravVurdering(aktivitetskravVurdering: AktivitetskravVurdering) {
        log.info("Processing aktivtetskrav vurdering: ${aktivitetskravVurdering.name}")
    }

}
