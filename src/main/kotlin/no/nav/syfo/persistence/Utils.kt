package no.nav.syfo.persistence

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import no.nav.syfo.kafka.consumer.domain.DocumentComponentDTO
import java.sql.Date
import java.sql.Timestamp
import java.time.LocalDate
import java.time.OffsetDateTime

val jsonWriter = ObjectMapper().apply {
    registerKotlinModule()
    registerModule(JavaTimeModule())
    configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
}
fun List<String>.toStr() = this.joinToString(separator = ",").trim()

fun LocalDate.toDate() = Date.valueOf(this)

fun OffsetDateTime.toTimestamp() = Timestamp.valueOf(this.toLocalDateTime())

fun List<DocumentComponentDTO>.documentsToStr() = jsonWriter.writeValueAsString(this)
