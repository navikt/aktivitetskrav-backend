package no.nav.syfo.kafka.consumer

import no.nav.syfo.exception.FailedSendingToEsyfovarselException
import no.nav.syfo.kafka.config.AKTIVITETSKRAV_VARSEL_TOPIC
import no.nav.syfo.kafka.config.AKTIVITETSKRAV_VURDERING_TOPIC
import no.nav.syfo.kafka.consumer.domain.KAktivitetskravVarsel
import no.nav.syfo.kafka.consumer.domain.KAktivitetskravVurdering
import no.nav.syfo.logger
import no.nav.syfo.metric.Metric
import no.nav.syfo.service.AktivitetskravService
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component
import tools.jackson.databind.DeserializationFeature
import tools.jackson.databind.ObjectMapper
import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.kotlin.KotlinModule
import java.io.IOException
import kotlin.system.exitProcess

@Profile("remote")
@Component
class AktivitetskravKafkaListener @Autowired constructor(
    private val aktivitetskravService: AktivitetskravService,
    private val metric: Metric
) {
    val log = logger()

    private val objectMapper: ObjectMapper = JsonMapper.builder()
        .addModule(KotlinModule.Builder().build())
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .build()

    @KafkaListener(topics = [AKTIVITETSKRAV_VARSEL_TOPIC, AKTIVITETSKRAV_VURDERING_TOPIC])
    fun listenToTopic(record: ConsumerRecord<String, String>, ack: Acknowledgment) {
        val topic = record.topic()
        metric.countRecordReceived()
        try {
            when (topic) {
                AKTIVITETSKRAV_VURDERING_TOPIC -> {
                    val vurdering = objectMapper.readValue(record.value(), KAktivitetskravVurdering::class.java)
                    aktivitetskravService.processAktivitetskravVurdering(vurdering)
                }

                AKTIVITETSKRAV_VARSEL_TOPIC -> {
                    val varsel = objectMapper.readValue(record.value(), KAktivitetskravVarsel::class.java)
                    aktivitetskravService.processAktivitetskravVarsel(varsel)
                }

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
