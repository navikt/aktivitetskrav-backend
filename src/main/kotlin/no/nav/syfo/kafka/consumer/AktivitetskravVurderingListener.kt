package no.nav.syfo.kafka.consumer

import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.syfo.kafka.config.aktivitetskravVurderingTopic
import no.nav.syfo.kafka.consumer.domain.KAktivitetskravVurdering
import no.nav.syfo.kafka.consumer.domain.toAktivitetskravVurdering
import no.nav.syfo.logger
import no.nav.syfo.objectMapper
import no.nav.syfo.service.AktivitetskravService
import no.nav.syfo.service.AktivitetskravVurdering
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component
import kotlin.system.exitProcess

@Component
class AktivitetskravVurderingListener @Autowired constructor(
    private val aktivitetskravService: AktivitetskravService
) : AktivitetkravListener() {
    override fun listenToTopic(
        record: ConsumerRecord<String, String>,
        ack: Acknowledgment
    ) {
        log.info("Received record from topic: $aktivitetskravVurderingTopic")
        try {
            val aktivitetskravVurdering: KAktivitetskravVurdering = objectMapper.readValue(record.value())
            aktivitetskravService.processAktivitetskravVurdering(aktivitetskravVurdering.toAktivitetskravVurdering())
            ack.acknowledge()
        } catch (e: RuntimeException) {
            log.error("Error during record processing (VURDERING). Shutting down application ...", e)
            exitProcess(1)
        }
    }
}
