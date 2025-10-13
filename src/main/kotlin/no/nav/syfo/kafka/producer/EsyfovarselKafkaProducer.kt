package no.nav.syfo.kafka.producer

import no.nav.syfo.exception.FailedSendingToEsyfovarselException
import no.nav.syfo.kafka.config.VARSELBUS_TOPIC
import no.nav.syfo.kafka.domain.EsyfovarselHendelse
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Profile
import org.springframework.kafka.KafkaException
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import java.util.*
import java.util.concurrent.ExecutionException

@Profile("remote")
@Component
class EsyfovarselKafkaProducer @Autowired constructor(
    @param:Qualifier("EsyfovarselKafkaTemplate") private val kafkaTemplate: KafkaTemplate<String, EsyfovarselHendelse>
) {

    fun sendToEsyfovarsel(esyfovarselHendelse: EsyfovarselHendelse) {
        try {
            kafkaTemplate.send(
                ProducerRecord(
                    VARSELBUS_TOPIC,
                    UUID.randomUUID().toString(),
                    esyfovarselHendelse
                )
            ).get()
        } catch (e: ExecutionException) {
            log.error(
                "ExecutionException was thrown when attempting to send varsel to esyfovarsel. ${e.message}"
            )
            throw FailedSendingToEsyfovarselException(e)
        } catch (e: KafkaException) {
            log.error(
                "KafkaException was thrown when attempting to send varsel to esyfovarsel. ${e.message}"
            )
            throw FailedSendingToEsyfovarselException(e)
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(EsyfovarselKafkaProducer::class.java)
    }
}
