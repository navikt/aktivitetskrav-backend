package no.nav.syfo.kafka.producer

import no.nav.syfo.kafka.config.varselBusTopic
import no.nav.syfo.kafka.domain.EsyfovarselHendelse
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Profile
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import java.util.*

@Profile("remote")
@Component
class EsyfovarselKafkaProducer @Autowired constructor(
    @Qualifier("EsyfovarselKafkaTemplate") private val kafkaTemplate: KafkaTemplate<String, EsyfovarselHendelse>
) {

    fun sendToEsyfovarsel(
        esyfovarselHendelse: EsyfovarselHendelse
    ) {
        try {
            kafkaTemplate.send(
                ProducerRecord(
                    varselBusTopic,
                    UUID.randomUUID().toString(),
                    esyfovarselHendelse
                )
            ).get()
        } catch (e: Exception) {
            log.error("[EsyfovarselAK]: Exception was thrown when attempting to send varsel to esyfovarsel. ${e.message}")
            throw e
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(EsyfovarselKafkaProducer::class.java)
    }
}
