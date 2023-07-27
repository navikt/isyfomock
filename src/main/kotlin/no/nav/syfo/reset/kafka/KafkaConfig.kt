package no.nav.syfo.reset.kafka

import no.nav.syfo.kafka.KafkaEnvironment
import no.nav.syfo.kafka.commonKafkaAivenProducerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import java.util.*

fun kafkaTestdataResetHendelseProducerConfig(
    kafkaEnvironment: KafkaEnvironment,
): Properties {
    return Properties().apply {
        putAll(commonKafkaAivenProducerConfig(kafkaEnvironment))
        this[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = String::class.java.canonicalName
    }
}
