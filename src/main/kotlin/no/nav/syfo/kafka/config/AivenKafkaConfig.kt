package no.nav.syfo.kafka.config

import no.nav.syfo.JacksonKafkaSerializer
import no.nav.syfo.kafka.domain.EsyfovarselHendelse
import org.apache.kafka.clients.CommonClientConfigs
import org.apache.kafka.clients.CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.config.SaslConfigs
import org.apache.kafka.common.config.SslConfigs
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.listener.ContainerProperties

const val AKTIVITETSKRAV_VARSEL_TOPIC = "teamsykefravr.aktivitetskrav-varsel"
const val AKTIVITETSKRAV_VURDERING_TOPIC = "teamsykefravr.aktivitetskrav-vurdering"
const val VARSELBUS_TOPIC = "team-esyfo.varselbus"

@Profile("remote")
@Configuration
class AivenKafkaConfig(
    @param:Value("\${KAFKA_BROKERS}") private val kafkaBrokers: String,
    @param:Value("\${KAFKA_TRUSTSTORE_PATH}") private val kafkaTruststorePath: String,
    @param:Value("\${KAFKA_KEYSTORE_PATH}") private val kafkaKeystorePath: String,
    @param:Value("\${KAFKA_CREDSTORE_PASSWORD}") private val kafkaCredstorePassword: String
) {
    private val javaKeystore = "JKS"
    private val pkcs12 = "PKCS12"
    private val ssl = "SSL"

    fun commonConfig() = mapOf(
        BOOTSTRAP_SERVERS_CONFIG to kafkaBrokers
    ) + securityConfig()

    private fun securityConfig() = mapOf(
        CommonClientConfigs.SECURITY_PROTOCOL_CONFIG to ssl,
        SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG to "", // Disable server host name verification
        SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG to javaKeystore,
        SslConfigs.SSL_KEYSTORE_TYPE_CONFIG to pkcs12,
        SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG to kafkaTruststorePath,
        SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG to kafkaCredstorePassword,
        SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG to kafkaKeystorePath,
        SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG to kafkaCredstorePassword,
        SslConfigs.SSL_KEY_PASSWORD_CONFIG to kafkaCredstorePassword
    )

    @Bean
    fun kafkaListenerContainerFactory(
        aivenKafkaErrorHandler: AivenKafkaErrorHandler
    ): ConcurrentKafkaListenerContainerFactory<String, String> {
        val config = mapOf(
            ConsumerConfig.GROUP_ID_CONFIG to "aktivitetskrav-backend-group-v2",
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "earliest",
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
            ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG to false,
            ConsumerConfig.MAX_POLL_RECORDS_CONFIG to "1",
            ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG to "600000"
        ) + commonConfig()
        val consumerFactory = DefaultKafkaConsumerFactory<String, String>(config)

        val factory = ConcurrentKafkaListenerContainerFactory<String, String>()
        factory.consumerFactory = consumerFactory
        factory.setCommonErrorHandler(aivenKafkaErrorHandler)
        factory.containerProperties.ackMode = ContainerProperties.AckMode.MANUAL_IMMEDIATE
        return factory
    }

    @Bean("EsyfovarselProducerFactory")
    fun producerFactory(): ProducerFactory<String, EsyfovarselHendelse> = DefaultKafkaProducerFactory(
        commonConfig() +
            mutableMapOf(
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to JacksonKafkaSerializer::class.java,
                ProducerConfig.ACKS_CONFIG to "all",
                ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG to false
            ).apply {
                remove(SaslConfigs.SASL_MECHANISM)
                remove(SaslConfigs.SASL_JAAS_CONFIG)
            }.toMap()
    )

    @Bean("EsyfovarselKafkaTemplate")
    fun kafkaTemplate(
        @Qualifier("EsyfovarselProducerFactory") producerFactory: ProducerFactory<String, EsyfovarselHendelse>
    ): KafkaTemplate<String, EsyfovarselHendelse> = KafkaTemplate(producerFactory)
}
