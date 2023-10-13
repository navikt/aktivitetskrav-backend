package no.nav.syfo.kafka.consumer

import no.nav.syfo.kafka.config.aktivitetskravVurderingTopic
import no.nav.syfo.logger
import no.nav.syfo.service.AktivitetskravVurderingService
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.info.JavaInfo.JavaRuntimeEnvironmentInfo
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component
import kotlin.system.exitProcess

@Component
class AktivitetskravVurderingListener @Autowired constructor(
    private val aktivitetskravVurderingService: AktivitetskravVurderingService
) {

    private val log = logger()
    @KafkaListener(topics = [aktivitetskravVurderingTopic])
    fun listenToAktivitetskravVurderingTopic(
        record: ConsumerRecord<String, KAktivtetskravVurdering>,
        ack: Acknowledgment
    ) {
        log.info("Received record from topic: $aktivitetskravVurderingTopic")
        try {
            val aktivitetskravVurdering = record.value()
            aktivitetskravVurderingService.processAktivitetskravVurdering(aktivitetskravVurdering.toAktivitetskravVurdering())
            ack.acknowledge()
        } catch (e: RuntimeException) {
            log.error("Error during record processing. Shutting down application ...")
            exitProcess(1)
        }


    }
}
