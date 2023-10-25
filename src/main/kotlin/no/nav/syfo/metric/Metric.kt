package no.nav.syfo.metric

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tags
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class Metric @Autowired constructor(
    private val registry: MeterRegistry
) {
    fun countKafkaErrorShutdown() = countEvent("shutdown_due_to_kafka_error")

    fun countRecordReceived() = countEvent("kafka_record_received")

    fun countAktivitetskravVurderingProcessed() = countEvent("aktivitetskrav_vurdering_processed")

    fun countAktivitetskravVarselProcessed() = countEvent("aktivitetskrav_varsel_processed")

    fun countEvent(name: String) {
        registry.counter(
            metricPrefix(name),
            Tags.of("type", "info")
        ).increment()
    }

    private fun metricPrefix(name: String) =
        "aktivitetskrav_backend_$name"
}
