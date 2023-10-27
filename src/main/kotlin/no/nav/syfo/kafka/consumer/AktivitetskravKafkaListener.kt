package no.nav.syfo.kafka.consumer

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import no.nav.syfo.kafka.config.aktivitetskravVarselTopic
import no.nav.syfo.kafka.config.aktivitetskravVurderingTopic
import no.nav.syfo.logger
import no.nav.syfo.metric.Metric
import no.nav.syfo.service.AktivitetskravService
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component
import kotlin.system.exitProcess

@Component
class AktivitetskravKafkaListener @Autowired constructor(
    private val aktivitetskravService: AktivitetskravService,
    private val metric: Metric,
) {
    val log = logger()

    val objectMapper = ObjectMapper().apply {
        registerKotlinModule()
        registerModule(JavaTimeModule())
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    @KafkaListener(topics = [aktivitetskravVarselTopic, aktivitetskravVurderingTopic])
    fun listenToTopic(
        record: ConsumerRecord<String, String>,
        ack: Acknowledgment,
    ) {
        val topic = record.topic()
        metric.countRecordReceived()
        log.info("[EsyfovarselAK] Received record from topic $topic")
        try {
            when (topic) {
                aktivitetskravVurderingTopic ->
                    aktivitetskravService.processAktivitetskravVurdering(objectMapper.readValue(record.value()))
                aktivitetskravVarselTopic ->
                    aktivitetskravService.processAktivitetskravVarsel(objectMapper.readValue(record.value()))
                else ->
                    throw IllegalArgumentException("Received record from topic not in the subscription list: $topic")
            }

            ack.acknowledge()
        } catch (e: RuntimeException) {
            log.error("[EsyfovarselAK] Error during record processing from topic $topic. Shutting down application ...", e)
            metric.countKafkaErrorShutdown()
            exitProcess(1)
        }
    }
}
