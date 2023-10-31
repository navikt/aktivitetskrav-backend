package no.nav.syfo.kafka.consumer.domain

import no.nav.syfo.kafka.domain.HendelseType
import no.nav.syfo.kafka.producer.VarselData

sealed interface VarselbusEvent {
    fun eventType(): HendelseType

    fun varselData(): VarselData?

    fun personIdent(): String
}
