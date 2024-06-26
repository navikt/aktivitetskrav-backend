package no.nav.syfo

import no.nav.security.token.support.spring.api.EnableJwtTokenValidation
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.kafka.annotation.EnableKafka

@SpringBootApplication
@EnableJwtTokenValidation
@EnableKafka
class Application

fun main(args: Array<String>) {
    runApplication<Application>(args = args)
}
