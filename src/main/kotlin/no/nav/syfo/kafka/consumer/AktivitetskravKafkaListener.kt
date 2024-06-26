package no.nav.syfo.kafka.consumer

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import no.nav.syfo.exception.FailedSendingToEsyfovarselException
import no.nav.syfo.kafka.config.AKTIVITETSKRAV_VARSEL_TOPIC
import no.nav.syfo.kafka.config.AKTIVITETSKRAV_VURDERING_TOPIC
import no.nav.syfo.logger
import no.nav.syfo.metric.Metric
import no.nav.syfo.service.AktivitetskravService
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component
import java.io.IOException
import kotlin.system.exitProcess

@Profile("remote")
@Component
class AktivitetskravKafkaListener @Autowired constructor(
    private val aktivitetskravService: AktivitetskravService,
    private val metric: Metric
) {
    val log = logger()

    val objectMapper = ObjectMapper().apply {
        registerKotlinModule()
        registerModule(JavaTimeModule())
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    @KafkaListener(topics = [AKTIVITETSKRAV_VARSEL_TOPIC, AKTIVITETSKRAV_VURDERING_TOPIC])
    fun listenToTopic(
        record: ConsumerRecord<String, String>,
        ack: Acknowledgment
    ) {
        val topic = record.topic()
        metric.countRecordReceived()
        try {
            when (topic) {
                AKTIVITETSKRAV_VURDERING_TOPIC ->
                    aktivitetskravService.processAktivitetskravVurdering(objectMapper.readValue(record.value()))

                AKTIVITETSKRAV_VARSEL_TOPIC ->
                    aktivitetskravService.processAktivitetskravVarsel(objectMapper.readValue(record.value()))

                else ->
                    throw IllegalArgumentException("Received record from topic not in the subscription list: $topic")
            }

            ack.acknowledge()
        } catch (e: IOException) {
            log.error(
                "IOException during record processing from topic $topic. Shutting down application ...",
                e
            )
            metric.countKafkaErrorShutdown()
            exitProcess(1)
        } catch (e: IllegalArgumentException) {
            log.error(
                "IllegalArgumentException during record processing from topic $topic. Shutting down application ...",
                e
            )
            metric.countKafkaErrorShutdown()
            exitProcess(1)
        } catch (e: FailedSendingToEsyfovarselException) {
            log.error(
                "FailedSendingToEsyfovarselException during record processing from " +
                    "topic $topic. Shutting down application ...",
                e
            )
            metric.countKafkaErrorShutdown()
            exitProcess(1)
        }
    }
}
