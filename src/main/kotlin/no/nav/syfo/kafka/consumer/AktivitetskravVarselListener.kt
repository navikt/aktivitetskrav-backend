package no.nav.syfo.kafka.consumer

import com.fasterxml.jackson.module.kotlin.readValue
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
): AktivitetkravListener() {
    @KafkaListener(topics = [aktivitetskravVarselTopic])
    override fun listenToTopic(
        record: ConsumerRecord<String, String>,
        ack: Acknowledgment
    ) {
        log.info("Received record from topic: $aktivitetskravVarselTopic")
        try {
            val aktivitetskravVarsel: KAktivitetskravVarsel = objectMapper.readValue(record.value())
            aktivitetskravService.processAktivitetskravVarsel(aktivitetskravVarsel.toAktivitetskravVarsel())
            ack.acknowledge()
        } catch (e: RuntimeException) {
            log.error("Error during record processing (VARSEL). Shutting down application ...", e)
            exitProcess(1)
        }
    }
}
