package no.nav.syfo

import no.nav.syfo.kafka.config.aktivitetskravVarselTopic
import no.nav.syfo.kafka.config.aktivitetskravVurderingTopic
import no.nav.syfo.testutil.EmbeddedDatabase
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.test.context.EmbeddedKafka

@SpringBootApplication
@EnableKafka
@EmbeddedKafka(
    partitions = 1,
    topics = [
        aktivitetskravVarselTopic,
        aktivitetskravVurderingTopic
    ]
)
class LocalApplication {
    fun main(args: Array<String>) {
        lateinit var pg: EmbeddedDatabase

        @BeforeAll
        fun setupDb() {
            pg = EmbeddedDatabase()
        }

        @AfterAll
        fun teardown() {
            pg.stop()
        }

        val app = SpringApplication(LocalApplication::class.java)
        app.run(*args)
    }
}
