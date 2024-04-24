package no.nav.syfo

import no.nav.syfo.kafka.config.AKTIVITETSKRAV_VARSEL_TOPIC
import no.nav.syfo.kafka.config.AKTIVITETSKRAV_VURDERING_TOPIC
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.test.context.EmbeddedKafka

@SpringBootApplication
@EnableKafka
@EmbeddedKafka(
    partitions = 1,
    topics = [
        AKTIVITETSKRAV_VARSEL_TOPIC,
        AKTIVITETSKRAV_VURDERING_TOPIC
    ]
)
class LocalApplication {
    fun main(args: Array<String>) {
        runApplication<LocalApplication>(*args)
    }
}
