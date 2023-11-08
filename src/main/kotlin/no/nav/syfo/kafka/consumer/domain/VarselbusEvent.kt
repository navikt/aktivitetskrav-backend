package no.nav.syfo.kafka.consumer.domain

import no.nav.syfo.kafka.producer.VarselData

sealed interface VarselbusEvent {
    fun varselData(): VarselData?

    fun personIdent(): String
}
