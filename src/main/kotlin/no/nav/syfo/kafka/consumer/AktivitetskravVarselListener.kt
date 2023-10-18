package no.nav.syfo.kafka.consumer

import no.nav.syfo.kafka.config.aktivitetskravVarselTopic
import no.nav.syfo.kafka.consumer.domain.KAktivitetskravVarsel
import no.nav.syfo.kafka.consumer.domain.toAktivitetskravVarsel
import no.nav.syfo.logger
import no.nav.syfo.service.AktivitetskravService
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component
import kotlin.system.exitProcess

@Component
class AktivitetskravVarselListener @Autowired constructor(
    private val aktivitetskravService: AktivitetskravService
) {

    private val log = logger()

    @KafkaListener(topics = [aktivitetskravVarselTopic])
    fun listenToAktivitetskravVurderingTopic(
        record: ConsumerRecord<String, KAktivitetskravVarsel>,
        ack: Acknowledgment
    ) {
        log.info("Received record from topic: $aktivitetskravVarselTopic")
        try {
            val aktivitetskravVurdering = record.value()
            aktivitetskravService.processAktivitetskravVarsel(aktivitetskravVurdering.toAktivitetskravVarsel())
            ack.acknowledge()
        } catch (e: RuntimeException) {
            log.error("Error during record processing. Shutting down application ...")
            exitProcess(1)
        }
    }
}
