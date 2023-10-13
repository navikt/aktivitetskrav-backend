package no.nav.syfo.kafka.consumer

import no.nav.syfo.service.AktivitetskravVurdering

data class KAktivtetskravVurdering(
    val name: String,
    val age: Int
)

fun KAktivtetskravVurdering.toAktivitetskravVurdering() =
    AktivitetskravVurdering(
        this.name,
        this.age
    )
