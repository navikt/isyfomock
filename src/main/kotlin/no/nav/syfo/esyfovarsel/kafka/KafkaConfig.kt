package no.nav.syfo.esyfovarsel.kafka

import no.nav.syfo.kafka.KafkaEnvironment
import no.nav.syfo.kafka.commonKafkaAivenConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import java.util.*

fun kafkaEsyfovarselHendelseProducerConfig(
    kafkaEnvironment: KafkaEnvironment,
): Properties {
    return Properties().apply {
        putAll(commonKafkaAivenConfig(kafkaEnvironment))
        this[ProducerConfig.ACKS_CONFIG] = "all"
        this[ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG] = "true"
        this[ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION] = "1"
        this[ProducerConfig.MAX_BLOCK_MS_CONFIG] = "15000"
        this[ProducerConfig.RETRIES_CONFIG] = "100000"
        this[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java.canonicalName
        this[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = KafkaEsyfovarselHendelseSerializer::class.java.canonicalName
    }
}
