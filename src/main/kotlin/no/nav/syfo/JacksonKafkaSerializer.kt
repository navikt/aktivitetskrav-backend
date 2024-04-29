package no.nav.syfo

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.apache.kafka.common.serialization.Serializer

val objectMapper: ObjectMapper = JsonMapper.builder()
    .addModule(JavaTimeModule())
    .addModule(KotlinModule.Builder().build())
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
    .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true)
    .build()

@Suppress("EmptyFunctionBlock")
class JacksonKafkaSerializer : Serializer<Any> {
    override fun serialize(topic: String?, data: Any?): ByteArray? = objectMapper.writeValueAsBytes(data)
    override fun close() {}
}
