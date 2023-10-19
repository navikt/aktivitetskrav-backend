package no.nav.syfo.persistence

import no.nav.syfo.kafka.consumer.domain.DocumentComponentDTO
import java.sql.Date
import java.sql.Timestamp
import java.time.LocalDate
import java.time.OffsetDateTime

fun List<String>.toStr() = this.joinToString(separator = ",").trim()

fun List<String>.toJsonArray() = this.joinToString(prefix = "[\"", postfix = "\"]", separator = "\",\"")

fun LocalDate.toDate() = Date.valueOf(this)

fun OffsetDateTime.toTimestamp() = Timestamp.valueOf(this.toLocalDateTime())

fun List<DocumentComponentDTO>.documentsToStr() = """
        {
            ${this.joinToString(prefix = "{", postfix = "}", separator = ",") { it.documentToStr() }}
        }
""".trimIndent()

fun DocumentComponentDTO.documentToStr() = """
        "type": "${this.type}",
        "key": "${this.key}",
        "title": "${this.title}",
        "texts": ${this.texts.toJsonArray()}
""".trimIndent()
