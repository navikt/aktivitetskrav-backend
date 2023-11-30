package no.nav.syfo

import no.nav.syfo.kafka.config.aktivitetskravVarselTopic
import no.nav.syfo.kafka.config.aktivitetskravVurderingTopic
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.test.context.EmbeddedKafka

@SpringBootApplication
@EnableKafka
@EmbeddedKafka(
    partitions = 1,
    topics = [
        aktivitetskravVarselTopic,
        aktivitetskravVurderingTopic,
    ],
)
class LocalApplication {
    fun main(args: Array<String>) {
        runApplication<LocalApplication>(*args)
    }
}
